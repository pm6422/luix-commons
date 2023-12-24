package com.luixtech.uidgenerator.demo.controller;

import com.luixtech.uidgenerator.demo.domain.IdGeneratorWorkerNode;
import com.luixtech.uidgenerator.demo.repository.IdGeneratorWorkerNodeRepository;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * spring filter
 * https://github.com/turkraft/spring-filter
 * <p>
 * spring boot-jpa整合QueryDSL来简化复杂操作
 * https://blog.csdn.net/liuchuanhong1/article/details/70244261/
 */
@RestController
public class WorkerNodeController {

    @Resource
    private IdGeneratorWorkerNodeRepository idGeneratorWorkerNodeRepository;

    @GetMapping(value = "/api/worker-node/query")
    public Page<IdGeneratorWorkerNode> query(@Parameter(in = ParameterIn.QUERY, name = "filter", description = "query criteria",
            schema = @Schema(type = "string", defaultValue = "(id:1 and appId:'luix-uid-generator-demo') or (id > 1)"))
                                             @Filter Specification<IdGeneratorWorkerNode> spec,
                                             @ParameterObject Pageable pageable) {
        return idGeneratorWorkerNodeRepository.findAll(spec, pageable);
    }
}
