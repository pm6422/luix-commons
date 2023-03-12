package com.luixtech.uidgenerator.core.worker;

import com.luixtech.uidgenerator.core.worker.model.WorkerNode;

public interface WorkerNodeService {

    void createTableIfNotExist(boolean autoCreateTable);

    void insert(WorkerNode domain);

}
