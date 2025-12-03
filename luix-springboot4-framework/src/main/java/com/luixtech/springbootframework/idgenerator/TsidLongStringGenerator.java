package com.luixtech.springbootframework.idgenerator;

import com.github.f4b6a3.tsid.TsidCreator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class TsidLongStringGenerator implements IdentifierGenerator {

    /**
     * Generate a unique 17-digit string ID
     * @param session
     * @param obj
     * @return
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        return StringUtils.EMPTY + TsidCreator.getTsid().toLong();
    }
}
