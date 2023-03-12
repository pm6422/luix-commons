package com.luixtech.uidgenerator.core.id;

import com.luixtech.utilities.lang.collection.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TimestampIdGeneratorTest {

    @Test
    public void uniqueTestOnSingleThread() throws InterruptedException {
        // Thread-safe container
        Set<Long> set = new ConcurrentHashSet<>();
        int maxTimes = 1_000;
        // Single thread pool
        ExecutorService threadPool = Executors.newFixedThreadPool(1);

        IntStream.range(0, maxTimes).forEach(i -> {
            threadPool.execute(() -> {
                set.add(TimestampIdGenerator.nextId());
            });
        });

        threadPool.shutdown();
        if (threadPool.awaitTermination(1, TimeUnit.HOURS)) {
            assertThat(maxTimes).isEqualTo(set.size());
        }
    }

    @Test
    public void uniqueTestOnMultiThreads() throws InterruptedException {
        // Thread-safe container
        Set<Long> set = new ConcurrentHashSet<>();
        int maxTimes = 100_000;

        // Multi-threads pool
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        IntStream.range(0, maxTimes).forEach(i -> {
            threadPool.execute(() -> {
                long id = TimestampIdGenerator.nextId();
                log.debug("Active thread count: {}", Thread.activeCount());
                log.debug("Generated ID: {}", id);
                set.add(id);
            });
        });

        threadPool.shutdown();
        if (threadPool.awaitTermination(1, TimeUnit.HOURS)) {
            assertThat(maxTimes).isEqualTo(set.size());
        }
    }

    @Test
    public void getApproximateTime() {
        long id = TimestampIdGenerator.nextId();
        Instant approximateTime = TimestampIdGenerator.parseId(id);
        assertThat(approximateTime).isCloseTo(Instant.now(), new TemporalUnitLessThanOffset(1, ChronoUnit.SECONDS));
    }
}
