package io.je.serviceTasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import io.je.utilities.apis.BodyType;
import io.je.utilities.apis.HttpMethod;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static io.je.utilities.constants.WorkflowConstants.*;
import static io.je.utilities.constants.WorkflowConstants.SMTP_SERVER;

public class MailServiceTask extends ServiceTask {


    public void execute(DelegateExecution execution) {

        MailTask task = (MailTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(WorkflowConstants.ENABLE_SSL, task.isbEnableSSL());
        attributes.put(WorkflowConstants.USE_DEFAULT_CREDENTIALS, task.isbUseDefaultCredentials());
        attributes.put(WorkflowConstants.PORT, task.getiPort());
        attributes.put(WorkflowConstants.SENDER_ADDRESS, task.getStrSenderAddress());
        attributes.put(SEND_TIME_OUT, task.getiSendTimeOut());
        attributes.put(RECEIVER_ADDRESS, task.getLstRecieverAddress());
        attributes.put(EMAIL_MESSAGE, task.getEmailMessage());
        attributes.put(SMTP_SERVER, task.getStrSMTPServer());
        try {
            String json = new ObjectMapper().writeValueAsString(attributes);
            Network network = new Network.Builder(JEConfiguration.getEmailApiUrl()).hasBody(true)
                    .withMethod(HttpMethod.POST).withBodyType(BodyType.JSON)
                    .withBody(json).build();
            Response response = network.call();
            JELogger.info("Network call response in Mail service task = " + response.body().string());
        }
        catch(Exception e) {
            JELogger.error(Arrays.toString(e.getStackTrace()));
        }

    }
}
