package io.je.serviceTasks;

import com.squareup.okhttp.Response;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;

import java.util.Arrays;

public class WebApiServiceTask extends ServiceTask{
    @Override
    public void execute(DelegateExecution execution) {
        WebApiTask task = (WebApiTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        JELogger.trace(WebApiServiceTask.class, " " + JEMessages.EXECUTING_WEB_API_TASK + task.getTaskId());
        Network network = null;
        if(task.hasBody()) {
            try {
                String json = task.getBody();
                network = new Network.Builder(task.getUrl()).hasBody(task.hasBody())
                        .toClass(task.getResponseClass()).withMethod(task.getHttpMethod()).withBodyType(task.getBodyType())
                        .withBody(json).build();
            }
            catch(Exception e) {
                JELogger.error(Arrays.toString(e.getStackTrace()));
                throw new BpmnError("Error = " + "Error");
            }
        }
        else {
            network = new Network.Builder(task.getUrl()).hasBody(task.hasBody())
                    .toClass(task.getResponseClass()).withMethod(task.getHttpMethod()).build();
        }
        try {
            Response response = network.call();
            JELogger.info(JEMessages.NETWORK_CALL_RESPONSE_IN_WEB_SERVICE_TASK + " = " + response.body().string());
        } catch (Exception e) {
            JELogger.error("Error = " + Arrays.toString(e.getStackTrace()));
            throw new BpmnError("Error");
        }
    }
}
