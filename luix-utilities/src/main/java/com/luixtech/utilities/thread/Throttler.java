package com.luixtech.utilities.thread;

import org.apache.commons.lang3.Validate;

/**
 * Utility to throttle a thread to a given number of executions (records) per second.
 */
public final class Throttler {
    private long throttleBatchSize;
    private long nanosPerBatch;
    private long endOfNextBatchNanos;
    private int  currentBatch;

    public Throttler(long maxRecordsPerSecond, int numberOfParallelSubtasks) {
        setup(maxRecordsPerSecond, numberOfParallelSubtasks);
    }

    public void adjustMaxRecordsPerSecond(long maxRecordsPerSecond) {
        setup(maxRecordsPerSecond, 1);
    }

    private void setup(long maxRecordsPerSecond, int numberOfParallelSubtasks) {
        Validate.isTrue(
                maxRecordsPerSecond == -1 || maxRecordsPerSecond > 0,
                "maxRecordsPerSecond must be positive or -1 (infinite)");
        Validate.isTrue(numberOfParallelSubtasks > 0, "numberOfParallelSubtasks must be greater than 0");

        if (maxRecordsPerSecond == -1) {
            // unlimited speed
            throttleBatchSize = -1;
            nanosPerBatch = 0;
            endOfNextBatchNanos = System.nanoTime() + nanosPerBatch;
            currentBatch = 0;
            return;
        }
        final float ratePerSubtask = (float) maxRecordsPerSecond / numberOfParallelSubtasks;

        if (ratePerSubtask >= 10000) {
            // high rates: all throttling in intervals of 2ms
            throttleBatchSize = (int) ratePerSubtask / 500;
            nanosPerBatch = 2_000_000L;
        } else {
            throttleBatchSize = ((int) (ratePerSubtask / 20)) + 1;
            nanosPerBatch = ((int) (1_000_000_000L / ratePerSubtask)) * throttleBatchSize;
        }
        this.endOfNextBatchNanos = System.nanoTime() + nanosPerBatch;
        this.currentBatch = 0;
    }


    public void throttle() throws InterruptedException {
        if (throttleBatchSize == -1) {
            return;
        }
        if (++currentBatch != throttleBatchSize) {
            return;
        }
        currentBatch = 0;

        final long now = System.nanoTime();
        final int millisRemaining = (int) ((endOfNextBatchNanos - now) / 1_000_000);

        if (millisRemaining > 0) {
            endOfNextBatchNanos += nanosPerBatch;
            Thread.sleep(millisRemaining);
        } else {
            endOfNextBatchNanos = now + nanosPerBatch;
        }
    }
}
