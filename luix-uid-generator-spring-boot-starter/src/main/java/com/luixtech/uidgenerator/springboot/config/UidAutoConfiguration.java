package com.luixtech.uidgenerator.springboot.config;

import com.luixtech.uidgenerator.core.epochseconds.EpochSecondsService;
import com.luixtech.uidgenerator.core.uid.UidGenerator;
import com.luixtech.uidgenerator.core.uid.impl.CachedUidGenerator;
import com.luixtech.uidgenerator.core.worker.DefaultWorkerIdAssigner;
import com.luixtech.uidgenerator.core.worker.WorkerIdAssigner;
import com.luixtech.uidgenerator.core.worker.WorkerNodeService;
import com.luixtech.uidgenerator.springboot.epochseconds.MysqlEpochSecondsServiceImpl;
import com.luixtech.uidgenerator.springboot.worker.MysqlWorkerNodeServiceImpl;
import org.apache.commons.lang3.Validate;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import javax.sql.DataSource;

@EnableConfigurationProperties({UidProperties.class})
public class UidAutoConfiguration {

    @Resource
    private UidProperties      uidProperties;
    @Resource
    private ApplicationContext applicationContext;

    @Bean
    public DSLContext luixDslContext() {
        DataSource dataSource = applicationContext.getBean(uidProperties.getDataSource().getName(), DataSource.class);
        Validate.notNull(dataSource, "dataSource must not be null, please check your configuration");
        DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.set(dataSource);
        configuration.settings().setRenderSchema(false);
        return DSL.using(configuration);
    }

    @Bean
    public WorkerNodeService mysqlWorkerNodeService(@Autowired @Qualifier("luixDslContext") DSLContext dslContext) {
        return new MysqlWorkerNodeServiceImpl(uidProperties.getWorker().getWorkerNodeTableName(), dslContext);
    }

    @Bean
    public EpochSecondsService mysqlEpochSecondsService(@Autowired @Qualifier("luixDslContext") DSLContext dslContext) {
        return new MysqlEpochSecondsServiceImpl(uidProperties.getWorker().getWorkerNodeTableName(), dslContext,
                uidProperties.getEpochSeconds().getStartDate());
    }

    @Bean
    public WorkerIdAssigner defaultWorkerIdAssigner() {
        WorkerNodeService workerNodeService = applicationContext
                .getBean(uidProperties.getWorker().getWorkerNodeServiceName(), WorkerNodeService.class);
        return new DefaultWorkerIdAssigner(uidProperties.getWorker().getAppId(),
                uidProperties.getWorker().isAutoCreateWorkerNodeTable(),
                uidProperties.getBits().getWorkerBits(),
                workerNodeService);
    }

    @Bean
    public UidGenerator uidGenerator() {
        CachedUidGenerator uidGenerator = new CachedUidGenerator();
        uidGenerator.setWorkerIdAssigner(getWorkerIdAssigner());
        uidGenerator.setEpochSecondsService(getEpochSecondsService());
        uidGenerator.setDeltaSecondsBits(uidProperties.getBits().getDeltaSecondsBits());
        uidGenerator.setWorkerBits(uidProperties.getBits().getWorkerBits());
        uidGenerator.setSequenceBits(uidProperties.getBits().getSequenceBits());
        uidGenerator.initialize();
        return uidGenerator;
    }

    private WorkerIdAssigner getWorkerIdAssigner() {
        return applicationContext
                .getBean(uidProperties.getWorker().getWorkerIdAssignerName(), WorkerIdAssigner.class);
    }

    private EpochSecondsService getEpochSecondsService() {
        return applicationContext
                .getBean(uidProperties.getEpochSeconds().getEpochSecondsServiceName(), EpochSecondsService.class);
    }
}
