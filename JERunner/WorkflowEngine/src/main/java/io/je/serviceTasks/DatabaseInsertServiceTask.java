package io.je.serviceTasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseInsertServiceTask extends ServiceTask {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInsertServiceTask.class);

    public void execute(DelegateExecution execution) {
        logger.info("database op at execution id = " + execution.getCurrentActivityId());

        //ProcessManager.getAllCallbacks().get(JEStringUtils.substring(execution.getProcessDefinitionId(), 0, execution.getProcessDefinitionId().indexOf(':'))).onDatabaseInsertOperation(execution.getCurrentActivityId());

    }
}
