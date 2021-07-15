package io.je.serviceTasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import io.je.utilities.apis.BodyType;
import io.je.utilities.apis.HttpMethod;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.*;
import io.je.utilities.network.Network;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

public class DatabaseServiceTask extends ServiceTask {


    public static final String EXECUTE_DATABASE_COMMAND = "/api/DBBridge/Execute";

    public void execute(DelegateExecution execution) {

       DatabaseTask databaseTask = (DatabaseTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
       if(databaseTask != null) {
           String url = JEConfiguration.getDatabaseApiUrl() + EXECUTE_DATABASE_COMMAND;
           try {
               Network network = new Network.Builder(url).hasBody(false).hasParameters(true).withParam("DBIdentifier",databaseTask.getDatabaseId() )
                       .withMethod(HttpMethod.GET).withParam("Command", databaseTask.getRequest())
                       .build();
               Response response = network.call();
               JELogger.info(JEMessages.DB_SERVICE_TASK_RESPONSE + " = " + response.body().string());
               LogMessage msg = new LogMessage(LogLevel.INFORM,  "DB task response = " + response.body().string(),  LocalDateTime.now().toString(), "JobEngine",  databaseTask.getProjectId(),
                       databaseTask.getProcessId(), LogSubModule.WORKFLOW, databaseTask.getTaskName(), null, "Log", "") ;
               if(response.code() != 200 || response.code() != 204 ) {
                   msg.setMessage("Database task failed with response code = " + response.code());
                   throw new BpmnError("Error");
               }
               ZMQLogPublisher.publish(msg);
           }
           catch(Exception e) {
               JELogger.error(Arrays.toString(e.getStackTrace()));
               throw new BpmnError("Error");
           }
       }
    }
}
