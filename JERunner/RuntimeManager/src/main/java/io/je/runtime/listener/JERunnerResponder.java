package io.je.runtime.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.project.exception.JEExceptionHandler;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEZMQResponse;
import io.je.utilities.beans.RunnerRequestObject;
import io.je.utilities.beans.ZMQResponseType;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.WorkflowBuildException;
import io.je.utilities.instances.DataModelRequester;
import io.je.utilities.log.JELogger;
import io.je.utilities.mapping.VariableModelMapping;
import io.je.utilities.models.VariableModel;
import io.je.utilities.runtimeobject.JEObject;
import org.springframework.beans.factory.annotation.Autowired;
import utils.log.LogLevel;
import utils.log.LogMessage;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQResponder;
import utils.zmq.ZMQType;

import java.util.HashMap;
import java.util.Locale;

import static io.je.utilities.instances.InstanceManager.createInstance;

public class JERunnerResponder extends ZMQResponder {

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    RuntimeDispatcher runtimeDispatcher = new RuntimeDispatcher();

    public JERunnerResponder(String url, int repPort, ZMQType bind) {
        super(url, repPort, bind);
    }


    @Override
    public void run() {
        while (isListening()) {
            JEZMQResponse response = new JEZMQResponse(ZMQResponseType.FAIL);
            RunnerRequestObject request;
            try {

                String data = this.getResponderSocket(ZMQType.BIND).recvStr(0);

                if (data != null && !data.isEmpty() && !data.equals("null")) {

                    JELogger.debug(JEMessages.ZMQ_REQUEST_RECEIVED + data, null, null, LogSubModule.JERUNNER, null);

                    request = objectMapper.readValue(data, RunnerRequestObject.class);

                    switch (request.getRequest()) {
                        case UPDATE_VARIABLE:
                            response = updateVariable(request.getRequestBody());
                            break;
                        case GET_VARIABLE:
                            response = readVariable(request.getRequestBody());
                            break;
                        case TRIGGER_EVENT:
                            response = triggerEvent(request.getRequestBody());
                            break;
                        case INFORM_USER:
                            response = informUser(request.getRequestBody());
                            break;
                        case SEND_LOG:
                            response = sendLog(request.getRequestBody());
                            break;
                        case GET_CLASS:
                            response = sendClass(request.getRequestBody());
                        default:
                            response.setErrorMessage(JEMessages.UNKNOWN_REQUEST);
                            break;

                    }

                    LoggerUtils.trace("JERunnerResponder : " + url + ":" + responderPort + " : sending : " + response);

                    sendResponse(response);

                }
            } catch (Exception e) {
                String errorMsg = JEExceptionHandler.getExceptionMessage(e);
                JELogger.error(JEMessages.ZMQ_FAILED_TO_RESPOND + errorMsg, null, null, LogSubModule.JERUNNER, null);

            }

        }

    }

    private JEZMQResponse triggerEvent(Object requestBody) {
        try {
            HashMap<String, Object> body = (HashMap<String, Object>) requestBody;

            runtimeDispatcher.triggerEvent((String) body.get(VariableModelMapping.PROJECT_ID),
                    (String) body.get("eventId"));
        } catch (Exception e) {
            LoggerUtils.logException(e);
            return new JEZMQResponse(ZMQResponseType.FAIL, e.getMessage());
        }

        return new JEZMQResponse(ZMQResponseType.SUCCESS);
    }

    private JEZMQResponse informUser(Object requestBody) throws WorkflowBuildException {
        try {
            HashMap<String, String> map = (HashMap<String, String>) requestBody;
            runtimeDispatcher.informUser(map.get("message"), map.get("projectName"), map.get("workflowName"));
        } catch (Exception e) {
            LoggerUtils.logException(e);
            return new JEZMQResponse(ZMQResponseType.FAIL, e.getMessage());
        }

        return new JEZMQResponse(ZMQResponseType.SUCCESS);
    }

