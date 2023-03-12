package com.luixtech.uidgenerator.core.uid.impl;

import com.luixtech.uidgenerator.core.BitsAllocator;
import com.luixtech.uidgenerator.core.epochseconds.EpochSecondsService;
import com.luixtech.uidgenerator.core.exception.UidGenerateException;
import com.luixtech.uidgenerator.core.uid.UidGenerator;
import com.luixtech.uidgenerator.core.worker.WorkerIdAssigner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Represents an implementation of {@link UidGenerator}
 * <p>
 * The unique id has 64bits (long), default allocated as blow:<br>
 * <li>sign: The highest bit is 0
 * <li>delta seconds: The next 28 bits, represents delta seconds since a customer epoch(2016-05-20 00:00:00.000).
 * Supports about 8.7 years until to 2024-11-20 21:24:16
 * <li>worker id: The next 22 bits, represents the worker's id which assigns based on database, max id is about 420W
 * <li>sequence: The next 13 bits, represents a sequence within the same second, max for 8192/s<br><br>
 * <p>
 * The {@link DefaultUidGenerator#parseUid(long)} is a tool method to parse the bits
 *
 * <pre>{@code
 * +------+----------------------+----------------+-----------+
 * | sign |     delta seconds    | worker node id | sequence  |
 * +------+----------------------+----------------+-----------+
 *   1bit          28bits              22bits         13bits
 * }</pre>
 * <p>
 * You can also specify the bits by Spring property setting.
 * <li>timeBits: default as 28
 * <li>workerBits: default as 22
 * <li>seqBits: default as 13
 * <li>epochStr: Epoch date string format 'yyyy-MM-dd'. Default as '2016-05-20'<p>
 *
 * <b>Note that:</b> The total bits must be 64 -1
 */
@Slf4j
public class DefaultUidGenerator implements UidGenerator {

    /**
     * Bits allocation
     */
    protected int deltaSecondsBits = 28;
    protected int workerBits       = 22;
    protected int sequenceBits     = 13;

    /**
     * Customer epoch, unit as second. For example 2016-05-20 (ms: 1463673600000)
     */
    protected long epochSeconds;

    /**
     * Stable fields after spring bean initializing
     */
    protected BitsAllocator       bitsAllocator;
    protected long                workerId;
    protected WorkerIdAssigner    workerIdAssigner;
    protected EpochSecondsService epochSecondsService;

    protected long sequence   = 0L;
    protected long lastSecond = -1L;

    public void initialize() {
        // Initialize bits allocator
        bitsAllocator = new BitsAllocator(deltaSecondsBits, workerBits, sequenceBits);

        // Initialize worker ID
        assignWorkerId();

        // Initialize epoch seconds
        assignEpochSeconds();
        log.info("Allocated bits with deltaSeconds: {}, worker: {} and sequence: {}",
                deltaSecondsBits, workerBits, sequenceBits);
    }

    private void assignWorkerId() {
        workerId = workerIdAssigner.assignWorkerId();
        log.info("Assigned worker ID: {}", workerId);
        Validate.validState(workerId <= bitsAllocator.getMaxWorkerId(),
                "Worker id [" + workerId + "] must not exceed the max " + bitsAllocator.getMaxWorkerId());
    }

    private void assignEpochSeconds() {
        epochSeconds = epochSecondsService.getEpochSeconds();
        log.info("Assigned epoch time: {}", Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Override
    public long generateUid() throws UidGenerateException {
        try {
            return nextId();
        } catch (Exception e) {
            log.error("Failed to generate unique id", e);
            // Re-throw it as UidGenerateException
            throw new UidGenerateException(e);
        }
    }

    @Override
    public String parseUid(long uid) {
        long totalBits = BitsAllocator.TOTAL_BITS;
        long signBits = bitsAllocator.getSignBits();
        long timestampBits = bitsAllocator.getDeltaSecondsBits();
        long workerIdBits = bitsAllocator.getWorkerIdBits();
        long sequenceBits = bitsAllocator.getSequenceBits();

        // parse UID
        long sequence = (uid << (totalBits - sequenceBits)) >>> (totalBits - sequenceBits);
        long workerId = (uid << (timestampBits + signBits)) >>> (totalBits - workerIdBits);
        long deltaSeconds = uid >>> (workerIdBits + sequenceBits);

        Instant instant = Instant.ofEpochSecond(epochSeconds + deltaSeconds);
        return String.format("{UID: %d, timestamp: %s, workerId: %d, sequence: %d}",
                uid, instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), workerId, sequence);
    }

    /**
     * Get UID
     *
     * @return UID
     * @throws UidGenerateException in the case: Clock moved backwards; Exceeds the max timestamp
     */
    protected synchronized long nextId() {
        long currentSecond = getCurrentSecond();

        // Clock moved backwards, refuse to generate uid
        if (currentSecond < lastSecond) {
            long refusedSeconds = lastSecond - currentSecond;
            throw new UidGenerateException("Clock moved backwards. Refusing for %d seconds", refusedSeconds);
        }

        // At the same second, increase sequence
        if (currentSecond == lastSecond) {
            sequence = (sequence + 1) & bitsAllocator.getMaxSequence();
            // Exceed the max sequence, we wait the next second to generate uid
            if (sequence == 0) {
                currentSecond = getNextSecond(lastSecond);
            }

            // At the different second, sequence restart from zero
        } else {
            sequence = 0L;
        }

        lastSecond = currentSecond;

        // Allocate bits for UID
        return bitsAllocator.allocate(currentSecond - epochSeconds, workerId, sequence);
    }

    /**
     * Get next second
     */
    private long getNextSecond(long lastTimestamp) {
        long timestamp = getCurrentSecond();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentSecond();
        }

        return timestamp;
    }

    /**
     * Get current second
     */
    private long getCurrentSecond() {
        long currentSecond = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        if (currentSecond - epochSeconds > bitsAllocator.getMaxDeltaSeconds()) {
            throw new UidGenerateException("Timestamp bits is exhausted. Refusing UID generate. Now: " + currentSecond);
        }

        return currentSecond;
    }

    public void setWorkerIdAssigner(WorkerIdAssigner workerIdAssigner) {
        this.workerIdAssigner = workerIdAssigner;
    }

    public void setEpochSecondsService(EpochSecondsService epochSecondsService) {
        this.epochSecondsService = epochSecondsService;
    }

    public void setDeltaSecondsBits(int deltaSecondsBits) {
        if (deltaSecondsBits > 0) {
            this.deltaSecondsBits = deltaSecondsBits;
        }
    }

    public void setWorkerBits(int workerBits) {
        if (workerBits > 0) {
            this.workerBits = workerBits;
        }
    }

    public void setSequenceBits(int sequenceBits) {
        if (sequenceBits > 0) {
            this.sequenceBits = sequenceBits;
        }
    }
}
