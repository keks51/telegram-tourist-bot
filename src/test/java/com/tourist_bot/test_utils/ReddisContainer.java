package com.tourist_bot.test_utils;

import org.testcontainers.containers.GenericContainer;


public class ReddisContainer extends GenericContainer<ReddisContainer> {

    public ReddisContainer(int port, String pass) {
        super("bitnami/redis:latest");
        addFixedExposedPort(port, port);
        this.withEnv("REDIS_PORT_NUMBER", port + "");
        this.withEnv("REDIS_PASSWORD", pass);
        this.withEnv("REDIS_AOF_ENABLED", "no");
    }

}
