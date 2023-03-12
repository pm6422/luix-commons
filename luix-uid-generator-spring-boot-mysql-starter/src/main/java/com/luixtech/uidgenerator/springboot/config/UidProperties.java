package com.luixtech.uidgenerator.springboot.config;


import lombok.Data;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Properties of UID generator.
 */
@ConfigurationProperties(prefix = "uid")
@Data
@Validated
public class UidProperties implements InitializingBean {
    private final DataSource   dataSource   = new DataSource();
    private final Worker       worker       = new Worker();
    private final EpochSeconds epochSeconds = new EpochSeconds();
    private final Bits         bits         = new Bits();

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notEmpty(dataSource.getName(), "dataSource.name must not be empty");
        Validate.notEmpty(worker.getWorkerNodeTableName(), "worker.workerNodeTableName must not be empty");
        Validate.notEmpty(worker.getWorkerNodeServiceName(), "worker.workerNodeServiceName must not be empty");
        Validate.notEmpty(worker.getWorkerIdAssignerName(), "worker.workerIdAssignerName must not be empty");
        Validate.notEmpty(worker.getAppId(), "worker.appId must not be empty");
        Validate.notEmpty(epochSeconds.getEpochSecondsServiceName(), "epochSeconds.epochSecondsServiceName must not be empty");

        Validate.isTrue(bits.getDeltaSecondsBits() > 0, "bits.deltaSecondsBits must be greater than 0");
        Validate.isTrue(bits.getWorkerBits() > 0, "bits.workerBits must be greater than 0");
        Validate.isTrue(bits.getSequenceBits() > 0, "bits.sequenceBits must be greater than 0");
    }


    @Data
    public static class DataSource {
        private String name = "dataSource";
    }

    @Data
    public static class Worker {
        private boolean autoCreateWorkerNodeTable = true;
        private String  workerNodeTableName       = "id_generator_worker_node";
        private String  workerNodeServiceName     = "mysqlWorkerNodeService";
        private String  workerIdAssignerName      = "defaultWorkerIdAssigner";
        private String  appId;
    }

    @Data
    public static class EpochSeconds {
        /**
         * 起始时间，也就是应用第一次使用本uid generator的时间，格式为：yyyy-MM-dd，如：2022-02-01
         */
        private String startDate;
        private String epochSecondsServiceName = "mysqlEpochSecondsService";
    }

    @Data
    public static class Bits {
        private int deltaSecondsBits = 29;
        private int workerBits       = 12;
        private int sequenceBits     = 22;
    }
}
