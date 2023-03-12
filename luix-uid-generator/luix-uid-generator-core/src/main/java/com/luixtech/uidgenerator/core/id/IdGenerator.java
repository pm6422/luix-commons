package com.luixtech.uidgenerator.core.id;


import java.time.Instant;

public abstract class IdGenerator {
    private static final ShortIdGenerator SHORT_ID_GENERATOR = new ShortIdGenerator();

    /**
     * Generate a thread-safe digit format ID
     *
     * @return 19 bits length，e.g：1672888135850179037
     */
    public static long generateTimestampId() {
        return TimestampIdGenerator.nextId();
    }

    /**
     * Generate a 20 bits string format ID
     *
     * @return 20 bits length，e.g：S317297928250941551
     */
    public static String generateId() {
        return "S" + generateTimestampId();
    }

    /**
     * Parse the timestampId ID to the approximate timestamp
     *
     * @param timestampId ID
     * @return instant object
     */
    public static Instant parseTimestampId(long timestampId) {
        return TimestampIdGenerator.parseId(timestampId);
    }

    /**
     * Generate a thread-safe digit format ID
     *
     * @return 12 bits length，e.g：306554419571
     */
    public static long generateShortId() {
        return SHORT_ID_GENERATOR.nextId();
    }
}
