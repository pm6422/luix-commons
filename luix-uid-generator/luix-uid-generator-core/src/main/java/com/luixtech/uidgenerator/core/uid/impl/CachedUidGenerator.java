package com.luixtech.uidgenerator.core.uid.impl;

import com.luixtech.uidgenerator.core.BitsAllocator;
import com.luixtech.uidgenerator.core.buffer.BufferPaddingExecutor;
import com.luixtech.uidgenerator.core.buffer.RejectedPutBufferHandler;
import com.luixtech.uidgenerator.core.buffer.RejectedTakeBufferHandler;
import com.luixtech.uidgenerator.core.buffer.RingBuffer;
import com.luixtech.uidgenerator.core.exception.UidGenerateException;
import com.luixtech.uidgenerator.core.uid.UidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cached implementation of {@link UidGenerator} extends
 * from {@link DefaultUidGenerator}, based on a lock free {@link RingBuffer}<p>
 * <p>
 * The spring properties you can specify as below:<br>
 * <li><b>boostPower:</b> RingBuffer size boost for a power of 2, Sample: boostPower is 3, it means the buffer size
 * will be <code>({@link BitsAllocator#getMaxSequence()} + 1) &lt;&lt;
 * {@link #boostPower}</code>, Default as {@value #DEFAULT_BOOST_POWER}
 * <li><b>paddingFactor:</b> Represents a percent value of (0 - 100). When the count of rest available UIDs reach the
 * threshold, it will trigger padding buffer. Default as{@link RingBuffer#DEFAULT_PADDING_PERCENT}
 * Sample: paddingFactor=20, bufferSize=1000 -> threshold=1000 * 20 /100, padding buffer will be triggered when tail-cursor<threshold
 * <li><b>scheduleInterval:</b> Padding buffer in a schedule, specify padding buffer interval, Unit as second
 * <li><b>rejectedPutBufferHandler:</b> Policy for rejected put buffer. Default as discard put request, just do logging
 * <li><b>rejectedTakeBufferHandler:</b> Policy for rejected take buffer. Default as throwing up an exception
 */
@Slf4j
public class CachedUidGenerator extends DefaultUidGenerator {
    private static final int DEFAULT_BOOST_POWER = 3;

    private int  boostPower = DEFAULT_BOOST_POWER;
    private Long scheduleInterval;

    private RejectedPutBufferHandler  rejectedPutBufferHandler;
    private RejectedTakeBufferHandler rejectedTakeBufferHandler;

    /**
     * RingBuffer
     */
    private RingBuffer            ringBuffer;
    private BufferPaddingExecutor bufferPaddingExecutor;

    @Override
    public void initialize() {
        // initialize workerId & bitsAllocator
        super.initialize();

        // initialize RingBuffer & RingBufferPaddingExecutor
        this.initRingBuffer();
        log.info("Initialized ringBuffer");
    }

    @PreDestroy
    public void destroy() {
        bufferPaddingExecutor.shutdown();
    }

    @Override
    public long generateUid() {
        try {
            return ringBuffer.take();
        } catch (Exception e) {
            log.error("Generate unique id exception. ", e);
            // Re-throw it as UidGenerateException
            throw new UidGenerateException(e);
        }
    }

    @Override
    public String parseUid(long uid) {
        return super.parseUid(uid);
    }

    /**
     * Get the UIDs in the same specified second under the max sequence
     *
     * @param currentSecond current time in seconds
     * @return UID list, size of {@link BitsAllocator#getMaxSequence()} + 1
     */
    protected List<Long> nextIdsForOneSecond(long currentSecond) {
        // Initialize result list size of (max sequence + 1)
        int listSize = (int) bitsAllocator.getMaxSequence() + 1;
        List<Long> uidList = new ArrayList<>(listSize);

        // Allocate the first sequence of the second, the others can be calculated with the offset
        long firstSeqUid = bitsAllocator.allocate(currentSecond - epochSeconds, workerId, 0L);
        for (int offset = 0; offset < listSize; offset++) {
            uidList.add(firstSeqUid + offset);
        }

        return uidList;
    }

    /**
     * Initialize RingBuffer & RingBufferPaddingExecutor
     */
    private void initRingBuffer() {
        // initialize RingBuffer
        int bufferSize = ((int) bitsAllocator.getMaxSequence() + 1) << boostPower;
        int paddingFactor = RingBuffer.DEFAULT_PADDING_PERCENT;
        this.ringBuffer = new RingBuffer(bufferSize, paddingFactor);
        log.info("Initialized ring buffer size: {}, paddingFactor: {}", bufferSize, paddingFactor);

        // initialize RingBufferPaddingExecutor
        boolean usingSchedule = (scheduleInterval != null);
        this.bufferPaddingExecutor = new BufferPaddingExecutor(ringBuffer, this::nextIdsForOneSecond, usingSchedule);
        if (usingSchedule) {
            bufferPaddingExecutor.setScheduleInterval(scheduleInterval);
        }

        log.info("Initialized BufferPaddingExecutor with usingSchedule: {}, interval: {}", usingSchedule, scheduleInterval);

        // set rejected put/take handle policy
        this.ringBuffer.setBufferPaddingExecutor(bufferPaddingExecutor);
        if (rejectedPutBufferHandler != null) {
            this.ringBuffer.setRejectedPutHandler(rejectedPutBufferHandler);
        }
        if (rejectedTakeBufferHandler != null) {
            this.ringBuffer.setRejectedTakeHandler(rejectedTakeBufferHandler);
        }

        // fill in all slots of the RingBuffer
        bufferPaddingExecutor.paddingBuffer();

        // start buffer padding threads
        bufferPaddingExecutor.start();
    }

    /**
     * Setters for spring property
     */
    public void setBoostPower(int boostPower) {
        Validate.isTrue(boostPower > 0, "boostPower must be positive!");
        this.boostPower = boostPower;
    }

    public void setRejectedPutBufferHandler(RejectedPutBufferHandler rejectedPutBufferHandler) {
        Validate.notNull(rejectedPutBufferHandler, "rejectedPutBufferHandler can not be null!");
        this.rejectedPutBufferHandler = rejectedPutBufferHandler;
    }

    public void setRejectedTakeBufferHandler(RejectedTakeBufferHandler rejectedTakeBufferHandler) {
        Validate.notNull(rejectedTakeBufferHandler, "rejectedTakeBufferHandler can not be null!");
        this.rejectedTakeBufferHandler = rejectedTakeBufferHandler;
    }

    public void setScheduleInterval(long scheduleInterval) {
        Validate.isTrue(scheduleInterval > 0, "scheduleInterval must positive!");
        this.scheduleInterval = scheduleInterval;
    }
}
