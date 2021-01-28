package io.je.runtime.workflow;

import io.je.callbacks.OnExecuteOperation;
import io.je.utilities.logger.JELogger;

public class WorkflowCallback implements OnExecuteOperation {

    public void onDatabaseInsertOperation(String id) {
        JELogger.info(WorkflowCallback.class, "outsourcing database operation in task = " + id);

    }

    public void onOpcInsertOperation() {
        // TODO Auto-generated method stub

    }
}
