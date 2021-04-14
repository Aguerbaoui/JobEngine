package io.je.serviceTasks;

import io.je.utilities.execution.Executioner;
import io.je.utilities.logger.JELogger;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;

import java.util.Arrays;

public class ScriptServiceTask extends ServiceTask {

    public void execute(DelegateExecution execution) {
        //throw new BpmnError("testErrorRef");

        try {
            Executioner.executeScript(execution.getCurrentFlowElement().getName());
        } catch (Exception e) {
            JELogger.error(Arrays.toString(e.getStackTrace()));
            throw new BpmnError("Error");
        }

    }

}
