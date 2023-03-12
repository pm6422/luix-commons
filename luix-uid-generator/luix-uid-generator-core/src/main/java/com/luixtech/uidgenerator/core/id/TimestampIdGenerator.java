package com.luixtech.uidgenerator.core.id;


import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The ID generator algorithm is based on the current timestamp plus an offset value
 *
 * <pre>
 *  ID value equals to currentTimeMillis * (2^20) + offset.incrementAndGet(),
 *  so we can derive the approximate time seconds from ID / (2^20 * 1000)
 * </pre>
 */
class TimestampIdGenerator {
    private static final AtomicLong OFFSET               = new AtomicLong(0);
    private static final int        BITS                 = 20;
    /**
     * Maximum count per millisecond
     * Left bit shifting to multiply by any power of two,
     * e.g: num << BITS represents num * (2^BITS), and 1 << 20 = 1048576
     */
    private static final long       MAX_COUNT_PER_MILLIS = 1 << BITS;

    /**
     * Generate a thread-safe digit format ID
     *
     * @return 19 bits length，e.g：1672888135850179037
     */
    protected static long nextId() {
        long currentTime = System.currentTimeMillis();
        long count = OFFSET.incrementAndGet();
        while (count >= MAX_COUNT_PER_MILLIS) {
            synchronized (TimestampIdGenerator.class) {
                if (OFFSET.get() >= MAX_COUNT_PER_MILLIS) {
                    OFFSET.set(0);
                }
            }
            count = OFFSET.incrementAndGet();
        }
        return (currentTime << BITS) + count;
    }

    /**
     * Parse the ID to the approximate timestamp
     *
     * @param timestampId ID
     * @return instant object
     */
    protected static Instant parseId(long timestampId) {
        return Instant.ofEpochMilli(timestampId / MAX_COUNT_PER_MILLIS);
    }
}