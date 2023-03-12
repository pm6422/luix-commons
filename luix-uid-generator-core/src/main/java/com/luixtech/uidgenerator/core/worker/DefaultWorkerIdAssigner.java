package com.luixtech.uidgenerator.core.worker;

import com.luixtech.utilities.lang.DockerUtils;
import com.luixtech.uidgenerator.core.worker.model.WorkerNode;
import com.luixtech.utilities.network.AddressUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.luixtech.uidgenerator.core.worker.model.WorkerNode.WORKER_NODE_TYPE_DOCKER;
import static com.luixtech.uidgenerator.core.worker.model.WorkerNode.WORKER_NODE_TYPE_PHYSICAL_MACHINE;

/**
 * Represents an implementation of {@link DefaultWorkerIdAssigner},
 * the worker id will be discarded after assigned to the UidGenerator
 */
@Slf4j
public class DefaultWorkerIdAssigner implements WorkerIdAssigner {

    private final String            appId;
    private final boolean           autoCreateTable;
    private final WorkerNodeService workerNodeService;
    private final long              maxWorkerId;


    public DefaultWorkerIdAssigner(String appId,
                                   boolean autoCreateTable,
                                   int workerIdBits,
                                   WorkerNodeService workerNodeService) {
        Validate.notNull(appId, "appId must not be null");
        Validate.notNull(workerNodeService, "workerNodeService must not be null");

        this.appId = appId;
        this.autoCreateTable = autoCreateTable;
        this.workerNodeService = workerNodeService;
        this.maxWorkerId = ~(-1L << workerIdBits);
    }

    /**
     * Assign worker id base on database.<p>
     * If there is host name & port in the environment, we considered that the node runs in Docker container<br>
     * Otherwise, the node runs on a physical machine.
     *
     * @return assigned worker id
     */
    @Override
    public long assignWorkerId() {
        workerNodeService.createTableIfNotExist(autoCreateTable);
        // build worker node entity
        WorkerNode workerNode = buildWorkerNode();
        // add worker node for new (ignore the same IP + PORT)
        workerNodeService.insert(workerNode);
        log.info("Created worker node record: " + workerNode);
        return getValidWorkerId(workerNode.getId());
    }

    public long getValidWorkerId(long workerId) {
        if (workerId > maxWorkerId) {
            log.warn("workerId {} is greater than maxWorkerId {}", workerId, maxWorkerId);
        }
        return workerId % (maxWorkerId + 1);
    }

    /**
     * Build worker node entity
     */
    private WorkerNode buildWorkerNode() {
        WorkerNode workerNode = new WorkerNode();
        if (DockerUtils.isRunningInDocker()) {
            workerNode.setType(WORKER_NODE_TYPE_DOCKER);
            workerNode.setHostName(StringUtils.EMPTY);
        } else {
            workerNode.setType(WORKER_NODE_TYPE_PHYSICAL_MACHINE);
            workerNode.setHostName(AddressUtils.getIntranetIp());
        }
        workerNode.setAppId(appId);
        workerNode.setUptime(LocalDate.now());
        workerNode.setCreatedTime(LocalDateTime.now());
        return workerNode;
    }
}
