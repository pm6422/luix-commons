package com.luixtech.springbootframework.idgenerator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;

import com.github.f4b6a3.tsid.TsidCreator;

public class TsidGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        return TsidCreator.getTsid().toLong();
    }
}
