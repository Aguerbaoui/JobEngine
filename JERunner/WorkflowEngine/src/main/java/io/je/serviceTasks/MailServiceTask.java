package io.je.serviceTasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.activiti.engine.delegate.DelegateExecution;

import io.je.utilities.logger.JELogger;

public class MailServiceTask extends ServiceTask{
	
	public void execute(DelegateExecution execution) {
		JELogger.info("mail op at execution id = " + execution.getCurrentActivityId());

	}
}
