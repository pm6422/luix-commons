package com.luixtech.utilities.masking.strategy.impl;

import com.luixtech.utilities.masking.MaskingUtils;
import com.luixtech.utilities.masking.annotation.SensitiveType;
import com.luixtech.utilities.masking.strategy.Maskable;
import com.luixtech.utilities.serviceloader.annotation.SpiName;

@SpiName(SensitiveType.LANDLINE_PHONE)
public class LandlinePhoneStrategy implements Maskable {

    @Override
    public String mask(String text) {
        return MaskingUtils.maskLandlinePhone(text);
    }
}