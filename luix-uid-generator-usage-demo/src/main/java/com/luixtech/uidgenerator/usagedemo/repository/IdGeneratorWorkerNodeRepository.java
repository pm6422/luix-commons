package com.luixtech.uidgenerator.usagedemo.repository;

import com.luixtech.uidgenerator.usagedemo.domain.IdGeneratorWorkerNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IdGeneratorWorkerNodeRepository extends JpaRepository<IdGeneratorWorkerNode, Long>, JpaSpecificationExecutor<IdGeneratorWorkerNode> {
}
