package com.luixtech.springbootframework.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
public abstract class AbstractMultiTenancyDomain implements Serializable {
    private static final long   serialVersionUID  = 1L;
    public static final  String FIELD_TENANT_CODE = "tenantCode";

    @Schema(description = "租户代码")
    protected String tenantCode;

}
