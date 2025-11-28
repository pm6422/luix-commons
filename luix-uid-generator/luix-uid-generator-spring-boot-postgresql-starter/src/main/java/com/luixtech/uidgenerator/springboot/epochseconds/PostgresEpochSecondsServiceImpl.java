package com.luixtech.uidgenerator.springboot.epochseconds;

import com.luixtech.uidgenerator.core.epochseconds.EpochSecondsService;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import java.time.LocalDate;
import java.time.ZoneOffset;

@RequiredArgsConstructor
public class PostgresEpochSecondsServiceImpl implements EpochSecondsService {
    private final String     workerNodeTableName;
    private final DSLContext dslContext;
    private final String     startDate;

    @Override
    public long getEpochSeconds() {
        if (startDate == null || startDate.isEmpty()) {
            // Fetch earliest created_time from worker node table, fallback to today
            Record1<Long> record = dslContext.select(DSL.field("EXTRACT(EPOCH FROM MIN(created_time))").cast(Long.class))
                    .from(DSL.table(workerNodeTableName))
                    .fetchOne();
            if (record != null && record.value1() != null) {
                return record.value1();
            }
            return LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        }
        LocalDate date = LocalDate.parse(startDate);
        return date.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
    }
}
