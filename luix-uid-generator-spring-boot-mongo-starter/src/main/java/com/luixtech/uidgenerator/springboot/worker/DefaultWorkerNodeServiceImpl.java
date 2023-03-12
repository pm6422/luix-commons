package com.luixtech.uidgenerator.springboot.worker;

import com.luixtech.uidgenerator.core.worker.WorkerNodeService;
import com.luixtech.uidgenerator.core.worker.model.WorkerNode;
import com.luixtech.uidgenerator.springboot.domain.IdWorkerNode;
import com.luixtech.uidgenerator.springboot.sequence.IncrementIdService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static com.luixtech.uidgenerator.core.worker.model.WorkerNode.WORKER_NODE_TYPE_PHYSICAL_MACHINE;
import static com.luixtech.uidgenerator.springboot.domain.IdWorkerNode.COL_APP_ID;
import static com.luixtech.uidgenerator.springboot.domain.IdWorkerNode.COL_HOST_NAME;

@Slf4j
public class DefaultWorkerNodeServiceImpl implements WorkerNodeService {
    private final MongoTemplate      mongoTemplate;
    private final IncrementIdService incrementIdService;

    public DefaultWorkerNodeServiceImpl(MongoTemplate mongoTemplate, IncrementIdService incrementIdService) {
        Validate.notNull(mongoTemplate, "mongoTemplate must not be null");
        Validate.notNull(incrementIdService, "incrementIdService must not be null");

        this.mongoTemplate = mongoTemplate;
        this.incrementIdService = incrementIdService;
    }

    @Override
    public void createTableIfNotExist(boolean autoCreateTable) {
        // Leave blank intentionally
    }

    @Override
    public void insert(WorkerNode domain) {
        if (WORKER_NODE_TYPE_PHYSICAL_MACHINE.equals(domain.getType())) {
            // For physical machine, we need to check if the hostName is already registered
            Query query = new Query(Criteria.where(COL_APP_ID).is(domain.getAppId()).and(COL_HOST_NAME).is(domain.getHostName()));
            IdWorkerNode existingOne = mongoTemplate.findOne(query, IdWorkerNode.class);
            if (existingOne != null) {
                // Re-use the existing ID
                domain.setId(existingOne.getId());
                return;
            }
        }
        Long id = insertAndReturnId(domain);
        domain.setId(id);
    }

    public Long insertAndReturnId(WorkerNode domain) {
        long nextId = incrementIdService.getNextId();
        IdWorkerNode idWorkerNode = new IdWorkerNode();
        BeanUtils.copyProperties(domain, idWorkerNode);
        idWorkerNode.setId(nextId);
        mongoTemplate.insert(idWorkerNode);
        return nextId;
    }
}
