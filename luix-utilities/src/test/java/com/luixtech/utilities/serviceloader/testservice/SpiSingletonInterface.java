package com.luixtech.utilities.serviceloader.testservice;


import com.luixtech.utilities.serviceloader.annotation.Spi;
import com.luixtech.utilities.serviceloader.annotation.SpiScope;

@Spi(scope = SpiScope.SINGLETON)
public interface SpiSingletonInterface {
    long spiHello();
}