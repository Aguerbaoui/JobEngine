package io.je.serviceTasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailServiceTask extends ServiceTask {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceTask.class);

    public void execute(DelegateExecution execution) {
        logger.info("mail op at execution id = " + execution.getCurrentActivityId());

    }
}
