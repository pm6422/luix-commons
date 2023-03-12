package com.luixtech.uidgenerator.springboot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Spring Data MongoDB collection for the IdSequence entity.
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdSequence implements Serializable {
    private static final long   serialVersionUID    = 1L;
    public static final  String COL_COLLECTION_NAME = "collectionName";
    public static final  String COL_MAX_SEQ_NUM     = "maxSeqNum";
    public static final  String COL_CREATED_TIME    = "createdTime";

    @Id
    protected String        id;
    private   String        collectionName;
    private   long          maxSeqNum = 0;
    private   LocalDateTime createdTime;
}
