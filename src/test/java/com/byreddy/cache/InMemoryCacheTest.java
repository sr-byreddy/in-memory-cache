package com.byreddy.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class InMemoryCacheTest {

    private final Cache<String, String> cache = new InMemoryCache<>();

    @Test
    void returnsEmptyWhenKeyIsAbsent() {
        assertTrue(cache.get("missing").isEmpty());
    }

    @Test
    void storesAndRetrievesAValue() {
        cache.set("key", "value");

        assertEquals("value", cache.get("key").orElseThrow());
    }

    @Test
    void replacesAnExistingValue() {
        cache.set("key", "first");
        cache.set("key", "second");

        assertEquals("second", cache.get("key").orElseThrow());
    }

    @Test
    void deletesAnExistingValue() {
        cache.set("key", "value");

        assertTrue(cache.delete("key"));
        assertTrue(cache.get("key").isEmpty());
    }

    @Test
    void reportsWhenThereIsNothingToDelete() {
        assertFalse(cache.delete("missing"));
    }

    @Test
    void rejectsNullKeysAndValues() {
        assertThrows(NullPointerException.class, () -> cache.get(null));
        assertThrows(NullPointerException.class, () -> cache.set(null, "value"));
        assertThrows(NullPointerException.class, () -> cache.set("key", null));
        assertThrows(NullPointerException.class, () -> cache.delete(null));
    }

    @Test
    void supportsConcurrentAccess() throws Exception {
        int workerCount = 8;
        int entriesPerWorker = 1_000;
        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        CountDownLatch start = new CountDownLatch(1);

        try {
            List<Future<?>> operations = new ArrayList<>();
            for (int worker = 0; worker < workerCount; worker++) {
                int workerId = worker;
                operations.add(executor.submit(() -> {
                    start.await();
                    for (int entry = 0; entry < entriesPerWorker; entry++) {
                        String key = workerId + "-" + entry;
                        cache.set(key, "value");
                        assertEquals("value", cache.get(key).orElseThrow());
                    }
                    return null;
                }));
            }

            start.countDown();
            for (Future<?> operation : operations) {
                operation.get();
            }

            for (int worker = 0; worker < workerCount; worker++) {
                for (int entry = 0; entry < entriesPerWorker; entry++) {
                    assertEquals("value", cache.get(worker + "-" + entry).orElseThrow());
                }
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
