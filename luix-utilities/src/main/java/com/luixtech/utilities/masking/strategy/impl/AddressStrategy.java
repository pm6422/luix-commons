package com.luixtech.utilities.masking.strategy.impl;

import com.luixtech.utilities.masking.MaskingUtils;
import com.luixtech.utilities.masking.annotation.SensitiveType;
import com.luixtech.utilities.masking.strategy.Maskable;
import com.luixtech.utilities.serviceloader.annotation.SpiName;

/**
 * Mask the address
 */
@SpiName(SensitiveType.ADDRESS)
public class AddressStrategy implements Maskable {

    @Override
    public String mask(String text) {
        return MaskingUtils.maskAddress(text, 4);
    }
}