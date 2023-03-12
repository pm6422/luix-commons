package com.luixtech.uidgenerator.core.worker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DefaultWorkerIdAssignerTest {

    @Mock
    private WorkerNodeService workerNodeService;

    @Test
    public void testAssignWorkerId() {
        int workerIdBits = 12;
        DefaultWorkerIdAssigner assigner =
                new DefaultWorkerIdAssigner("testApp", false, workerIdBits, workerNodeService);
        long maxWorkerId = ~(-1L << workerIdBits);
        for (int i = 0; i <= maxWorkerId; i++) {
            assertThat(assigner.getValidWorkerId(i)).isEqualTo(i);
        }

        assertThat(assigner.getValidWorkerId(maxWorkerId + 1)).isEqualTo(0);
        assertThat(assigner.getValidWorkerId(maxWorkerId + 2)).isEqualTo(1);

        assertThat(assigner.getValidWorkerId(maxWorkerId * 2)).isEqualTo(maxWorkerId - 1);
        assertThat(assigner.getValidWorkerId(maxWorkerId * 2 + 1)).isEqualTo(maxWorkerId);
        assertThat(assigner.getValidWorkerId(maxWorkerId * 2 + 2)).isEqualTo(0);

        for (int i = 0; i <= maxWorkerId * 3; i++) {
            assertThat(assigner.getValidWorkerId(i)).isLessThanOrEqualTo(maxWorkerId);
        }
    }
}
