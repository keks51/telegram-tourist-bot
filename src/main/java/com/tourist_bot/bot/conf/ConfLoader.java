package com.tourist_bot.bot.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Map;


public class ConfLoader {

    private static final Logger log = LoggerFactory.getLogger(ConfLoader.class.getName());

    public static BotConfYaml load(String path) throws IOException {
        InputStream is;
        if (path == null) {
            log.info("Loading conf from jar");
            is = ConfLoader.class
                    .getClassLoader()
                    .getResourceAsStream("bot_conf.yaml");
        } else {
            log.info("Loading conf from path: '" + path + "'");
            is = new FileInputStream(path);
        }

        Representer representer = new Representer(new DumperOptions());
        representer.getPropertyUtils().setSkipMissingProperties(true);


        Yaml yaml = new Yaml(new Constructor(BotConfYaml.class, new LoaderOptions()), representer);


        BotConfYaml conf = yaml.load(is);

        is.close();

        Map<String, String> envVars = System.getenv();

        // APP
        if (envVars.containsKey("TELE_BOT_APP_TOKEN")) {
            String value = envVars.get("TELE_BOT_APP_TOKEN");
            log.info("Env var 'TELE_BOT_APP_TOKEN' overrides conf 'app.token' by '" + value + "'");
            conf.getApp().setToken(value);
        }
        if (conf.getApp().getToken() == null) throw new IllegalArgumentException("'app.token' cannot be null");

        if (envVars.containsKey("TELE_BOT_APP_GEO_JSON_PATH")) {
            String value = envVars.get("TELE_BOT_APP_GEO_JSON_PATH");
            log.info("Env var 'TELE_BOT_APP_GEO_JSON_PATH' overrides conf 'app.geoJsonPath' by '" + value + "'");
            conf.getApp().setGeoJsonPath(value);
        }
        if (conf.getApp().getGeoJsonPath() == null) throw new IllegalArgumentException("GeoJsonPath cannot be null");

        if (envVars.containsKey("TELE_BOT_APP_MAX_SEARCH_DISTANCE")) {
            String value = envVars.get("TELE_BOT_APP_MAX_SEARCH_DISTANCE");
            log.info("Env var 'TELE_BOT_APP_MAX_SEARCH_DISTANCE' overrides conf 'app.maxSearchDistMeters' by '" + value + "'");
            conf.getApp().setMaxSearchDistMeters(Integer.parseInt(value));
        }
        if (envVars.containsKey("TELE_BOT_APP_DEFAULT_SEARCH_DIST_STEP_METERS")) {
            String value = envVars.get("TELE_BOT_APP_DEFAULT_SEARCH_DIST_STEP_METERS");
            log.info("Env var 'TELE_BOT_APP_DEFAULT_SEARCH_DIST_STEP_METERS' overrides conf 'app.defaultSearchDistStepMeters' by '" + value + "'");
            conf.getApp().setDefaultSearchDistStepMeters(Integer.parseInt(value));
        }
        if (envVars.containsKey("TELE_BOT_APP_SESSION_TIMEOUT_SEC")) {
            String value = envVars.get("TELE_BOT_APP_SESSION_TIMEOUT_SEC");
            log.info("Env var 'TELE_BOT_APP_SESSION_TIMEOUT_SEC' overrides conf 'app.sessionTimeoutSec' by '" + value + "'");
            conf.getApp().setSessionTimeoutSec(Integer.parseInt(value));
        }
        if (envVars.containsKey("TELE_BOT_APP_SESSIONS_CLEANUP_PERIOD_SEC")) {
            String value = envVars.get("TELE_BOT_APP_SESSIONS_CLEANUP_PERIOD_SEC");
            log.info("Env var 'TELE_BOT_APP_SESSIONS_CLEANUP_PERIOD_SEC' overrides conf 'app.sessionsCleanupPeriodSec' by '" + value + "'");
            conf.getApp().setSessionsCleanupPeriodSec(Integer.parseInt(value));
        }
        if (envVars.containsKey("TELE_BOT_APP_SESSION_MANAGER")) {
            String value = envVars.get("TELE_BOT_APP_SESSION_MANAGER");
            log.info("Env var 'TELE_BOT_APP_SESSION_MANAGER' overrides conf 'app.sessionManager' by '" + value + "'");
            conf.getApp().setSessionManager(value);
        }

        // WEBHOOK
        if (envVars.containsKey("TELE_BOT_WEBHOOK_PUBLIC_HOST_WEBHOOK_URL")) {
            String value = envVars.get("TELE_BOT_WEBHOOK_PUBLIC_HOST_WEBHOOK_URL");
            log.info("Env var 'TELE_BOT_WEBHOOK_PUBLIC_HOST_WEBHOOK_URL' overrides conf 'webHook.publicHostWebHookUrl' by '" + value + "'");
            conf.getWebHook().setPublicHostWebHookUrl(value);
        }
        if (envVars.containsKey("TELE_BOT_WEBHOOK_LOCAL_HOST_WEB_HOOK_URL")) {
            String value = envVars.get("TELE_BOT_WEBHOOK_LOCAL_HOST_WEB_HOOK_URL");
            log.info("Env var 'TELE_BOT_WEBHOOK_LOCAL_HOST_WEB_HOOK_URL' overrides conf 'webHook.localHostWebHookUrl' by '" + value + "'");
            conf.getWebHook().setLocalHostWebHookUrl(value);
        }
        if (envVars.containsKey("TELE_BOT_WEBHOOK_PUBLIC_PEM_PATH")) {
            String value = envVars.get("TELE_BOT_WEBHOOK_PUBLIC_PEM_PATH");
            log.info("Env var 'TELE_BOT_WEBHOOK_PUBLIC_PEM_PATH' overrides conf 'webHook.publicPemPath' by '" + value + "'");
            conf.getWebHook().setPublicPemPath(value);
        }
        if (envVars.containsKey("TELE_BOT_WEBHOOK_KEY_STORE_PATH")) {
            String value = envVars.get("TELE_BOT_WEBHOOK_KEY_STORE_PATH");
            log.info("Env var 'TELE_BOT_WEBHOOK_KEY_STORE_PATH' overrides conf 'webHook.keyStorePath' by '" + value + "'");
            conf.getWebHook().setKeyStorePath(value);
        }
        if (envVars.containsKey("TELE_BOT_WEBHOOK_KEY_STORE_PASSWORD")) {
            String value = envVars.get("TELE_BOT_WEBHOOK_KEY_STORE_PASSWORD");
            log.info("Env var 'TELE_BOT_WEBHOOK_KEY_STORE_PASSWORD' overrides conf 'webHook.keyStorePassword'");
            conf.getWebHook().setKeyStorePassword(value);
        }
        String localHostWebHookUrl = conf.getWebHook().getLocalHostWebHookUrl();
        if (localHostWebHookUrl != null && localHostWebHookUrl.contains("https")) {
            if (conf.getWebHook().getKeyStorePath() == null) {
                throw new IllegalArgumentException("Local host is 'HTTPS': '" + localHostWebHookUrl + "'. Conf 'webhook.keyStorePath' cannot be null. Or change to 'HTTP'.");
            }
            if (conf.getWebHook().getPublicPemPath() == null) {
                throw new IllegalArgumentException("Local host is 'HTTPS': '" + localHostWebHookUrl + "'. Conf 'webhook.publicPemPath' cannot be null. Or change to 'HTTP'.");
            }
            if (conf.getWebHook().getKeyStorePassword() == null) {
                throw new IllegalArgumentException("Local host is 'HTTPS': '" + localHostWebHookUrl + "'. Conf 'webhook.keyStorePassword' cannot be null. Or change to 'HTTP'.");
            }
        }


        // REDIS
        if (envVars.containsKey("TELE_BOT_REDIS_HOST")) {
            String value = envVars.get("TELE_BOT_REDIS_HOST");
            log.info("Env var 'TELE_BOT_REDIS_HOST' overrides conf 'redis.host' by '" + value + "'");
            conf.getRedis().setHost(value);
        }
        if (envVars.containsKey("TELE_BOT_REDIS_PORT")) {
            String value = envVars.get("TELE_BOT_REDIS_PORT");
            log.info("Env var 'TELE_BOT_REDIS_PORT' overrides conf 'redis.port' by '" + value + "'");
            conf.getRedis().setPort(Integer.parseInt(value));
        }
        if (envVars.containsKey("TELE_BOT_REDIS_PASS")) {
            String value = envVars.get("TELE_BOT_REDIS_PASS");
            log.info("Env var 'TELE_BOT_REDIS_PASS' overrides conf 'redis.pass'");
            conf.getRedis().setPass(value);
        }

        System.out.println("Conf: " + conf);

        return conf;
    }

    public static void main(String[] args) throws IOException {

        BotConfYaml load = load(null);
        System.out.println(load);


    }

}
