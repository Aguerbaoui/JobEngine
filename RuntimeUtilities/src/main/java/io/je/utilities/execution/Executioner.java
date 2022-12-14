package io.je.utilities.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.je.project.variables.VariableManager;
import io.je.utilities.apis.JERunnerRequester;
import io.je.utilities.beans.JEZMQResponse;
import io.je.utilities.beans.ZMQResponseType;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.CommandFailedException;
import io.je.utilities.exceptions.VariableNotFoundException;
import io.je.utilities.instances.ClassRepository;
import io.je.utilities.instances.InstanceManager;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.http.HttpStatus;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.network.Network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.je.utilities.constants.JEMessages.ERROR_OCCURRED_WHEN_SENDING_MESSAGE_TO;
import static io.je.utilities.constants.JEMessages.SENT_MESSAGE_SUCCESSFULLY_TO;
import static io.je.utilities.constants.WorkflowConstants.*;
import static io.je.utilities.dataflow.DataflowRequester.sendDataflowZMQ;

public class Executioner {

    public static final String SEND_EMAIL_AUTH = "SendEmailAuth";
    static final int MAX_THREAD_COUNT = 100;
    static ObjectMapper objectMapper = new ObjectMapper();
    static ExecutorService executor = Executors.newCachedThreadPool();
    static int test = 0;
    private Executioner() {

    }

    /***************************************
     * INFORM
     *********************************************************/

