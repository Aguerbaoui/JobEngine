package io.je.serviceTasks;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.je.utilities.log.JELogger;
import org.activiti.engine.delegate.DelegateExecution;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static io.je.utilities.constants.JEMessages.ERROR_OCCURRED_WHEN_SENDING_MESSAGE_TO;
import static io.je.utilities.constants.JEMessages.SENT_MESSAGE_SUCCESSFULLY_TO;

public class SMSTaskService extends ServiceTask {
    @Override
    public void execute(DelegateExecution execution) {
        SMSTask smsTask = (SMSTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        if (smsTask.isTwilioServer() == true) {
            sendTwilioSMS(smsTask.getProjectId(), smsTask.getWorkflowId(), smsTask.getTaskName(), smsTask, smsTask.getMessage());
        }
        if (smsTask.isTwilioServer() == false) {
            sendSMSEagle(smsTask.getProjectId(), smsTask.getWorkflowId(), smsTask.getTaskName(), smsTask, smsTask.getMessage());
        }
    }

    private static void sendSMSEagle(String projectId, String ruleId, String blockName, SMSTask body, String messageBody) {
        String sendTo ="";
        List<String> phoneNumbers = body.getReceiverPhoneNumbers();
        phoneNumbers.removeAll(Collections.singleton(null));
        String validity = body.getValidity();
        String inputType = body.getInputType();
        String modem = body.getModem();
        boolean sendAsUnicode = body.isSendAsUnicode();
        boolean priority = body.isPriority();
        String smsType = body.getSmsType();
        String accountSID = body.getAccountSID();
        String accountToken = body.getAccountToken();
        String URI = body.getURI();
        String prio = "";
        String uni = "";
        if (modem.equals("0")) modem = "";
        if (modem.equals("1") || modem.equals("2")) modem = "&modem_no=" + modem;
        if (smsType.equals("flash")) smsType = "1";
        if (smsType.equals("sms")) smsType = "0";
        if (priority) prio = "1";
        if (!priority) prio = "0";
        if (sendAsUnicode) uni = "1";
        if (!sendAsUnicode) uni = "0";
        if (inputType.equals("1")) {
            URI = URI + "/http_api/send_sms";
            sendTo = "&to=";
        }
        if (inputType.equals("2")) {
            URI = URI + "/http_api/send_tocontact";
            sendTo = "&contactname=";
        }
        if (inputType.equals("3")) {
            URI = URI + "/http_api/send_togroup";
            sendTo = "&groupname=";
        }
        if (accountSID == null) URI = URI + "?access_token=" + accountToken;
        if (accountSID != null) URI = URI + "?login=" + accountSID + "&pass=" + accountToken;
        URI = URI + "&unicode=" + uni + "&highpriority=" + prio + "&flash=" + smsType + modem + "&validity=" + validity + "&message=" + messageBody.replace(" ", "%20");
        final String base = URI + sendTo;
        phoneNumbers.forEach(number -> {
            HttpURLConnection conn;
            BufferedReader reader;
            String line;
            String result = "";
            String  baseUrl = base + number.replace(" ", "%20") ;
            URL url = null;
            try {
                url = new URL(baseUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            conn.disconnect();
            if (result.toLowerCase().indexOf("OK;".toLowerCase()) != -1 ) {
                JELogger.control(SENT_MESSAGE_SUCCESSFULLY_TO + new PhoneNumber(number), LogCategory.RUNTIME, projectId,
                        LogSubModule.JERUNNER, ruleId, blockName);
            }
            else {
                JELogger.error(ERROR_OCCURRED_WHEN_SENDING_MESSAGE_TO + new PhoneNumber(number) + ": " + result, LogCategory.RUNTIME, projectId,
                        LogSubModule.JERUNNER, ruleId, blockName);
            }
            System.out.println(result);
        });
    }

    private static void sendTwilioSMS(String projectId, String ruleId, String blockName, SMSTask body, String messageBody) {
        Twilio.init(body.getAccountSID(), body.getAccountToken());
        List<String> phoneNumbers = body.getReceiverPhoneNumbers();
        String twilioPhoneNumber = body.getSenderPhoneNumber();

        phoneNumbers.forEach(number -> {


            try {
                Message message = Message.creator(
                                new PhoneNumber(number),
                                new PhoneNumber(twilioPhoneNumber),
                                messageBody)
                        .create();
                JELogger.control(SENT_MESSAGE_SUCCESSFULLY_TO + new PhoneNumber(number), LogCategory.RUNTIME, projectId,
                        LogSubModule.JERUNNER, ruleId, blockName);
            } catch (Exception e) {
                LoggerUtils.logException(e);
                JELogger.error(ERROR_OCCURRED_WHEN_SENDING_MESSAGE_TO + new PhoneNumber(number) + ": " + e.getMessage(), LogCategory.RUNTIME, projectId,
                        LogSubModule.JERUNNER, ruleId, blockName);
            }
        });
    }
}
