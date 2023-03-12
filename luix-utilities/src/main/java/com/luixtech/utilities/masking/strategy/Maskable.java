package com.luixtech.utilities.masking.strategy;

import com.luixtech.utilities.serviceloader.annotation.Spi;
import com.luixtech.utilities.serviceloader.annotation.SpiScope;

@Spi(scope = SpiScope.SINGLETON)
public interface Maskable {
    /**
     * Mask the text
     * @param text the text to mask
     * @return the masked text
     */
    String mask(String text);
}