package com.tourist_bot.test_utils;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

public class DockerContainer {

    public static void main(String[] args) throws IOException {
        int freePort = findAvailablePort(4040);
        System.out.println(freePort);
        String pass = "reddis";

        ReddisContainer reddisContainer = new ReddisContainer(freePort, pass);
        reddisContainer.start();
        Runtime.getRuntime().addShutdownHook(   new Thread(() -> {
            System.out.println("stopping reddis container");
            try {
                reddisContainer.stop();
            } catch (Throwable ignored) {
            }
            System.out.println("reddis container was stopped");
        }));
    }

    public static int findAvailablePort(int startPort) throws IOException {
        int endPort = startPort + 1_000;
        for (int i = startPort; i < endPort; i++) {
            try (ServerSocket ignored = new ServerSocket(i)) {
                return i;
            } catch (BindException ignored) {
            }
        }
        throw new RuntimeException("Cannot find Available port in range: [" + startPort + ":" + endPort + "])");
    }

}
