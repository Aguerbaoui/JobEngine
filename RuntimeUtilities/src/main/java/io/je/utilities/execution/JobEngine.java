package io.je.utilities.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import io.je.utilities.apis.DatabaseApiHandler;
import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.InformModel;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.VariableModel;
import io.je.utilities.beans.JEResponse;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogLevel;
import utils.log.LogMessage;
import utils.log.LogSubModule;
import utils.network.AuthScheme;
import utils.network.HttpMethod;
import utils.network.Network;
import utils.string.StringUtilities;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import static io.je.utilities.constants.APIConstants.INFORM_USER;

public class JobEngine {

    /*
     * Run a workflow from script task
     * */
    public static int runWorkflow(String projectId, String workflowName) {

        try {
            JEResponse response = JEBuilderApiHandler.runWorkflow(projectId, workflowName);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.WORKFLOW_RUN_ERROR, projectId, LogLevel.Error, workflowName, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    /*
     * Stop a workflow from script task
     * */
    public static int stopWorkflow(String projectId, String workflowName) {

        try {
            JEResponse response = JEBuilderApiHandler.stopWorkflow(projectId, workflowName);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.DELETE_WORKFLOW_FAILED, projectId, LogLevel.Error, workflowName, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    /*
     * Add a variable to runner from script task
     * */
    public static int addIntegerVariable(String varProjectId, String varName, int varValue) {

        try {
            JEResponse response = JEBuilderApiHandler.addVariable(varProjectId, varName, getVariableBody(varName, varProjectId, varName, varValue, "int"));
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, varProjectId, LogLevel.Error, varName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static int addLongVariable(String varProjectId, String varName, long varValue) {

        try {
            JEResponse response = JEBuilderApiHandler.addVariable(varProjectId, varName, getVariableBody(varName, varProjectId, varName, varValue, "long"));
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, varProjectId, LogLevel.Error, varName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static int addDoubleVariable(String varProjectId, String varName, double varValue) {

        try {
            JEResponse response = JEBuilderApiHandler.addVariable(varProjectId, varName, getVariableBody(varName, varProjectId, varName, varValue, "double"));
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, varProjectId, LogLevel.Error, varName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    /*
     * Add a variable to runner from script task
     * */
    public static int addStringVariable(String varProjectId, String varName, String varValue) {

        try {
            JEResponse response = JEBuilderApiHandler.addVariable(varProjectId, varName, getVariableBody(varName, varProjectId, varName, varValue, "string"));
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, varProjectId, LogLevel.Error, varName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    /*
     * Add a variable to runner from script task
     * */
    public static int addBooleanVariable(String varProjectId, String varName, String varValue) {

        try {
            JEResponse response = JEBuilderApiHandler.addVariable(varProjectId, varName, getVariableBody(varName, varProjectId, varName, varValue, "boolean"));
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, varProjectId, LogLevel.Error, varName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    public static int addVariables(ArrayList<VariableModel> vars) {

        if (vars != null) {
            for (VariableModel variableModel : vars) {
                try {
                    JEResponse response = JEBuilderApiHandler.addVariable(variableModel.getProjectId(),
                            variableModel.getId(), getVariableBody(variableModel.getId(), variableModel.getProjectId(),
                                    variableModel.getName(), variableModel.getValue(), variableModel.getType()));
                } catch (Exception e) {
                    sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, variableModel.getProjectId(), LogLevel.Error,
                            variableModel.getId(), LogCategory.RUNTIME, LogSubModule.VARIABLE);
                    return ResponseCodes.UNKNOWN_ERROR;
                }
            }
        }

        return ResponseCodes.CODE_OK;
    }

    //Get variable body from parameters
    public static Map<String, Object> getVariableBody(String varId, String varProjectId, String varName, Object varValue, String type) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("id", varId);
        body.put("projectId", varProjectId);
        body.put("name", varName);
        body.put("type", type);
        body.put("value", varValue);
        return body;
    }

    /*
     * Add a variable to runner from script task
     * */
    public static int removeVariable(String projectId, String variableName) {
        try {

            JEResponse response = JEBuilderApiHandler.removeVariable(projectId, variableName);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_DELETING_VARIABLE_FROM_PROJECT, projectId, LogLevel.Error,
                    variableName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    /*
     * Inform user of a message with inform level
     * */
    public static int informUser(String message, String projectName) {

        //Network.makeGetNetworkCallWithResponse()
        int respCode = -1;

        try {
            InformModel informModel = new InformModel(message, projectName);
            JEResponse response = JEBuilderApiHandler.informUser(informModel);
            System.out.println(response.getMessage());
            respCode = response.getCode();
        } catch (JERunnerErrorException e) {
            e.printStackTrace();
            sendLogMessage(JEMessages.ERROR_SENDING_INFORM_MESSAGE, projectName, LogLevel.Error,
                    "", LogCategory.RUNTIME, LogSubModule.JERUNNER);
        }

        return respCode;
    }

    /*
     * Inform user of a message with level
     * */
    public static int sendLogMessage(String message, String projectName, LogLevel level, String objectName,
                                     LogCategory logCategory, LogSubModule logSubModule) {
        //Network.makeGetNetworkCallWithResponse()
        int respCode = -1;

        try {
            /*HashMap<String, String> body = new HashMap<>();
            body.put("message", message);
            body.put("projectName", projectName);
            body.put("level", LogLevel.Inform.toString());
            body.put("objectName", objectName);
            body.put("logCategory", logCategory.toString());
            body.put("logSubModule", logSubModule.toString());
            body.put("logSubModule", logSubModule.toString());*/
            LogMessage logMessage = new LogMessage(level, message,  Instant.now().toString(), projectName, logSubModule,
                    objectName, objectName);
            JEResponse response = JEBuilderApiHandler.sendLogMessage(logMessage);
            respCode = response.getCode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return respCode;
    }
    /*
     * Send user message
     * */
   /* public static int informUser(String message, String projectName) {
        if(!StringUtilities.isEmpty(message)) {
            String projectId = projectName;
            if(projects.containsKey(projectName)){
                projectId = projects.get(projectName);
            }
            JELogger.info( message,  LogCategory.RUNTIME,  projectId,
                    LogSubModule.WORKFLOW, null);
        }

        return ResponseCodes.CODE_OK;
    }*/

    /*
     * Send user message
     * */
   /* public static int informUser(String level, String message, String projectId, String processId, String taskName) {
        if(!StringUtilities.isEmpty(message)) {
            JELogger.info( message,  LogCategory.RUNTIME,  projectId,
                    LogSubModule.WORKFLOW, processId);
            //send to monitoring when its ready
        }

        return ResponseCodes.CODE_OK;
    }*/


    /*
     * Trigger event from script
     * */
    public static int triggerEvent(String projectId, String eventName) {
        try {

            JEResponse response = JERunnerAPIHandler.triggerEvent(eventName, projectId);
            return response.getCode();
        } catch (Exception e) {
            JELogger.error(JEMessages.ERROR_TRIGGERING_EVENT, LogCategory.RUNTIME,
                    projectId, LogSubModule.EVENT, eventName);
            sendLogMessage(JEMessages.ERROR_TRIGGERING_EVENT, projectId, LogLevel.Error,
                    eventName, LogCategory.RUNTIME, LogSubModule.EVENT);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    //Remove workflow from project
    public static int removeWorkflow(String projectId, String workflowId) {
        try {

            JEResponse response = JEBuilderApiHandler.removeWorkflow(projectId, workflowId);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.DELETE_WORKFLOW_FAILED, projectId, LogLevel.Error,
                    workflowId, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    public static int removeEvent(String projectId, String eventId) {
        try {

            JEResponse response = JEBuilderApiHandler.removeEvent(projectId, eventId);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_DELETING_EVENT, projectId, LogLevel.Error,
                    eventId, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    //Remove rule from JobEngine
    public static int removeRule(String projectId, String ruleId) {

        try {
            JEResponse response = JEBuilderApiHandler.removeRule(projectId, ruleId);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_REMOVING_RULE, projectId, LogLevel.Error,
                    ruleId, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    //Execute a database sql query
    public static int executeSqlQuery(String projectId, String dbId, String query) {
        int responseCode = 500;

        try {
            responseCode = DatabaseApiHandler.executeCommand(dbId, query);
        } catch (Exception e) {

            sendLogMessage(JEMessages.ERROR_EXECUTING_DB_QUERY, projectId, LogLevel.Error,
                    null, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
        }
        return responseCode;
    }

    public static void main(String... args) {

        //String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + INFORM_USER;
        //JobEngine.sendLogMessage("ora", "test", LogLevel.Inform, "none", LogCategory.RUNTIME, LogSubModule.JERUNNER);
        //JobEngine.informUser("ora", "test");
        HashMap<String, String> authorization = new HashMap<>();
        authorization.put("token", "eyJhbGciOiJSUzI1NiIsImtpZCI6Im0waldfSW5FclUwYVFnc0RHU1FGcEEiLCJ0eXAiOiJhdCt" +
                "qd3QifQ.eyJuYmYiOjE2NDE5MDAzNjQsImV4cCI6MTY0MTkwNzU2NCwiaX" +
                "NzIjoiaHR0cDovL25qZW5kb3ViaS1wYzo1MDAwIiwiYXVkIjpbIlNJT1R" +
                "IX1RyYWNrZXIiLCJTSU9USF9KRSIsIkFkbWluQ2xpZW50SWRfYXBpIiwiU0lP" +
                "VEhfTm9kZSIsIlNJT1RIX0RGIiwiU0lPVEhfRGV2aWNlIiwiU0lPVEhfTW9uaXRvc" +
                "mluZyIsIlNJT1RIX0RhdGFNb2RlbCIsIlNJT1RIX0V4cGxvcmVyIiwiU0lPVEhfQW" +
                "RtaW4iLCJTSU9USF9MaWNlbnNlIl0sImNsaWVudF9pZCI6IlNJT1RIX0FQUCIsIn" +
                "N1YiI6IjBiNDg3YTBlLWU2ZTQtNDRmYy1iYTkxLTEyMGMwYzBjMjljNSIsImF1dGhfdG" +
                "ltZSI6MTY0MTgyODM4MCwiaWRwIjoibG9jYWwiLCJuYW1lIjoiYWRtaW4iLCJyb2xlI" +
                "joiQWRtaW5Sb2xlIiwic2NvcGUiOlsicm9sZXMiLCJvcGVuaWQiLCJwcm9maWxlIiwiZW" +
                "1haWwiLCJ0cmFja2VyLnJlYWQiLCJ0cmFja2VyLndyaXRlIiwiamUucmVhZCIsImplLndyaXRlIiwiQWRtaW5DbGllb" +
                "nRJZF9hcGkiLCJub2RlLndyaXRlIiwibm9kZS5yZWFkIiwiZGYud3JpdGUiLCJkZi5yZWFkIiwiZGV2aWNlLndyaXRlIiw" +
                "iZGV2aWNlLnJlYWQiLCJtb25pdG9yaW5nLndyaXRlIiwibW9uaXRvcmluZy5yZWFkIiwiZG0ud3JpdGUiLCJkbS5yZWFkI" +
                "iwiZXhwbG9yZXIud3JpdGUiLCJleHBsb3Jlci5yZWFkIiwiYWRtaW4ud3JpdGUiLCJhZG1pbi5yZWFkIiwibGljZW5zZS5" +
                "yZWFkIiwibGljZW5zZS53cml0ZSJdLCJhbXIiOlsicHdkIl19.RxSGBmlzn7r29W5_ijoDC3i5lAWcMDQ9cvOm_ZdYRolU" +
                "q3Pt6VVVWIKSwmdIhkhnJKn3WB5SY9AltWckLNBpqo_VuOSoUBnasXk3yh9rarUsSSREqyGtW0P5gmfy2u0HzH_1-oTjWq" +
                "L5LW1vtoB_bMTPczwT0g2e9WqpsA-Cw94UfDX1cazf457pQDH3RfDXdB5dKY4KzcHJiwjoCRLNs-MoeM-MNn6PzW0V3bdZ2" +
                "hNAapGHhLJHRS8dHcoPoQGW3pRX5qA-zKcKsk42Nu5QIb-7E6byLzW3m4bhFPXNSA-Z-lco1q3GZv_5VmCN7RsehqpVJDrQoZlhHnbFwI5nYg");
        Network network = new Network.Builder("http://localhost:13003/api/Dataflows/e4526323-c737-3504-6d88-964d9a4f4614/runtime")
                .withMethod(HttpMethod.GET).withAuthScheme(AuthScheme.BEARER).withAuthentication(authorization).build();
        try {
            Response response = network.call();
            ObjectMapper objectMapper = new ObjectMapper();
            HashMap<String, Object> respBody = objectMapper.readValue(response.body().string(), HashMap.class);
            String state = (String) respBody.get("state");
            System.out.println("dataflow state = " + state);
            if(state.equals("Stopped")) {
                network = new Network.Builder("http://localhost:13003/api/Dataflows/e4526323-c737-3504-6d88-964d9a4f4614/start")
                        .withMethod(HttpMethod.GET).withAuthScheme(AuthScheme.BEARER).withAuthentication(authorization).build();
                network.call();
            }

            else {
                network = new Network.Builder("http://localhost:13003/api/Dataflows/e4526323-c737-3504-6d88-964d9a4f4614/stop")
                        .withMethod(HttpMethod.GET).withAuthScheme(AuthScheme.BEARER).withAuthentication(authorization).build();
                network.call();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(requestUrl);
    }
}
