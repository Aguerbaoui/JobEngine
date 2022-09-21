package io.je.serviceTasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.http.HttpStatus;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.network.Network;

import java.util.HashMap;

import static io.je.utilities.constants.WorkflowConstants.*;

public class MailServiceTask extends ServiceTask {


    public static final String SEND_EMAIL_AUTH = "SendEmailAuth";
    public static final String SEND_EMAIL = "SendEmail";

    // FIXME move to test main java ...
    /*
    public static void main(String... args) {
        String json = "{\n" +
                "\"strSenderAddress\": \"ikhdhiri@integrationobjects.com\",\n" +
                "\"lstRecieverAddress\": [\n" +
                "\"ikhdhiri@integrationobjects.com\", \"njendoubi@integrationobjects.com\"\n" +
                "],\n" +
                "\"strSMTPServer\": \"secure.emailsrvr.com\",\n" +
                "\"iPort\": 25,\n" +
                "\"iSendTimeOut\": 1000,\n" +
                "\"emailMessage\": {\n" +
                "\"strSubject\": \"EmailBlock test\",\n" +
                "\"strBody\": \"Hi, This is a test of the email block\"\n" +
                "},\n" +
                "\"lstCCs\": [\n" +
                "\"\"\n" +
                "],\n" +
                "\"lstBCCs\": [\n" +
                "\"\"\n" +
                "], \n" +
                "\"lstAttachementPaths\": [\n" +
                "\"\"\n" +
                "],\n" +
                "\"lstUploadedFiles\": [\n" +
                "\"\"\n" +
                "],\n" +
                "\"strUserName\": \"ikhdhiri@integrationobjects.com\",\n" +
                "\"strPassword\": \"IKh=ObjectS@2131251\"\n" +
                "}";
        Network network = new Network.Builder("http://localhost:14003/api/SIOTHEmail/SendEmailAuth").hasBody(true)
                .withMethod(HttpMethod.POST)
                .withBodyType(BodyType.JSON)
                .withBody(json)
                .build();

        Response response = null;
        try {
            response = network.call();
            LoggerUtils.debug("MailServiceTask main response.body().string() : " + response.body().string());
        } catch (IOException exp) {
            LoggerUtils.logException(exp);

        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }
*/

    public void execute(DelegateExecution execution) {

        MailTask task = (MailTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        HashMap<String, Object> attributes = new HashMap<>();
        if (task.isbUseDefaultCredentials()) {
            attributes.put(WorkflowConstants.ENABLE_SSL, task.isbEnableSSL());
            attributes.put(WorkflowConstants.B_REQUIRE_AUTHENTICATION, task.isbUseDefaultCredentials());
        } else {
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
        String url = task.isbUseDefaultCredentials() ? SIOTHConfigUtility.getSiothConfig()
                .getApis()
                .getEmailAPI()
                .getAddress() + SEND_EMAIL : SIOTHConfigUtility.getSiothConfig()
                .getApis()
                .getEmailAPI()
                .getAddress() + SEND_EMAIL_AUTH;
        try {
            String json = new ObjectMapper().writeValueAsString(attributes);

            HashMap<String, Object> response = Network.makePostWebClientRequest(url, json);
            HttpStatus code = (HttpStatus) response.get("code");


            if (code.isError()) {
                JELogger.error(JEMessages.MAIL_SERVICE_TASK_RESPONSE + ": \n" + response.get("message"), LogCategory.RUNTIME, task.getProjectId(),
                        LogSubModule.JERUNNER, task.getWorkflowId(), task.getTaskName());
                throw new BpmnError("Error");

            } else {
                JELogger.control(JEMessages.EMAIL_SENT_SUCCESSFULLY, LogCategory.RUNTIME, task.getProjectId(),
                        LogSubModule.JERUNNER, task.getWorkflowId(), task.getTaskName());
            }

        } catch (Exception e) {
            LoggerUtils.logException(e);

            JELogger.error(JEMessages.UNEXPECTED_ERROR + e.getMessage(), LogCategory.RUNTIME, task.getProjectId(),
                    LogSubModule.JERUNNER, task.getWorkflowId(), task.getTaskName());
            throw new BpmnError("Error");
        }

    }

}
