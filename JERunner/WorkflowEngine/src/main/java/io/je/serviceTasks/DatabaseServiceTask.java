package io.je.serviceTasks;

import io.je.utilities.apis.DatabaseApiHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;

import java.util.Arrays;

public class DatabaseServiceTask extends ServiceTask {

    public void execute(DelegateExecution execution) {

       DatabaseTask databaseTask = (DatabaseTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
       if(databaseTask != null) {
           try {
               int responseCode = DatabaseApiHandler.executeCommand(databaseTask.getDatabaseId(), databaseTask.getRequest());
               if(responseCode != 200 || responseCode != 204 ){
                   throw new BpmnError("Error");
               }
           }
           catch(Exception e) {
               JELogger.error(JEMessages.UNEXPECTED_ERROR +  Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME, null,
                       LogSubModule.JERUNNER, null);
               throw new BpmnError(String.valueOf(ResponseCodes.UNKNOWN_ERROR));
           }
       }
    }
}
