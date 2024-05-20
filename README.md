# Tourist attractions bot
Bot for finding the nearest tourist places to your location.
Attractions are loaded from https://www.openstreetmap.org/.

1. start    
![img.png](bot_cleint_ex%2Fimg.png)     
2. send location     
![img_1.png](bot_cleint_ex%2Fimg_1.png)     
3. choose tourism places     
![img_3.jpg](bot_cleint_ex%2Fimg_3.jpg)     
4. press load more     
![img_3.png](bot_cleint_ex%2Fimg_3.png)  

## Search engine     
QuadTree algorithm is used to find nearest places.   
https://en.wikipedia.org/wiki/Quadtree     
```text    
com.tourist_bot.quad.QuadTree
```

## Create bot
https://core.telegram.org/bots/tutorial    
## Telegram Bot modes

Bot can be started in 2 modes:

### LongPolling (pull mechanism).

Server periodically sends a request to Telegram, asking for new updates and keeps connection some time alive.    
After some time (30s) if no updates exist, Telegram will return an empty list, meaning that no new messages were   
sent to the bot since the last request was sent. Server sends a request again.

```mermaid
sequenceDiagram
    Server bot ->> Telegram: (pull) any updates
    Telegram -->> Server bot: nope
    Server bot ->> Telegram: (pull) any updates
    Telegram -->> Server bot: nope
    Server bot ->> Telegram: (pull) any updates
    Telegram ->> Server bot: YES
```

### WebHook (push mechanism).

Instead of asking Telegram for the updates, Telegram will immediately send all updates.

```mermaid
sequenceDiagram
    Server bot ->> Telegram: set up web hook connection
    Telegram ->> Server bot: (push) new updates
    Server bot -->> Telegram: received
    Telegram ->> Server bot: (push) new updates
    Server bot -->> Telegram: received
```

## System design

### Long Polling

![](./draw_io/long_polling_design.png)

Since Router doesn't need any configuration we can skip communication with it

```mermaid
sequenceDiagram
%%    <br/> 149.154.160.0/20 <br/> 91.108.4.0/22
    participant client as Mobile Client
    participant telegram as Telegram
    participant bot as Server bot
    participant redis as Redis
    bot ->> telegram: (pull) any updates
    telegram -->> bot: nope
    client ->> telegram: new user
    client ->> telegram: send location
    bot ->> telegram: (pull) <br/> any updates
    telegram ->> bot: ['new user',<br/> 'location']
    bot ->> redis: get or create <br/> session
    redis ->> bot: new session
    bot ->> telegram: send hello
    telegram ->> client: send hello
    bot ->> redis: update session
    bot ->> telegram: attractions for <br/> this location
    bot ->> redis: update session
    telegram ->> client: attractions for <br/> this location
    bot ->> telegram: (pull) any updates
    telegram -->> bot: nope
```

### Web Hook

![](./draw_io/web_hook.png)

```mermaid
sequenceDiagram
%%    <br/> 149.154.160.0/20 <br/> 91.108.4.0/22
    participant client as Mobile Client
    participant telegram as Telegram
    participant router as Router
    Note over router: Incoming packet with <br/> destination port (8443) <br/> is translated to a packet <br/> different destination <br/> port (8070)
    participant nginx as Nginx <br/> Round Robin
    participant bot1 as Server bot (1) <br/> 11.6.0.3
    participant bot2 as Server bot (2) <br/> 11.6.0.4
    participant redis as Redis
    bot1 ->> router: set webHook <br/> Router Public IP:Port <br/> (204.66.107.144:8443)
    router ->> telegram: set webHook <br/> Router Public IP:Port <br/> (204.66.107.144:8443)
    bot2 ->> router: set webHook <br/> Router Public IP:Port <br/> (204.66.107.144:8443)
    router ->> telegram: set webHook <br/> Router Public IP:Port <br/> (204.66.107.144:8443)
    client ->> telegram: new user
    telegram ->> router: 'new user'
    router ->> nginx: 'new user'
    nginx ->> bot1: 'new user'
    bot1 ->> redis: get or create <br/> session
    redis ->> bot1: new session
    bot1 ->> nginx: send hello
    nginx ->> router: send hello
    router ->> telegram: send hello
    telegram ->> client: send hello
    bot1 ->> redis: update session
    client ->> telegram: send location
    telegram ->> router: 'location'
    router ->> nginx: 'location'
    nginx ->> bot2: 'location'
    bot2 ->> redis: get or create <br/> session
    redis ->> bot2: return session
    bot2 ->> nginx: attractions for <br/> this location
    nginx ->> router: attractions for <br/> this location
    router ->> telegram: attractions for <br/> this location
    bot2 ->> redis: update session
    telegram ->> client: attractions for <br/> this location
```

# How to use

## Data preparation

1) Go to   
   https://overpass-turbo.eu/#
2) Select an area    
   ![](readme_pngs/img1.png)

