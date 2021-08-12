package io.je.serviceTasks;

import com.squareup.okhttp.Response;
import io.je.utilities.apis.HttpMethod;
import io.je.utilities.config.Utility;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.logger.*;
import io.je.utilities.network.Network;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import java.time.LocalDateTime;
import java.util.Arrays;

public class DatabaseServiceTask extends ServiceTask {


    public static final String EXECUTE_DATABASE_COMMAND = "/api/DBBridge/Execute";

    public void execute(DelegateExecution execution) {

       DatabaseTask databaseTask = (DatabaseTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
       if(databaseTask != null) {
           String url = Utility.getSiothConfig().getApis().getDatabaseAPI().getAddress() + EXECUTE_DATABASE_COMMAND;
           try {
               Network network = new Network.Builder(url).hasBody(false).hasParameters(true).withParam("DBIdentifier",databaseTask.getDatabaseId() )
                       .withMethod(HttpMethod.GET).withParam("Command", databaseTask.getRequest())
                       .build();
               Response response = network.call();
               JELogger.info(JEMessages.DB_SERVICE_TASK_RESPONSE + " = " + response.body().string(),  LogCategory.RUNTIME,
                       databaseTask.getProjectId(), LogSubModule.WORKFLOW, null);

               if(response.code() != 200 || response.code() != 204 ) {
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
