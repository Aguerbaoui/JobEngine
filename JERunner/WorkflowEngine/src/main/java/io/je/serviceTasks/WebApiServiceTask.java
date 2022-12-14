package io.je.serviceTasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import okhttp3.Response;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.network.Network;
import utils.string.StringSub;

import java.util.HashMap;

public class WebApiServiceTask extends ServiceTask {
    @Override
    public void execute(DelegateExecution execution) {
        WebApiTask task = (WebApiTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        JELogger.debug(JEMessages.EXECUTING_WEB_API_TASK + task.getTaskId(),
                LogCategory.RUNTIME, task.getProjectId(),
                LogSubModule.WORKFLOW, task.getTaskId());
        Network network = null;
        if (task.getStringBody() != null && task.getStringBody() != "") {
            try{
                HashMap<String, String> body = new ObjectMapper().readValue(task.getStringBody(), HashMap.class);
                task.setBody(body);
            } catch(Exception e) {
                JELogger.logException(e);

                JELogger.error(JEMessages.UNEXPECTED_ERROR + e.getMessage(), LogCategory.RUNTIME, task.getProjectId(),
                        LogSubModule.JERUNNER, task.getWorkflowId());
                throw new BpmnError("Error");
            }
        }
        if (task.hasBody()) {
            try {
                String json = task.getBody();
                json = StringSub.replace(task.getProjectId(), json);
                network = new Network.Builder(task.getUrl()).hasBody(task.hasBody())
                        .toClass(task.getResponseClass()).withMethod(task.getHttpMethod()).withBodyType(task.getBodyType())
                        .withBody(json).withAuthScheme(task.getAuthScheme()).withAuthentication(task.getAuthentication()).build();
            } catch (Exception e) {
                JELogger.logException(e);

                JELogger.error(JEMessages.UNEXPECTED_ERROR + e.getMessage(), LogCategory.RUNTIME, task.getProjectId(),
                        LogSubModule.JERUNNER, task.getWorkflowId());
                throw new BpmnError("Error");
            }
        } else {
            network = new Network.Builder(task.getUrl()).hasBody(task.hasBody())
                    .toClass(task.getResponseClass()).withMethod(task.getHttpMethod()).
                    withAuthScheme(task.getAuthScheme()).withAuthentication(task.getAuthentication()).build();
        }
        Response response = null;
        try {
            response = network.call();
            if (response != null && response.body() != null) {
                JELogger.info(JEMessages.NETWORK_CALL_RESPONSE_IN_WEB_SERVICE_TASK + " = " + response.body().string(), LogCategory.RUNTIME,
                        task.getProjectId(), LogSubModule.WORKFLOW, task.getWorkflowId());
            }
        } catch (Exception e) {
            JELogger.logException(e);
            JELogger.error(JEMessages.UNEXPECTED_ERROR + e.getMessage(), LogCategory.RUNTIME, task.getProjectId(),
                    LogSubModule.JERUNNER, task.getWorkflowId());
            throw new BpmnError("Error");
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

}
