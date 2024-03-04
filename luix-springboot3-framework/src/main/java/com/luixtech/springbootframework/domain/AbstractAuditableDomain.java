package com.luixtech.springbootframework.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Instant;

/**
 * Abstract auditable domain for log createdBy, createdTime, modifiedBy and modifiedTime automatically.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AbstractAuditableDomain extends AbstractCreationDomain implements Serializable {

    private static final long   serialVersionUID    = 1L;
    public static final  String FIELD_MODIFIED_BY   = "modifiedBy";
    public static final  String FIELD_MODIFIED_TIME = "modifiedTime";

    /**
     * Set the proper value when updating. Value comes from SpringSecurityAuditorAware.getCurrentAuditor()
     */
    @LastModifiedBy
    @Schema(description = "更新人")
    protected String modifiedBy;

    /**
     * Set the current time when updating.
     */
    @LastModifiedDate
    @Schema(description = "更新时间", required = true)
    protected Instant modifiedTime;
}
