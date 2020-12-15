package io.je.serviceTasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.je.processes.ProcessManager;
import io.je.utilities.logger.JELogger;
import io.je.utilities.string.JEStringUtils;

public class DatabaseInsertServiceTask extends ServiceTask {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseInsertServiceTask.class); 
	public void execute(DelegateExecution execution) {
		logger.info("database op at execution id = " + execution.getCurrentActivityId());
		
		//ProcessManager.getAllCallbacks().get(JEStringUtils.substring(execution.getProcessDefinitionId(), 0, execution.getProcessDefinitionId().indexOf(':'))).onDatabaseInsertOperation(execution.getCurrentActivityId());

	}
}
