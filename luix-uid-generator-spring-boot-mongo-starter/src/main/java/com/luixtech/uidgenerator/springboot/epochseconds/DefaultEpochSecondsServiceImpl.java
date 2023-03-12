package com.luixtech.uidgenerator.springboot.epochseconds;

import com.luixtech.uidgenerator.core.epochseconds.EpochSecondsService;
import com.luixtech.uidgenerator.core.exception.UidGenerateException;
import com.luixtech.uidgenerator.springboot.sequence.IncrementIdService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.time.Instant;

public class DefaultEpochSecondsServiceImpl implements EpochSecondsService {

    private final String             specifiedEpochStr;
    private final IncrementIdService incrementIdService;

    public DefaultEpochSecondsServiceImpl(String specifiedEpochStr, IncrementIdService incrementIdService) {
        Validate.notNull(incrementIdService, "mongoTemplate must not be null");

        this.specifiedEpochStr = specifiedEpochStr;
        this.incrementIdService = incrementIdService;
    }

    @Override
    public long getEpochSeconds() {
        if (StringUtils.isNotEmpty(specifiedEpochStr)) {
            // Use default epoch
            return Instant.parse(specifiedEpochStr + "T00:00:00Z").getEpochSecond();
        }
        try {
            return incrementIdService.getCreatedTime();
        } catch (Exception e) {
            // Re-throw it as UidGenerateException
            throw new UidGenerateException("Failed to get first created time", e);
        }
    }
}
