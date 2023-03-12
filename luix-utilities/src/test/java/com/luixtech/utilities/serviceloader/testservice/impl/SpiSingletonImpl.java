package com.luixtech.utilities.serviceloader.testservice.impl;

import com.luixtech.utilities.serviceloader.annotation.SpiName;
import com.luixtech.utilities.serviceloader.testservice.SpiSingletonInterface;

import java.util.concurrent.atomic.AtomicLong;

@SpiName("singleton")
public class SpiSingletonImpl implements SpiSingletonInterface {
    private static AtomicLong counter = new AtomicLong(0);
    private        long       index   = 0;

    public SpiSingletonImpl() {
        index = counter.incrementAndGet();
    }

    @Override
    public long spiHello() {
        return index;
    }

}
