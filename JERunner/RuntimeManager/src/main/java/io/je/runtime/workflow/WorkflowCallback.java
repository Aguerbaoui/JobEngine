package io.je.runtime.workflow;

import io.je.callbacks.OnExecuteOperation;

public class WorkflowCallback implements OnExecuteOperation {

    public void onDatabaseInsertOperation(String id) {
        //JELogger.info(WorkflowCallback.class, JEMessages.DB_TASK + id);

    }

    public void onOpcInsertOperation() {
        // TODO Auto-generated method stub

    }
}
