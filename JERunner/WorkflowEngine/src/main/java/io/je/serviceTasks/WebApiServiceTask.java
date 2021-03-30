package io.je.serviceTasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;
import org.activiti.engine.delegate.DelegateExecution;

import java.io.IOException;

public class WebApiServiceTask extends ServiceTask{
    @Override
    public void execute(DelegateExecution execution) {
        WebApiTask task = (WebApiTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        JELogger.trace(WebApiServiceTask.class, " Executing web api task" + task.getTaskId());
        Network network = null;
        if(task.hasBody()) {
            try {
                String json = new ObjectMapper().writeValueAsString(task.getBody());
                network = new Network.Builder(task.getUrl()).hasBody(task.hasBody())
                        .toClass(task.getResponseClass()).withMethod(task.getHttpMethod()).withBodyType(task.getBodyType())
                        .withBody(json).build();
            }
            catch(Exception e) {}
        }
        else {
            network = new Network.Builder(task.getUrl()).hasBody(task.hasBody())
                    .toClass(task.getResponseClass()).withMethod(task.getHttpMethod()).build();
        }
        try {
            Response response = network.call();
            JELogger.info("Network call response in web service task = " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