3) copy code block to the left part and press 'run'

   ```text
   [out:json][timeout:25];
   // gather results
   (
   // query part for: “tourism=*”
   node["tourism"]({{bbox}});
   way["tourism"]({{bbox}});
   relation["tourism"]({{bbox}});
   );
   // print results
   out body;
   >;
   out skel qt;
   ```
   ![](readme_pngs/img2.png)

4) All found points are marked on the map    
   ![](readme_pngs/img3.png)

5) Press 'export' and select 'GeoJSON' to download data     
   ![](readme_pngs/img4.png)
6) Use <b>com.tourist_bot.geo.TouristAttractionsDataPreparation</b> class to filter out incorrect data    
   and select only required data.
```java

        String inputFilePath = "";
        String outputFilePath = "";

        String jsonStr = Files.readString(Path.of(inputFilePath)).replace("\u00a0", "");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonStr);
        JsonNode jsonArrRaw = jsonNode.get("features");

        ArrayList<JsonNode> jsonArrayFiltered = filterTouristAttractions(jsonArrRaw.iterator());
        System.out.println(jsonArrayFiltered.size());

        ArrayList<TouristAttraction> touristAttractions = mapToTouristAttraction(jsonArrayFiltered);

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileWriter(outputFilePath), touristAttractions);

```

## Running bot in Intellij Idea

1) If you want to use redis as a cache service.   
   Go to dir 'local_redis' and run 'docker compose up -d'


2) Edit file 'src/main/resources/bot_conf.yaml'

- set 'app.token' with generated token
- set 'app.geoJsonPath' with generated json by class 'com.tourist_bot.geo.TouristAttractionsDataPreparation'
- set 'app.sessionManager' EMBEDDED or REDIS
- set other settings if prefer

### LongPolling mode

Run <b>com.tourist_bot.bot.LongTouristBotApp</b>

### WebHook
To run bot in a web hook mode server should be accessible from the internet.     
We can achieve it by several ways.   
#### Using Ngrok as public IP

Ngrok is a globally distributed reverse proxy that allows to expose locally hosted server to the internet.   
![](./draw_io/web_hook_ngrok.png)

1) install https://ngrok.com/download
2) open console and run 
   ```shell
   ngrok http 8080 --authtoken <token>
   ```
   copy 'Forwarding host'    
   ![](readme_pngs/img5.png)     
   webHook:
   Paste  'Forwarding host' to config variable 'webHook.publicHostWebHookUrl'.   
   Add port '443' at the end. For example 'https://4a95-46-229-218-226.ngrok.io:443'.   
   Webhook can be set up only on ports [80, 88, 443 or 8443]       
3) Set config 'webHook.localHostWebHookUrl' as 'http://0.0.0.0:8080'. Only HTTP since ssl is not required with Ngrok     
4) Run com.tourist_bot.bot.WebHookTouristBotApp    
Check host address http instead of https if:     
```text
Exception in thread "main" java.lang.IllegalArgumentException: Local host is 'HTTPS': 'https://0.0.0.0:8080'. Conf 'webhook.keyStorePath' cannot be null. Or change to 'HTTP'.
	at com.tourist_bot.bot.conf.ConfLoader.load(ConfLoader.java:102)
	at com.tourist_bot.bot.WebHookTouristBotApp.main(WebHookTouristBotApp.java:71)
```

```mermaid
sequenceDiagram
    participant client as Mobile Client
    participant telegram as Telegram
    participant ngrok as Ngrok
    participant ngrok_agent as Ngrok-Agent
    participant bot as Server bot
    bot ->> ngrok_agent: set webHook <br/> Ngrok Forwarding Host:Port <br/> (ngrok_host:443)
    ngrok_agent ->> ngrok: set webHook <br/> Ngrok Forwarding Host:Port <br/> (ngrok_host:443)
    ngrok ->> telegram: set webHook <br/> Ngrok Forwarding Host:Port <br/> (ngrok_host:443)
    client ->> telegram: new user
    telegram ->> ngrok: 'new user'
    ngrok ->> ngrok_agent: 'new user'
    ngrok_agent ->> bot: 'new user'
    bot ->> ngrok_agent: send hello
    ngrok_agent ->> ngrok: send hello
    ngrok ->> telegram: send hello
    telegram ->> client: send hello

```

#### Using router's static or dynamic IP.

![](./draw_io/web_hook_router.png)

#### Generate Pem certificate
Get your ROUTER_IP. Use  https://whatismyipaddress.com/
Generate JKS certificate:

```shell
# replace ROUTER_IP with real ip
openssl req -newkey rsa:2048 -sha256 -nodes -keyout YOURPRIVATE.key -x509 -days 365 -out YOURPUBLIC.pem -subj "/C=US/ST=New York/L=Brooklyn/O=Example Brooklyn Company/CN=ROUTER_IP"

openssl pkcs12 -export -in YOURPUBLIC.pem -inkey YOURPRIVATE.key -out certificate.p12 -name "certificate"
# Enter Export Password: <keyStorePassword>  any number of characters but at least 6 characters
# Verifying - Enter Export Password: <keyStorePassword>

keytool -importkeystore -srckeystore certificate.p12 -srcstoretype pkcs12 -destkeystore cert.jks  
# Enter destination keystore password: <keyStorePassword>
# Re-enter new password: <keyStorePassword>
# Enter source keystore password: <keyStorePassword>
```