    /*
     * Execute inform block
     */
    public static void informRuleBlock(String projectId, String ruleId, String message, String logDate,
                                       String blockName) {
        try {
            executor.submit(() -> {
                JELogger.info(message, LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId, blockName);
            });
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.INFORM_BLOCK_ERROR, LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);

        }

    }

    /***************************************************
     * SETTERS
     *******************************************************************/
    /***************************************
     * SET DATA MODEL INSTANCE VALUE
     *********************************************************/
    /***** SET FROM STATIC VALUE *****/
    public static void updateInstanceAttributeValueFromStaticValue(String projectId, String ruleId, String blockName,
                                                                   String instanceId, String attributeName, Object value, boolean ignoreSameValue) {
        // Rework to use a callable for exception handling

        try {
            executor.submit(() -> {
                InstanceManager.writeToDataModelInstance(instanceId, attributeName, value, ignoreSameValue);
            });
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                    LogSubModule.RULE, ruleId, blockName);
        }
    }

    /***** SET FROM VARIABLE *****/
    /*
     * update instance attribute from variable
     */
    public static void updateInstanceAttributeValueFromVariable(String projectId, String ruleId, String blockName,
                                                                String instanceId, String attributeName, String variableId, boolean ignoreSameValue) {

        try {
            executor.submit(() -> {
                Object attribueValue;
                try {
                    attribueValue = VariableManager.getVariableValue(projectId, variableId)
                            .getValue();
                    InstanceManager.writeToDataModelInstance(instanceId, attributeName, attribueValue, ignoreSameValue);

                } catch (VariableNotFoundException e) {
                    LoggerUtils.logException(e);
                    JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                            LogSubModule.RULE, ruleId, blockName);
                }
            });
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                    LogSubModule.RULE, ruleId, blockName);
        }

    }

    /***** SET FROM ANOTHER DATAMODEL INSTANCE *****/
    public static void updateInstanceAttributeValueFromAnotherInstance(String projectId, String ruleId,
                                                                       String blockName, String sourceInstanceId, String sourceAttributeName, String destinationInstanceId,
                                                                       String destinationAttributeName, boolean ignoreSameValue) {

        try {
            executor.submit(() -> {
                try {
                    Object attribueValue = InstanceManager.getAttributeValue(sourceInstanceId, sourceAttributeName);
                    if (attribueValue == null) {
                        JELogger.error(JEMessages.READ_INSTANCE_VALUE_FAILED, LogCategory.RUNTIME, projectId,
                                LogSubModule.RULE, ruleId, blockName);
                        return;
                    }

                    InstanceManager.writeToDataModelInstance(destinationInstanceId, destinationAttributeName,
                            attribueValue, ignoreSameValue);

                } catch (Exception e) {
                    LoggerUtils.logException(e);
                    JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                            LogSubModule.RULE, ruleId, blockName);
                }
            });
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                    LogSubModule.RULE, ruleId, blockName);
        }

    }

    /***************************************
     * SET VARIABLE VALUE
     *********************************************************/
    /***** SET FROM STATIC VALUE *****/
    /*
     * update Variable from a static value
     */
    public static void updateVariableValue(String projectId, String ruleId, String variableId, Object value,
                                           String blockName, boolean ignoreIfSameValue) {

        try {
            executor.submit(() -> {
                try {
                    JEZMQResponse response = JERunnerRequester.updateVariable(projectId, variableId, value, ignoreIfSameValue);
                    if (response.getResponse() != ZMQResponseType.SUCCESS) {
                        JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + response.getErrorMessage(), LogCategory.RUNTIME, projectId,
                                LogSubModule.RULE, ruleId, blockName);
                    }
                } catch (Exception e) {
                    LoggerUtils.logException(e);
                    JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                            LogSubModule.RULE, ruleId, blockName);
                }
            });
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                    LogSubModule.RULE, ruleId, blockName);
        }

    }

    /***** SET FROM VARIABLE *****/

    /*
     * update Variable from another variable
     */
    public static void updateVariableValueFromAnotherVariable(String projectId, String ruleId, String sourceVariableId,
                                                              String destinationVariableId, String blockName, boolean ignoreIfSameValue) {

        try {
            executor.submit(() -> {
                try {
                    JERunnerRequester.updateVariable(projectId, destinationVariableId,
                            VariableManager.getVariableValue(projectId, sourceVariableId)
                                    .getValue(), ignoreIfSameValue);
                } catch (Exception e) {
                    LoggerUtils.logException(e);
                    JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                            LogSubModule.RULE, ruleId, blockName);
                }
            });
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                    LogSubModule.RULE, ruleId, blockName);
        }

    }

    /***** SET FROM ANOTHER DATAMODEL INSTANCE *****/

    /*
     * update Variable from a data model instance
     */
    public static void updateVariableValueFromDataModel(String projectId, String ruleId, String destinationVariableId,
                                                        String sourceInstanceId, String sourceAttributeName, String blockName, boolean ignoreIfSameValue) {

        try {
            executor.submit(() -> {
                try {
                    Object attribueValue = InstanceManager.getAttributeValue(sourceInstanceId, sourceAttributeName);
                    if (attribueValue != null) {
                        JEZMQResponse response = JERunnerRequester.updateVariable(projectId, destinationVariableId, attribueValue, ignoreIfSameValue);
                        if (response.getResponse() != ZMQResponseType.SUCCESS) {
                            JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + response.getErrorMessage(), LogCategory.RUNTIME, projectId,
                                    LogSubModule.RULE, ruleId, blockName);
                        }
                    } else {
                        JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED, LogCategory.RUNTIME, projectId,
                                LogSubModule.RULE, ruleId, blockName);
                    }

                } catch (Exception e) {
                    LoggerUtils.logException(e);
                    JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                            LogSubModule.RULE, ruleId, blockName);
                }
            });
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED + e.getMessage(), LogCategory.RUNTIME, projectId,
                    LogSubModule.RULE, ruleId, blockName);
        }

    }

    /***************************************
     * EVENT
     *********************************************************/

    /*
     * trigger an event + send event data to logging system
     */
    public static void triggerEvent(String projectId, String eventId, String eventName, String ruleId,
                                    String triggerSource) {

        executor.submit(() -> {
            try {
                JERunnerRequester.triggerEvent(projectId, eventId);
            } catch (Exception e) {
                LoggerUtils.logException(e);
                JELogger.error(JEMessages.EVENT_TRIGGER_FAIL, LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId,
                        triggerSource);

            }
        });

    }

    public static String getCurrentClassPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(File.pathSeparator);
        URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread()
                .getContextClassLoader();
        for (URL url : urlClassLoader.getURLs()) {
            //JELogger.info(JEClassLoader.class, url.getFile().substring(1));
            sb.append(url.getFile()
                            .substring(1)
                            .replace("%20", " "))
                    .append(File.pathSeparator);
        }
        return sb.toString()
                .replace("/", "\\");
    }

    public static Thread executeScript(String filePath) throws IOException, InterruptedException, CommandFailedException {
        //String classpathFolder = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE) + "\\..\\Job Engine\\libs\\*";
        //String command = "java" + " " + "-cp" + " \"" + classpathFolder  + getCurrentClassPath() + "\" " + filePath;
        return CommandExecutioner.runCode(filePath);
        //return ProcessRunner.executeCommandWithPidOutput(command);

        //long pid = ProcessRunner.executeCommandWithPidOutput(command);

    }

    /**
     * Send email
     */
    public static void sendEmail(String projectId, String ruleId, String blockName, Map body) {
        try {

            executor.submit(() -> {
                String url = SIOTHConfigUtility.getSiothConfig()
                        .getApis()
                        .getEmailAPI()
                        .getAddress() + SEND_EMAIL_AUTH;
                try {
                    String json = new ObjectMapper().writeValueAsString(body);

                    HashMap<String, Object> response = Network.makePostWebClientRequest(url, json);
                    HttpStatus code = (HttpStatus) response.get("code");


                    if (code.isError()) {
                        JELogger.error(JEMessages.MAIL_SERVICE_TASK_RESPONSE + ": \n" + response.get("message"), LogCategory.RUNTIME, projectId,
                                LogSubModule.JERUNNER, ruleId, blockName);

                    } else {
                        JELogger.control(JEMessages.EMAIL_SENT_SUCCESSFULLY, LogCategory.RUNTIME, projectId,
                                LogSubModule.JERUNNER, ruleId, blockName);
                    }

                } catch (Exception e) {
                    LoggerUtils.logException(e);
                    JELogger.error(JEMessages.UNEXPECTED_ERROR + e.getMessage(), LogCategory.RUNTIME, projectId,
                            LogSubModule.JERUNNER, ruleId, blockName);

                }
            });
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.EMAIL_BLOCK_ERROR, LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);

        }

    }

    /**
     * Take dataflow action
     */
    public static void TakeDataFlowAction(String projectId, String ruleId, String blockName, Map body) {
        try {
            Map B = body;
            List<?> actions = (List<?>) body.get("actions");
            executor.submit(() -> {
                actions.forEach(act -> {
                    HashMap<String, Object> requestParams = new HashMap<>();
                    List<String> ConnectorsID = (List<String>) ((Map)act).get("connectorsID");
                    String action = ((String)((Map)act).get("startStop")).toUpperCase();
                    String dataflowID = (String)((Map)act).get("dataFlowID");
                    String dataflowName = (String)((Map)act).get("name");
                    requestParams.put("idDataProject", dataflowID);
                    requestParams.put("key", dataflowName);

                    if (ConnectorsID.isEmpty()) {
                        action = action.toUpperCase() + '_' + "DATAFLOW";
                        requestParams.put("type", action);
                        sendDataflowZMQ(projectId, ruleId, requestParams);
                    }
                    else {
                        action = action.toUpperCase() + '_' + "CONNECTOR";
                        requestParams.put("type", action);
                        ConnectorsID.forEach(id -> {
                            requestParams.put("AgentConfig", id);
                            sendDataflowZMQ(projectId, ruleId, requestParams);
                        });
                    }
                });
           });
        } catch (Exception e) {
            LoggerUtils.logException(e);
        }
    }

    /**
     * Send sms using twilio or another server
     */
    public static void sendSMS(String projectId, String ruleId, String blockName, Map body, String messageBody) {
        try {

            executor.submit(() -> {
                if ((boolean) body.get(TWILIO_SERVER) == true) {
                    sendTwilioSMS(projectId, ruleId, blockName, body, messageBody);
                }
                if ((boolean) body.get(TWILIO_SERVER) == false) {
                    sendSMSEagle(projectId, ruleId, blockName, body, messageBody);
                }
            });

        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.SMS_BLOCK_ERROR, LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);

        }

    }

    private static void sendSMSEagle(String projectId, String ruleId, String blockName, Map body, String messageBody) {
        String sendTo ="";
        List<String> phoneNumbers = (List<String>) body.get(RECEIVER_PHONE_NUMBERS);
        phoneNumbers.removeAll(Collections.singleton(null));
        String validity = (String) body.get(VALIDITY);
        String inputType = (String) body.get(INPUT_TYPE);
        String modem = (String) body.get(MODEM);
        boolean sendAsUnicode = (boolean) body.get(SEND_AS_UNICODE);
        boolean priority = (boolean) body.get(PRIORITY);
        String smsType = (String) body.get(SMS_EAGLE_TYPE);
        String accountSID = (String) body.get(TWILIO_ACCOUNT_SID);
        String accountToken = (String) body.get(TWILIO_ACCOUNT_TOKEN);
        String URI = (String) body.get(SMS_URI);
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

    private static void sendTwilioSMS(String projectId, String ruleId, String blockName, Map body, String messageBody) {
        Twilio.init((String) body.get(TWILIO_ACCOUNT_SID), (String) body.get(TWILIO_ACCOUNT_TOKEN));
        List<String> phoneNumbers = (List<String>) body.get(RECEIVER_PHONE_NUMBERS);
        String twilioPhoneNumber = (String) body.get(TWILIO_SENDER_PHONE_NUMBER);

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

    public static void executeScript(String name, String processId, String projectId, int timeout) {
        Class<?> loadClass = null;
        try {
            loadClass = ClassRepository
                    .getClassByName(name);
            Method method = loadClass.getDeclaredMethods()[0];
            method.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            LoggerUtils.logException(e);
        }

    }
}
