package io.je.serviceTasks;

import com.squareup.okhttp.Response;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import utils.network.Network;
import utils.string.StringSub;

import java.util.Arrays;

public class WebApiServiceTask extends ServiceTask{
    @Override
    public void execute(DelegateExecution execution) {
        WebApiTask task = (WebApiTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        JELogger.debug(JEMessages.EXECUTING_WEB_API_TASK + task.getTaskId(),
                LogCategory.RUNTIME, task.getProjectId(),
                LogSubModule.WORKFLOW, task.getTaskId());
        Network network = null;
        if(task.hasBody()) {
            try {
                String json = task.getBody();
                json = StringSub.replace(task.getProjectId(), json);
                network = new Network.Builder(task.getUrl()).hasBody(task.hasBody())
                        .toClass(task.getResponseClass()).withMethod(task.getHttpMethod()).withBodyType(task.getBodyType())
                        .withBody(json).build();
            }
            catch(Exception e) {
                JELogger.error(JEMessages.UNEXPECTED_ERROR +  Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME, null,
                        LogSubModule.JERUNNER, null);
                throw new BpmnError(String.valueOf(ResponseCodes.UNKNOWN_ERROR));
            }
        }
        else {
            network = new Network.Builder(task.getUrl()).hasBody(task.hasBody())
                    .toClass(task.getResponseClass()).withMethod(task.getHttpMethod()).build();
        }
        try {
            Response response = network.call();
            JELogger.info(JEMessages.NETWORK_CALL_RESPONSE_IN_WEB_SERVICE_TASK + " = " + response.body().string(),  LogCategory.RUNTIME,
                    task.getProjectId(), LogSubModule.WORKFLOW, null);
        } catch (Exception e) {
            JELogger.error(JEMessages.UNEXPECTED_ERROR +  Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME, null,
                    LogSubModule.JERUNNER, null);
            throw new BpmnError(String.valueOf(ResponseCodes.UNKNOWN_ERROR));
        }
    }
}
