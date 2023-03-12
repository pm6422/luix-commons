package com.luixtech.uidgenerator.core.uid;

import com.luixtech.uidgenerator.core.exception.UidGenerateException;

/**
 * Represents a unique id generator.
 */
public interface UidGenerator {

    /**
     * Generate a digit format ID with unique value under cluster environment
     *
     * @return 19 bits length digit，e.g：2068288161913520137
     * @throws UidGenerateException if generate failed
     */
    long generateUid() throws UidGenerateException;

    /**
     * Parse the UID into elements which are used to generate the UID. <br>
     * Such as timestamp & workerId & sequence...
     *
     * @param uid the uid to parse
     * @return parsed info
     */
    String parseUid(long uid);
}
