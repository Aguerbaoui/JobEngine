package io.je.serviceTasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.activiti.engine.delegate.DelegateExecution;

import io.je.processes.ProcessManager;
import io.je.utilities.logger.JELogger;
import io.je.utilities.string.JEStringUtils;

public class DatabaseInsertServiceTask extends ServiceTask {

	public void execute(DelegateExecution execution) {
		JELogger.info("database op at execution id = " + execution.getCurrentActivityId());
		
		ProcessManager.getAllCallbacks().get(JEStringUtils.substring(execution.getProcessDefinitionId(), 0, execution.getProcessDefinitionId().indexOf(':'))).onDatabaseInsertOperation(execution.getCurrentActivityId());

	}
}
