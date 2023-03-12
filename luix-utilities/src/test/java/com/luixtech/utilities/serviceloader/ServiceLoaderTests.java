package com.luixtech.utilities.serviceloader;

import com.luixtech.utilities.serviceloader.testservice.SpiPrototypeInterface;
import com.luixtech.utilities.serviceloader.testservice.SpiSingletonInterface;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceLoaderTests {

    @Test
    public void testSingletonInitialization() {
        // 单例模式下只会构造一次实例
        assertThat(ServiceLoader.forClass(SpiSingletonInterface.class)
                .load("singleton").spiHello()).isEqualTo(1);

        assertThat(ServiceLoader.forClass(SpiSingletonInterface.class)
                .load("singleton").spiHello()).isEqualTo(1);
    }

    @Test
    public void testPrototypeInitialization() {
        // 多例模式下在每次获取的时候进行实例化
        assertThat(ServiceLoader.forClass(SpiPrototypeInterface.class)
                .load("prototype").spiHello()).isEqualTo(1);
        assertThat(ServiceLoader.forClass(SpiPrototypeInterface.class)
                .load("prototype").spiHello()).isEqualTo(2);
    }

    @Test
    public void testChineseCharacterLoad() {
        // 单例模式下只会构造一次实例
        assertThat(ServiceLoader.forClass(SpiSingletonInterface.class)
                .load("单例").spiHello()).isEqualTo(1);
    }
}
