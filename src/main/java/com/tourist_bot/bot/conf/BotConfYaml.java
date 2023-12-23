package com.tourist_bot.bot.conf;


public class BotConfYaml {

    private AppConfYaml app;

    private WebHookConfYaml webHook;

    private RedisConfYaml redis = null;

    public BotConfYaml() {

    }

    public AppConfYaml getApp() {
        return app;
    }

    public void setApp(AppConfYaml app) {
        this.app = app;
    }

    public WebHookConfYaml getWebHook() {
        return webHook;
    }


    public RedisConfYaml getRedis() {
        return redis;
    }

    public void setRedis(RedisConfYaml redis) {
        this.redis = redis;
    }

    @Override
    public String toString() {
        return "BotConfYaml{" +
                "app=" + app +
                ", webHook=" + webHook +
                ", redis=" + redis +
                '}';
    }

    public void setWebHook(WebHookConfYaml webHook) {
        this.webHook = webHook;
    }

}