1) You have to configure Port Address Translation (PAT) on your router device.
   Port 8443 should be mapped to 8070
2) Set config 'webhook.publicPemPath' as path to 'YOURPUBLIC.pem' <b>see Generate Pem certificate</b>
3) Set config 'webhook.keyStorePath' as path to 'cert.jks'.  <b>see Generate Pem certificate</b>
4) Set config 'webhook.keyStorePassword' as value that was typed during 'keytool' command. <b>see Generate Pem certificate</b>
5) Set config 'webhook.localHostWebHookUrl' as 'https://0.0.0.0:8070'. http(S). REQUIRED HTTP(S)!!!!!!   
6) Set config 'webhook.publicHostWebHookUrl' as 'https://ROUTER_IP:8443'. http(S). REQUIRED HTTP(S)!!!!!!
7) Run com.tourist_bot.bot.WebHookTouristBotApp
8) Check that port is available https://www.yougetsignal.com/tools/open-ports/
   ![](readme_pngs/img6.png)



```mermaid
sequenceDiagram
    participant client as Mobile Client
    participant telegram as Telegram
    participant router as Router
    Note over router: Incoming packet with <br/> destination port (8443) <br/> is translated to a packet <br/> different destination <br/> port (8070)
    participant bot as Server bot
    bot ->> router: set webHook <br/> Router Ip:Port <br/> (ROUTER_IP:8443)
    router ->> telegram: set webHook <br/> Router Ip:Port <br/> (ROUTER_IP:8443)
    client ->> telegram: new user
    telegram ->> router: 'new user'
    router ->> bot: 'new user'
    bot ->> router: send hello
    router ->> telegram: send hello
    telegram ->> client: send hello
```      


# Running bot in docker cluster
## Create Image
cd to dir 'docker_cluster'    
execute     
```shell
chmod +x ./build.sh
./build.sh
```
## Running Long polling mode
![](./draw_io/docker_bot_long.png)      
put 'generated json' from step [Data preparation] as file to volume/bot/conf/tourist_places.geojson     
Set 'TELE_BOT_APP_TOKEN' inside  long/docker-compose_long.yml    
Run
```shell 
./deploy.sh long start
```

## Running Ngrok as a public host
![](./draw_io/docker_bot_webhook_ngrok.png)      
put 'generated json' from step [Data preparation] as file to volume/bot/conf/tourist_places.geojson
set 'TELE_BOT_APP_TOKEN' and 'NGROK_AUTHTOKEN' inside webhook/ngrok/docker-compose_ngrok.yml
Run    
```shell
./deploy.sh ngrok start
```

## Running webhook with static host
![](./draw_io/docker_bot_webhook_static.png)      
put 'generated json' from step [Data preparation] as file to volume/bot/conf/tourist_places.geojson    
set 'TELE_BOT_APP_TOKEN' inside webhook/static/docker-compose_static.yml
Run    
```shell
./deploy.sh static start
```

# Deploy bot on kubernetes cluster
## Minikube
replace <project_path> and run
```shell
minikube start --mount-string="<project_path>/telegram-tourist-bot/kube_cluster:/host_volume_dir" --mount
minikube image  load  keks/tourist-bot:1.0
```

## Helm
All helms are placed ./telegram-tourist-bot/kube_cluster/tourist_bot  
Set variables in each  <b>values.yaml</b> before install    

If <b>ErrImageNeverPull</b> 
1. First approach
   ```text
   Events:
     Type     Reason             Age               From               Message
     ----     ------             ----              ----               -------
     Normal   Scheduled          17s               default-scheduler  Successfully assigned default/long-bot-deploy-h-c8f9599cb-cznjn to server
     Warning  ErrImageNeverPull  2s (x3 over 17s)  kubelet            Container image "keks/tourist-bot:1.0" is not present with pull policy of Never
     Warning  Failed             2s (x3 over 17s)  kubelet            Error: ErrImageNeverPull
   ```
   kubectl get nodes -o wide
   ```text
   NAME     STATUS   ROLES           AGE   VERSION   INTERNAL-IP   EXTERNAL-IP   OS-IMAGE             KERNEL-VERSION      CONTAINER-RUNTIME
   server   Ready    control-plane   19d   v1.28.2   192.168.0.9   <none>        Ubuntu 22.04.3 LTS   5.15.0-89-generic   containerd://1.6.14
   ```
   If CONTAINER-RUNTIME  <b>containerd</b>
   ```shell
   docker save keks/tourist-bot:1.0 -o tourist-bot.tar
   sudo ctr -n k8s.io image import tourist-bot.tar
   sudo ctr -n k8s.io image ls | grep tour
   # docker.io/keks/tourist-bot:1.0
   ```
2. Second approach     
   use local registry to push and pull images    
