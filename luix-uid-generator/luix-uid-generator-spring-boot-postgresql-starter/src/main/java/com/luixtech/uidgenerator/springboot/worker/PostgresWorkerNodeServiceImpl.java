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
        // Hikari 配置了 auto-commit=false，需在事务中执行 DDL 以确保提交
        dslContext.transaction(configuration -> {
            DSL.using(configuration).query("CREATE TABLE IF NOT EXISTS public." + workerNodeTableName + " (\n" +
                            "    id BIGSERIAL PRIMARY KEY,\n" +
                            "    app_id VARCHAR(64) NOT NULL,\n" +
                            "    host_name VARCHAR(128) NOT NULL,\n" +
                            "    type VARCHAR(16) NOT NULL,\n" +
                            "    uptime DATE NOT NULL,\n" +
                            "    created_time TIMESTAMP NOT NULL DEFAULT NOW()\n" +
                            ")")
                    .execute();
        });
    }

    @Override
    public void insert(WorkerNode domain) {
        // 使用原生 SQL 并显式指定 public schema，避免 search_path 差异导致找不到表
        String sql = "insert into public." + workerNodeTableName +
                " (app_id, host_name, type, uptime, created_time) values (?, ?, ?, cast(? as date), cast(? as timestamp)) returning id";
        Record record = dslContext.fetchOne(sql,
                domain.getAppId(),
                domain.getHostName(),
                domain.getType(),
                domain.getUptime(),
                domain.getCreatedTime());
        if (record != null) {
            Long id = record.getValue(DSL.field("id", Long.class));
            if (id != null) {
                domain.setId(id);
            }
        }
    }
}
