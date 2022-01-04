package io.je.serviceTasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;

import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.network.BodyType;
import utils.network.HttpMethod;
import utils.network.Network;

import java.util.Arrays;
import java.util.HashMap;

import static io.je.utilities.constants.WorkflowConstants.*;

public class MailServiceTask extends ServiceTask {


    public static final String SEND_EMAIL_AUTH = "SendEmailAuth";
    public static final String SEND_EMAIL = "SendEmail";

    public void execute(DelegateExecution execution) {

        MailTask task = (MailTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        HashMap<String, Object> attributes = new HashMap<>();
        if(task.isbUseDefaultCredentials()) {
            attributes.put(WorkflowConstants.ENABLE_SSL, task.isbEnableSSL());
            attributes.put(WorkflowConstants.USE_DEFAULT_CREDENTIALS, task.isbUseDefaultCredentials());
        }
        else {
            attributes.put(USERNAME, task.getStrUserName());
            attributes.put(PASSWORD, task.getStrPassword());
        }
        attributes.put(PORT, task.getiPort());
        attributes.put(SENDER_ADDRESS, task.getStrSenderAddress());
        attributes.put(SEND_TIME_OUT, task.getiSendTimeOut());
        attributes.put(RECEIVER_ADDRESS, task.getLstRecieverAddress());
        attributes.put(EMAIL_MESSAGE, task.getEmailMessage());
        attributes.put(SMTP_SERVER, task.getStrSMTPServer());
        attributes.put(CC_LIST, task.getLstCCs());
        attributes.put(BCC_LIST, task.getLstBCCs());
        attributes.put(ATTACHEMENT_URLS, task.getLstAttachementPaths());
        attributes.put(UPLOADED_FILES_PATHS, task.getLstUploadedFiles());
        String url = task.isbUseDefaultCredentials() ?  SIOTHConfigUtility.getSiothConfig().getApis().getEmailAPI().getAddress() + SEND_EMAIL : SIOTHConfigUtility.getSiothConfig().getApis().getEmailAPI().getAddress() + SEND_EMAIL_AUTH;
        try { //http://192.168.4.128:14003/api/SIOTHEmail/SendEmailAuth
            String json = new ObjectMapper().writeValueAsString(attributes);
            Network network = new Network.Builder(url).hasBody(true)
                    .withMethod(HttpMethod.POST).withBodyType(BodyType.JSON)
                    .withBody(json).build();
            Response response = network.call();
            JELogger.debug(JEMessages.MAIL_SERVICE_TASK_RESPONSE + " = " + response.body().string(),  LogCategory.RUNTIME,
                    task.getProjectId(), LogSubModule.WORKFLOW, null);

            if(response.networkResponse().code() != 200 && response.networkResponse().code() != 204 ) {
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
