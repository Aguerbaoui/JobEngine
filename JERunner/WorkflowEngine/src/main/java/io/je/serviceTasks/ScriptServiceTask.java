package io.je.serviceTasks;

import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.execution.Executioner;
import io.je.utilities.log.ZMQLogPublisher;
import utils.log.LogLevel;
import utils.log.LogMessage;
import utils.log.LogSubModule;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;

import java.time.LocalDateTime;

public class ScriptServiceTask extends ServiceTask {

    public void execute(DelegateExecution execution) {
        //throw new BpmnError("testErrorRef");
        //script path generationPath + packageName + "\\" + className +".java" ;
        ScriptTask task = (ScriptTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        String message = "[Task= "+task.getTaskName()+"]Executing script task"  ;
        LogMessage msg = new LogMessage(LogLevel.Inform,  message,  LocalDateTime.now().toString(),   task.getProjectId(),
                LogSubModule.WORKFLOW, task.getTaskName()) ;
        try {
            //Executioner.executeScript(execution.getCurrentFlowElement().getName(), execution.getCurrentActivityId(), task.getProjectId(), task.getTimeout());
            String filePath = ConfigurationConstants.JAVA_GENERATION_PATH + ClassBuilderConfig.SCRIPTS_PACKAGE + "\\" + execution.getCurrentFlowElement().getName() +".java";
            long pid = Executioner.executeScript(filePath);
            task.setPid(pid);
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
