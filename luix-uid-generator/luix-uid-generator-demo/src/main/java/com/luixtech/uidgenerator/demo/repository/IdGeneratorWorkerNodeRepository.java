package com.luixtech.uidgenerator.demo.repository;

import com.luixtech.uidgenerator.demo.domain.IdGeneratorWorkerNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IdGeneratorWorkerNodeRepository extends JpaRepository<IdGeneratorWorkerNode, Long>, JpaSpecificationExecutor<IdGeneratorWorkerNode> {
}
