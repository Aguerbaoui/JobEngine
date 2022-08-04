package io.je.serviceTasks;

import io.je.utilities.apis.DatabaseApiHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.util.Arrays;

public class DatabaseServiceTask extends ServiceTask {

    public void execute(DelegateExecution execution) {

       DatabaseTask databaseTask = (DatabaseTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
       if(databaseTask != null) {
           try {
               String response = DatabaseApiHandler.executeCommand(databaseTask.getDatabaseId(), databaseTask.getRequest());
               if(response == null){
                   throw new BpmnError("Error");
               }
           }
           catch(Exception e) {
               JELogger.logException(e);
               JELogger.error(JEMessages.UNEXPECTED_ERROR +  e.getMessage(), LogCategory.RUNTIME, null,
                       LogSubModule.JERUNNER, null);
               throw new BpmnError("Error");
           }
       }
    }
}
