package com.luixtech.uidgenerator.core;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.Validate;

/**
 * Allocate 64 bits for the UID(long)<br>
 * sign (fixed 1bit) -> deltaSeconds -> workerId -> sequence(within the same second)
 */
@Getter
@ToString
public class BitsAllocator {
    /**
     * Total 64 bits
     */
    public static final int TOTAL_BITS = 1 << 6;

    /**
     * Bits for [sign-> second-> workId-> sequence]
     */
    private final int signBits = 1;
    private final int deltaSecondsBits;
    private final int workerIdBits;
    private final int sequenceBits;

    /**
     * Max values for deltaSeconds, workId and sequence
     */
    private final long maxDeltaSeconds;
    private final long maxWorkerId;
    private final long maxSequence;

    /**
     * Shift for timestamp & workerId
     */
    private final int timestampShift;
    private final int workerIdShift;

    /**
     * Constructor with timestampBits, workerIdBits, sequenceBits<br>
     * The highest bit used for sign, so <code>63</code> bits for timestampBits, workerIdBits, sequenceBits
     */
    public BitsAllocator(int deltaSecondsBits, int workerIdBits, int sequenceBits) {
        // Validate 64 bits length
        int allocateTotalBits = signBits + deltaSecondsBits + workerIdBits + sequenceBits;
        Validate.isTrue(allocateTotalBits == TOTAL_BITS, "allocate not enough 64 bits");

        // Initialize bits
        this.deltaSecondsBits = deltaSecondsBits;
        this.workerIdBits = workerIdBits;
        this.sequenceBits = sequenceBits;

        // Initialize max value
        this.maxDeltaSeconds = ~(-1L << deltaSecondsBits);
        this.maxWorkerId = ~(-1L << workerIdBits);
        this.maxSequence = ~(-1L << sequenceBits);

        // Initialize shift
        this.timestampShift = workerIdBits + sequenceBits;
        this.workerIdShift = sequenceBits;
    }

    /**
     * Allocate bits for UID according to delta seconds & workerId & sequence<br>
     * <b>Note that: </b>The highest bit will always be 0 for sign
     *
     * @param deltaSeconds delta seconds
     * @param workerId     worker id
     * @param sequence     sequence
     * @return
     */
    public long allocate(long deltaSeconds, long workerId, long sequence) {
        return (deltaSeconds << timestampShift) | (workerId << workerIdShift) | sequence;
    }
}