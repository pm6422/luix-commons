package com.luixtech.uidgenerator.core.worker.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkerNode {

    public static final String WORKER_NODE_TYPE_PHYSICAL_MACHINE = "P";
    public static final String WORKER_NODE_TYPE_DOCKER           = "D";

    private long          id;
    /**
     * Application ID
     */
    private String        appId;
    /**
     * HostName for CONTAINER or IP for PHYSICAL_MACHINE
     */
    private String        hostName;
    /**
     * Work node type: P - Physical Machine, D - Docker
     */
    private String        type;
    /**
     * Uptime
     */
    private LocalDate     uptime;
    /**
     * Created time
     */
    private LocalDateTime createdTime;
}
