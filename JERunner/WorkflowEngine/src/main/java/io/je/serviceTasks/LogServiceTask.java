package io.je.serviceTasks;

import org.activiti.engine.delegate.DelegateExecution;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogServiceTask extends ServiceTask {

    public void execute(DelegateExecution execution) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now + " done " + execution.getId());

    }

}
