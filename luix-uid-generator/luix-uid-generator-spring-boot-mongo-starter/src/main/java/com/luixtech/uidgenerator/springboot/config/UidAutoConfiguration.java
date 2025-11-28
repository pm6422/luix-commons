package com.luixtech.uidgenerator.springboot.config;

import com.luixtech.uidgenerator.core.epochseconds.EpochSecondsService;
import com.luixtech.uidgenerator.core.uid.UidGenerator;
import com.luixtech.uidgenerator.core.uid.impl.CachedUidGenerator;
import com.luixtech.uidgenerator.core.worker.DefaultWorkerIdAssigner;
import com.luixtech.uidgenerator.core.worker.WorkerIdAssigner;
import com.luixtech.uidgenerator.core.worker.WorkerNodeService;
import com.luixtech.uidgenerator.springboot.domain.IdWorkerNode;
import com.luixtech.uidgenerator.springboot.epochseconds.DefaultEpochSecondsServiceImpl;
import com.luixtech.uidgenerator.springboot.sequence.IncrementIdService;
import com.luixtech.uidgenerator.springboot.sequence.impl.DefaultIncrementIdServiceImpl;
import com.luixtech.uidgenerator.springboot.worker.DefaultWorkerNodeServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "luix.uid", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({UidProperties.class})
@AllArgsConstructor
@Configuration
public class UidAutoConfiguration {
    private final UidProperties      uidProperties;
    private final ApplicationContext applicationContext;

    @Bean
    public IncrementIdService defaultIncrementIdServiceImpl(MongoTemplate mongoTemplate) {
        return new DefaultIncrementIdServiceImpl(IdWorkerNode.class.getSimpleName(), mongoTemplate);
    }

    @Bean
    public WorkerNodeService defaultWorkerNodeService(MongoTemplate mongoTemplate, IncrementIdService incrementIdService) {
        return new DefaultWorkerNodeServiceImpl(mongoTemplate, incrementIdService);
    }

    @Bean
    public EpochSecondsService defaultEpochSecondsService(IncrementIdService incrementIdService) {
        return new DefaultEpochSecondsServiceImpl(uidProperties.getEpochSeconds().getStartDate(), incrementIdService);
    }

    @Bean
    public WorkerIdAssigner defaultWorkerIdAssigner() {
        WorkerNodeService workerNodeService = applicationContext.getBean(uidProperties.getWorker().getWorkerNodeServiceName(), WorkerNodeService.class);
        return new DefaultWorkerIdAssigner(uidProperties.getWorker().getAppId(), uidProperties.getWorker().isAutoCreateWorkerNodeTable(), uidProperties.getBits().getWorkerBits(), workerNodeService);
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
        return applicationContext.getBean(uidProperties.getWorker().getWorkerIdAssignerName(), WorkerIdAssigner.class);
    }

    private EpochSecondsService getEpochSecondsService() {
        return applicationContext.getBean(uidProperties.getEpochSeconds().getEpochSecondsServiceName(), EpochSecondsService.class);
    }
}
