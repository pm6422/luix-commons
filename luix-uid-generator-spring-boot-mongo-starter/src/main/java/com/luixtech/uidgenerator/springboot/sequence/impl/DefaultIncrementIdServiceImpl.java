package com.luixtech.uidgenerator.springboot.sequence.impl;

import com.luixtech.uidgenerator.core.exception.UidGenerateException;
import com.luixtech.uidgenerator.springboot.domain.IdSequence;
import com.luixtech.uidgenerator.springboot.sequence.IncrementIdService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import static com.luixtech.uidgenerator.springboot.domain.IdSequence.*;

@AllArgsConstructor
public class DefaultIncrementIdServiceImpl implements IncrementIdService {

    private String        collectionName;
    private MongoTemplate mongoTemplate;

    @Override
    public long getNextId() {
        Query query = new Query(Criteria.where(COL_COLLECTION_NAME).is(collectionName));
        Update update = new Update();
        update.inc(COL_MAX_SEQ_NUM, 1); // add 1
        update.setOnInsert(COL_CREATED_TIME, LocalDateTime.now());
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true); // return new after updated
        // It will create a new record if the query cannot get the record.
        IdSequence domain = mongoTemplate.findAndModify(query, update, options, IdSequence.class);
        if (domain == null) {
            throw new IllegalStateException("This will not happen generally!");
        }
        return domain.getMaxSeqNum();
    }

    @Override
    public long getCreatedTime() {
        Query query = new Query(Criteria.where(COL_COLLECTION_NAME).is(collectionName));
        IdSequence existingOne = mongoTemplate.findOne(query, IdSequence.class);
        if (existingOne != null) {
            // Truncate to zero clock
            return LocalDateTime.of(existingOne.getCreatedTime().toLocalDate(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;
        } else {
            // Throw it as UidGenerateException
            throw new UidGenerateException("Failed to get first created time");
        }
    }
}
