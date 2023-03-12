package com.luixtech.uidgenerator.usagedemo.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class IdGeneratorWorkerNode {

    @Id
    private Long          id;
    /**
     * Application ID
     */
    private String        appId;
    /**
     * HostName for CONTAINER or IP for PHYSICAL_MACHINE
     */
    private String        hostName;
    /**
     * Work node type: P - Physical Machine, C - Container
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
