package com.luixtech.utilities.serviceloader;

import com.luixtech.utilities.serviceloader.testservice.SpiPrototypeInterface;
import com.luixtech.utilities.serviceloader.testservice.SpiSingletonInterface;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceLoaderTests {

    @Test
    public void testSingletonInitialization() {
        // In singleton mode, the instance is constructed only once.
        assertThat(ServiceLoader.forClass(SpiSingletonInterface.class)
                .load("singleton").spiHello()).isEqualTo(1);

        assertThat(ServiceLoader.forClass(SpiSingletonInterface.class)
                .load("singleton").spiHello()).isEqualTo(1);
    }

    @Test
    public void testPrototypeInitialization() {
        // In prototype mode, a new instance is created on each load.
        assertThat(ServiceLoader.forClass(SpiPrototypeInterface.class)
                .load("prototype").spiHello()).isEqualTo(1);
        assertThat(ServiceLoader.forClass(SpiPrototypeInterface.class)
                .load("prototype").spiHello()).isEqualTo(2);
    }

    @Test
    public void testChineseCharacterLoad() {
        // Verify that a service name with non-ASCII characters can be loaded.
        assertThat(ServiceLoader.forClass(SpiSingletonInterface.class)
                .load("单例").spiHello()).isEqualTo(1);
    }
}
