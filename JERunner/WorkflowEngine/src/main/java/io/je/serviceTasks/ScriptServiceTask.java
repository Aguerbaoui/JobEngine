package io.je.serviceTasks;

import io.je.utilities.execution.Executioner;
import org.activiti.engine.delegate.DelegateExecution;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScriptServiceTask extends ServiceTask {

    public void execute(DelegateExecution execution) {
        Executioner.executeScript(execution.getCurrentFlowElement().getName());
    }

}
