package com.tourist_bot.test_utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Utils {


    public static void runConcurrentTest(int taskCount,
                                         int threadPoolAwaitTimeoutSec,
                                         int taskAwaitTimeoutSec,
                                         Function<Integer, String> function,
                                         int numberOfThreads) throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<?>> futures = new ArrayList<>(taskCount);
        for (int i = 0; i < taskCount; i++) {
            int y = i;
            Future<?> future1 = executor.submit(() -> function.apply(y));
            futures.add(future1);
        }
        executor.shutdown();
        if (!executor.awaitTermination(threadPoolAwaitTimeoutSec, TimeUnit.SECONDS)) {
            throw new InterruptedException();
        }

        assertEquals(taskCount, futures.size());
        for (Future<?> future : futures) {
            future.get(taskAwaitTimeoutSec, TimeUnit.SECONDS);
        }

    }

}
