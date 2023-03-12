package com.luixtech.uidgenerator.springboot.domain;

import com.luixtech.uidgenerator.core.worker.model.WorkerNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the IdWorkerNode entity.
 */
@Document
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class IdWorkerNode extends WorkerNode implements Serializable {
    public static final String COL_APP_ID    = "appId";
    public static final String COL_HOST_NAME = "hostName";
}
