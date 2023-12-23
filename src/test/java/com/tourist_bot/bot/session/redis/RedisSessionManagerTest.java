package com.tourist_bot.bot.session.redis;

import com.tourist_bot.bot.logic.ClientInfo;
import com.tourist_bot.bot.session.Session;
import com.tourist_bot.test_utils.DockerContainer;
import com.tourist_bot.test_utils.ReddisContainer;
import com.tourist_bot.test_utils.Utils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;


class RedisSessionManagerTest {


    @Test
    public void testConcurrent() throws Exception {
        String host = "127.0.0.1";
        int port = DockerContainer.findAvailablePort(4045);
        String pass = "redis";

        try (ReddisContainer reddisContainer = new ReddisContainer(port, pass)) {
            reddisContainer.start();
            try (RedisSessionManager sessionManager = new RedisSessionManager(host, port, pass, Duration.of(100, ChronoUnit.SECONDS))) {
                reddisContainer.start();
                Function<Integer, String> func = i -> {
                    long userId = i % 100;
                    ClientInfo client = getClient(userId);
                    try (Session session = sessionManager.getSession(client)) {
                        session.setLastUsedTimeSec(i);

                        assertEquals(i, (int) session.getLastUsedTimeSec());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    return "";
                };

                Utils.runConcurrentTest(1000, 10000, 1, func, 32);
            }
        }

    }

    private static ClientInfo getClient(long id) {
        return new ClientInfo(id, id, "", "", "", "en");
    }

}