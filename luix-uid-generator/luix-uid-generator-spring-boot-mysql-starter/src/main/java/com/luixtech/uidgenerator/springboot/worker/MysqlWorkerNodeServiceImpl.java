package com.luixtech.uidgenerator.springboot.worker;

import com.luixtech.uidgenerator.core.exception.UidGenerateException;
import com.luixtech.uidgenerator.core.worker.WorkerNodeService;
import com.luixtech.uidgenerator.core.worker.model.WorkerNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

import static com.luixtech.uidgenerator.core.worker.model.WorkerNode.WORKER_NODE_TYPE_PHYSICAL_MACHINE;

/**
 * 不推荐写法如下：
 * ##### SQL写法:
 * SELECT count(*) FROM table WHERE a = 1 AND b = 2
 * <p>
 * ##### Java写法:
 * int nums = xxDao.countXxxxByXxx(params);
 * if ( nums > 0 ) {
 * //当存在时，执行这里的代码
 * } else {
 * //当不存在时，执行这里的代码
 * }
 * <p>
 * 推荐写法如下：
 * <p>
 * ##### SQL写法:
 * SELECT 1 FROM table WHERE a = 1 AND b = 2 LIMIT 1
 * <p>
 * ##### Java写法:
 * Integer exist = xxDao.existXxxxByXxx(params);
 * if ( exist != NULL ) {
 * //当存在时，执行这里的代码
 * } else {
 * //当不存在时，执行这里的代码
 * }
 */
@Slf4j
public class MysqlWorkerNodeServiceImpl implements WorkerNodeService {
    private static final String     COL_ID = "id";
    private final        String     tableName;
    private final        DSLContext dslContext;

    public MysqlWorkerNodeServiceImpl(String tableName, DSLContext dslContext) {
        Validate.notNull(tableName, "tableName must not be null");
        Validate.notNull(dslContext, "dslContext must not be null");

        this.tableName = tableName;
        this.dslContext = dslContext;
    }

    @Override
    public void createTableIfNotExist(boolean autoCreateTable) {
        String sql;
        try {
            sql = StreamUtils.copyToString(new ClassPathResource("id_generator_worker_node.sql").getInputStream(),
                    Charset.defaultCharset());
        } catch (IOException e) {
            throw new UidGenerateException("Failed to load sql file", e);
        }
        try {
            if (!tableExists(dslContext, tableName)) {
                if (autoCreateTable) {
                    // If table does not exist, create it
                    dslContext.execute(String.format(sql, tableName));
                    log.info("Automatically created table {}", tableName);
                } else {
                    throw new UidGenerateException("Worker node table does not exist, please create it manually with sql: \n"
                            + String.format(sql, tableName));
                }
            }
        } catch (Exception e) {
            // Re-throw it as UidGenerateException
            throw new UidGenerateException("Failed to create worker node table, please create it manually with sql: \n"
                    + String.format(sql, tableName), e);
        }
    }

    public boolean tableExists(DSLContext context, String tableName) {
        return context.meta().getTables().stream().anyMatch(table -> table.getName().equalsIgnoreCase(tableName));
    }

    @Override
    public void insert(WorkerNode domain) {
        if (WORKER_NODE_TYPE_PHYSICAL_MACHINE.equals(domain.getType())) {
            // For physical machine, we need to check if the hostName is already registered
            Record1<Object> existingOne = dslContext.select(DSL.field(COL_ID)).from(tableName)
                    .where("app_id = ?", domain.getId())
                    .and("host_name = ?", domain.getHostName())
                    .limit(1)
                    .fetchOne();
            if (existingOne != null) {
                // Re-use the existing ID
                domain.setId((long) Objects.requireNonNull(existingOne.get(COL_ID)));
                return;
            }
        }
        Long id = insertAndReturnId(domain);
        domain.setId(id);
    }

    public Long insertAndReturnId(WorkerNode domain) {
        return dslContext.transactionResult(configuration -> {
            DSLContext createContext = DSL.using(configuration);
            createContext.insertInto(DSL.table(tableName))
                            .set(DSL.field("app_id"), domain.getAppId())
                            .set(DSL.field("host_name"), domain.getHostName())
                            .set(DSL.field("type"), domain.getType())
                            .set(DSL.field("uptime"), domain.getUptime())
                            .set(DSL.field("created_time"), domain.getCreatedTime())
                            .execute();
            return createContext.lastID().longValue();
        });
    }
}
