package io.je.serviceTasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.je.utilities.logger.JELogger;

public class MailServiceTask extends ServiceTask{
	
	private static final Logger logger = LoggerFactory.getLogger(MailServiceTask.class); 
	public void execute(DelegateExecution execution) {
		logger.info("mail op at execution id = " + execution.getCurrentActivityId());

	}
}
