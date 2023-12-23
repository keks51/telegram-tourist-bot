package com.tourist_bot.bot.conf;


public class RedisConfYaml {

    private String host;
    private int port;
    private String pass;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "RedisConfYaml{" +
                "host='" + host + '\'' +
                ", port=" + port + '\'' +
                '}';
    }

}
