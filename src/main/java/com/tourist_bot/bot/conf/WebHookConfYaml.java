package com.tourist_bot.bot.conf;


public class WebHookConfYaml {

    private String publicHostWebHookUrl;
    private String localHostWebHookUrl;
    private String publicPemPath;
    private String keyStorePath;
    private String keyStorePassword;

    public String getPublicHostWebHookUrl() {
        return publicHostWebHookUrl;
    }

    public void setPublicHostWebHookUrl(String publicHostWebHookUrl) {
        this.publicHostWebHookUrl = publicHostWebHookUrl;
    }

    public String getLocalHostWebHookUrl() {
        return localHostWebHookUrl;
    }

    public void setLocalHostWebHookUrl(String localHostWebHookUrl) {
        this.localHostWebHookUrl = localHostWebHookUrl;
    }

    public String getPublicPemPath() {
        return publicPemPath;
    }

    public void setPublicPemPath(String publicPemPath) {
        this.publicPemPath = publicPemPath;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    @Override
    public String toString() {
        return "WebHookConfYaml{" +
                "publicHostWebHookUrl='" + publicHostWebHookUrl + '\'' +
                ", localHostWebHookUrl='" + localHostWebHookUrl + '\'' +
                ", publicPemPath='" + publicPemPath + '\'' +
                ", keyStorePath='" + keyStorePath + '\'' +
                '}';
    }
}
