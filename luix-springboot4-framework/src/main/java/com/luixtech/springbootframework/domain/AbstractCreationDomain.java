package com.luixtech.springbootframework.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.Instant;

/**
 * Abstract auditable domain for log createdBy, createdTime, modifiedBy and modifiedTime automatically.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AbstractCreationDomain extends AbstractMultiTenancyDomain {

    private static final long   serialVersionUID   = 1L;
    public static final  String FIELD_ID           = "id";
    public static final  String FIELD_CREATED_BY   = "createdBy";
    public static final  String FIELD_CREATED_TIME = "createdTime";

    /**
     * ID data type must NOT be Long, because the number which exceeds 16 digits will be display as 0 at front end.
     * e.g. the value 526373442322434543 will be displayed as 526373442322434500 in front end
     * If id is null, save operation equals insert, or else save operation equals update
     */
    @Id
    @Schema(description = "ID主键")
    protected String id;

    /**
     * Set the proper value when inserting. Value comes from SpringSecurityAuditorAware.getCurrentAuditor()
     */
    @CreatedBy
    @Schema(description = "创建人")
    protected String createdBy;

    /**
     * Set the current time when inserting.
     */
    @CreatedDate
    @Schema(description = "创建时间")
    protected Instant createdTime;
}
