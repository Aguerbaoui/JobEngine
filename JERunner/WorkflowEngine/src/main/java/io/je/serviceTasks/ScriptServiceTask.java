package io.je.serviceTasks;

import io.je.utilities.execution.Executioner;
import io.je.utilities.logger.*;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;

import java.time.LocalDateTime;
import java.util.Arrays;

public class ScriptServiceTask extends ServiceTask {

    public void execute(DelegateExecution execution) {
        //throw new BpmnError("testErrorRef");

        ScriptTask task = (ScriptTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        String message = "Executing script task with id = " + task.getTaskId();
        LogMessage msg = new LogMessage(LogLevel.INFORM,  message,  LocalDateTime.now().toString(), "JobEngine",  task.getProjectId(),
                task.getProcessId(), LogSubModule.WORKFLOW, task.getTaskName(), null, "Log", "") ;
        try {
            Executioner.executeScript(execution.getCurrentFlowElement().getName(), execution.getCurrentActivityId(), task.getProjectId());
        } catch (Exception e) {
            //JELogger.error(Arrays.toString(e.getStackTrace()));
            //message = "Error executing script task with id = " + task.getTaskId() + " error message = " + e.getMessage();
            //msg.setMessage(message);
            //JELogger.error(Arrays.toString(e.getStackTrace()));
            throw new BpmnError("Error");
        }
        ZMQLogPublisher.publish(msg);

    }

}