    private JEZMQResponse sendLog(Object requestBody) {
        try {
            HashMap<String, String> map = (HashMap<String, String>) requestBody;

            LogMessage logMessage = new LogMessage(LogLevel.valueOf(map.get("LogLevel")
                    .toUpperCase(Locale.ROOT)), map.get("Message"), map.get("LogDate"),
                    map.get("ProjectId"), LogSubModule.JERUNNER,
                    map.get("ObjectId"), map.get("ObjectId"));
            runtimeDispatcher.sendLog(logMessage);
        } catch (Exception e) {
            LoggerUtils.logException(e);
            return new JEZMQResponse(ZMQResponseType.FAIL, e.getMessage());
        }

        return new JEZMQResponse(ZMQResponseType.SUCCESS);
    }

    /*private JEZMQResponse sendLogMessage(Object requestBody) {
        try {
            HashMap<String, Object> body = (HashMap<String, Object>) requestBody;

            runtimeDispatcher.triggerEvent((String) body.get(VariableModelMapping.PROJECT_ID),
                    (String) body.get("eventId"));
        } catch (Exception e) {
            e.printStackTrace();
            return new JEZMQResponse(ZMQResponseType.FAIL, e.getMessage());
        }

        return new JEZMQResponse(ZMQResponseType.SUCCESS);
    }*/
    private JEZMQResponse updateVariable(Object requestBody) {

        try {
            HashMap<String, Object> body = (HashMap<String, Object>) requestBody;

            runtimeDispatcher.writeVariableValue((String) body.get(VariableModelMapping.PROJECT_ID),
                    (String) body.get(VariableModelMapping.VARIABLE_ID),
                    String.valueOf(body.get(VariableModelMapping.VALUE)),
                    (boolean) body.get(VariableModelMapping.IGNORE_IF_SAME_VALUE));
        } catch (Exception e) {
            LoggerUtils.logException(e);
            return new JEZMQResponse(ZMQResponseType.FAIL, e.getMessage());
        }

        return new JEZMQResponse(ZMQResponseType.SUCCESS);

    }

    private JEZMQResponse readVariable(Object requestBody) {
        try {
            HashMap<String, Object> body = (HashMap<String, Object>) requestBody;
            JEZMQResponse rep = new JEZMQResponse(ZMQResponseType.SUCCESS);
            var variable = runtimeDispatcher.getVariable((String) body.get(VariableModelMapping.PROJECT_ID),
                    (String) body.get(VariableModelMapping.VARIABLE_ID));
            rep.setResponseObject(objectMapper.writeValueAsString(new VariableModel(variable)));

            return rep;
        } catch (Exception e) {
            LoggerUtils.logException(e);
            return new JEZMQResponse(ZMQResponseType.FAIL, e.getMessage());
        }

    }

    private JEZMQResponse sendClass(Object requestBody) {
        try {
            HashMap<String, Object> body = (HashMap<String, Object>) requestBody;
            JEZMQResponse rep = new JEZMQResponse(ZMQResponseType.SUCCESS);
            String data = DataModelRequester.getLastInstanceValue((String) body.get("instanceId"), true);
            JEObject instance = null;
            if (data != null && !data.isEmpty()) {
                instance = createInstance(data);
            }
            instance.setJeObjectCreationDate(null);
            instance.setJeObjectLastUpdate(null);
            instance.setJeObjectLastUpdate(null);
     /*       DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
            objectMapper.setDateFormat(df);
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);*/
            HashMap<String, String> response = new HashMap<>();
            response.put("instance", objectMapper.writeValueAsString((instance)));
            response.put("className", instance.getClassName());
            rep.setResponseObject(response);

            return rep;
        } catch (Exception e) {
            LoggerUtils.logException(e);
            return new JEZMQResponse(ZMQResponseType.FAIL, e.getMessage());
        }

    }

    private void sendResponse(JEZMQResponse response) {

        try {
            JELogger.debug(JEMessages.ZMQ_SENDING_RESPONSE + objectMapper.writeValueAsString(response), null, null, LogSubModule.JERUNNER, null);

            this.getResponderSocket(ZMQType.BIND)
                    .send(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.ZMQ_FAILED_TO_RESPOND + e.getMessage(), null, null, LogSubModule.JERUNNER, null);
        }

    }

}
