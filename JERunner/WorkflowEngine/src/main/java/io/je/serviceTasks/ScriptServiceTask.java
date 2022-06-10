package io.je.serviceTasks;

import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.execution.Executioner;
import io.je.utilities.log.JELogger;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import utils.log.LogCategory;
import utils.log.LogSubModule;

public class ScriptServiceTask extends ServiceTask {

    public void execute(DelegateExecution execution) {
        //throw new BpmnError("testErrorRef");
        //script path generationPath + packageName + "\\" + className +".java" ;
        ScriptTask task = (ScriptTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        String message = "Executing script task";
        JELogger.control(message,
                LogCategory.RUNTIME, task.getProjectId(),
                LogSubModule.WORKFLOW, task.getWorkflowId(), task.getTaskName());
        try {
            //Executioner.executeScript(execution.getCurrentFlowElement().getName(), execution.getCurrentActivityId(), task.getProjectId(), task.getTimeout());
            String filePath = ConfigurationConstants.JAVA_GENERATION_PATH + ClassBuilderConfig.SCRIPTS_PACKAGE + "\\" + execution.getCurrentFlowElement()
                    .getName() + ".java";
            Thread runningThread = Executioner.executeScript(filePath);
            task.setPid(Long.valueOf(runningThread.getName()));
            runningThread.join();
            //?  can we add all variables here, so they can be accessible to other blocks
            execution.setVariable("test", "555");
            //System.out.println("done waiting for script");
        } catch (Exception e) {
            throw new BpmnError("Error");
        }


    }

}
