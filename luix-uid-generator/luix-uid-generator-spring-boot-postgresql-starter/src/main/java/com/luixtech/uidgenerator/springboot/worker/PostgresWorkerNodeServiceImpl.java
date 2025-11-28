package com.luixtech.uidgenerator.springboot.worker;

import com.luixtech.uidgenerator.core.worker.WorkerNodeService;
import com.luixtech.uidgenerator.core.worker.model.WorkerNode;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;

@RequiredArgsConstructor
public class PostgresWorkerNodeServiceImpl implements WorkerNodeService {
    private final String     workerNodeTableName;
    private final DSLContext dslContext;

    @Override
    public void createTableIfNotExist(boolean autoCreateTable) {
        if (!autoCreateTable) {
            return;
        }
        dslContext.query("CREATE TABLE IF NOT EXISTS " + workerNodeTableName + " (\n" +
                        "    id BIGSERIAL PRIMARY KEY,\n" +
                        "    app_id VARCHAR(64) NOT NULL,\n" +
                        "    host_name VARCHAR(128) NOT NULL,\n" +
                        "    type VARCHAR(16) NOT NULL,\n" +
                        "    uptime DATE NOT NULL,\n" +
                        "    created_time TIMESTAMP NOT NULL DEFAULT NOW()\n" +
                        ")")
                .execute();
    }

    @Override
    public void insert(WorkerNode domain) {
        Record record = dslContext.insertInto(DSL.table(workerNodeTableName))
                .columns(
                        DSL.field("app_id"),
                        DSL.field("host_name"),
                        DSL.field("type"),
                        DSL.field("uptime"),
                        DSL.field("created_time")
                )
                .values(
                        domain.getAppId(),
                        domain.getHostName(),
                        domain.getType(),
                        domain.getUptime(),
                        domain.getCreatedTime()
                )
                .returning()
                .fetchOne();
        if (record != null) {
            Long id = record.getValue(DSL.field("id", Long.class));
            if (id != null) {
                domain.setId(id);
            }
        }
    }
}
