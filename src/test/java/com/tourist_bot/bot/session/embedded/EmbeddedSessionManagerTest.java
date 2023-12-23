package com.tourist_bot.bot.session.embedded;

import com.tourist_bot.bot.logic.ClientInfo;
import com.tourist_bot.bot.session.Session;
import com.tourist_bot.test_utils.Utils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EmbeddedSessionManagerTest {

    @Test
    public void testConcurrent() throws ExecutionException, InterruptedException, TimeoutException {

        EmbeddedSessionManager sessionManager = new EmbeddedSessionManager(Duration.of(100, ChronoUnit.SECONDS));

        Function<Integer, String> func = i -> {
            long userId = i % 100;
            ClientInfo client = getClient(userId);
            try(Session session = sessionManager.getSession(client);) {
                session.setLastUsedTimeSec(i);

                assertEquals(i, (int) session.getLastUsedTimeSec());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return "";
        };

        Utils.runConcurrentTest(1000, 10000, 1, func, 32);

    }

    private static ClientInfo getClient(long id) {
        return new ClientInfo(id, id, "", "", "", "en");
    }

}
