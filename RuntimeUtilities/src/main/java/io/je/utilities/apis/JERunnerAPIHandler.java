package io.je.utilities.apis;


import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.WorkflowModel;
import io.siothconfig.SIOTHConfigUtility;
import okhttp3.Response;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.network.Network;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.je.utilities.apis.Request.*;
import static io.je.utilities.constants.APIConstants.*;

/*
 * class that handles interaction with the JERunner REST API
 */
public class JERunnerAPIHandler {


    private static String runtimeManagerBaseApi = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeRunner();


    private JERunnerAPIHandler() {
    }

    public static String getRuntimeManagerBaseApi() {
        return runtimeManagerBaseApi;
    }

    public static void setRuntimeManagerBaseApi(String runtimeUrl) {
        runtimeManagerBaseApi = runtimeUrl;
    }

    /*
     * run project
     */
    public static JEResponse runProject(String projectId, String projectName)
            throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.RUN_PROJECT + projectId + "/" + projectName;
        return sendRequest(requestUrl);
    }

    public static JEResponse stopProject(String projectId, String projectName)
            throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.STOP_PROJECT + projectId + "/" + projectName;
        return sendRequest(requestUrl);
    }

    //////// RULES //////////

    // add rule
    public static JEResponse addRule(Object requestModel) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.ADD_RULE;
        return sendRequestWithBody(requestUrl, requestModel);

    }

    // compile rule
    public static JEResponse compileRule(Object requestModel)
            throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.COMPILE_RULE;
        return sendRequestWithBody(requestUrl, requestModel);
    }

    // update rule
    public static JEResponse updateRule(Object requestModel) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.UPDATE_RULE;
        return sendRequestWithBody(requestUrl, requestModel);
    }

    public static JEResponse deleteRule(String projectId, String ruleId) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.DELETE_RULE + "/" + projectId + "/" + ruleId;
        return sendRequest(requestUrl);
    }

    ///// CLASSES ///////

    public static JEResponse addClass(Map<String, String> requestModel) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + ADD_CLASS;
        return sendRequestWithBody(requestUrl, requestModel);

    }

    public static JEResponse updateClass(Map<String, String> requestModel) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + UPDATE_CLASS;
        return sendRequestWithBody(requestUrl, requestModel);

    }

    public static JEResponse addClasses(List<HashMap> requestModel) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + ADD_CLASSES;
        return sendRequestWithBody(requestUrl, requestModel);

    }

    ///////////////////////////////// EVENTS//////////////////////////////

    public static JEResponse triggerEvent(String eventId, String projectId) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + TRIGGER_EVENT + projectId + "/" + eventId;
        return sendRequest(requestUrl);
    }

    // add event
    public static JEResponse addEvent(HashMap<String, Object> requestModel) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + ADD_EVENT;
        return sendRequestWithBody(requestUrl, requestModel);
    }

    ////////////// Workflows

    // add workflow
    public static JEResponse addWorkflow(WorkflowModel wf) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.ADD_WORKFLOW;
        return sendRequestWithBody(requestUrl, wf);

    }

    //run workflow
    public static JEResponse runWorkflow(String projectId, String workflowName) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeRunner() + APIConstants.RUN_WORKFLOW + projectId + "/" + workflowName;
        return sendRequest(requestUrl);

    }

    //update event type
    public static void updateEventType(String projectId, String eventId, String type) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.UPDATE_EVENT_TYPE + "/" + projectId + "/" + eventId;
        JELogger.debug(JEMessages.NETWORK_UPDATE_EVENT + "project id = " + projectId + ", event id = " + eventId);
        sendRequestWithStringBody(requestUrl, type);

    }

    // Check runner health
    public static boolean checkRunnerHealth() throws InterruptedException, JERunnerErrorException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + ACTUATOR_HEALTH;
        Response response = null;
        try {
            response = Network.makeGetNetworkCallWithResponse(requestUrl);
            if (response != null) {
                if (response.code() != ResponseCodes.CODE_OK) {
                    JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.DESIGN_MODE,
                            null, LogSubModule.JEBUILDER, null);
                    throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body().string());
                } else {
                    // TODO check {"status":"UP"}
                }
                response.body().close();
                return true;
               /* String respBody = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                HashMap<String, String> v = objectMapper.readValue(respBody, HashMap.class);
                if (v.containsKey("status") && v.get("status").equalsIgnoreCase("up")) {
                    return true;
                }*/
            }

        } catch (IOException e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.DESIGN_MODE,
                    null, LogSubModule.JEBUILDER, null);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
        } finally {
            if (response != null && response.body() != null) {
                response.close();
            }
        }
        return false;
    }

    //delete event from runner
    public static JEResponse deleteEvent(String projectId, String eventId) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + DELETE_EVENT + "/" + projectId + "/" + eventId;
        JELogger.debug(JEMessages.NETWORK_DELETE_EVENT + ", project id = " + projectId + ", event id = " + eventId);
        return sendDeleteRequest(requestUrl);
    }

    // clean project data from runner
    public static JEResponse cleanProjectDataFromRunner(String projectId) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + CLEAN_HOUSE + "/" + projectId;
        JELogger.debug(JEMessages.NETWORK_CLEAN_PROJECT + " project id = " + projectId);
        return sendRequest(requestUrl);
    }


    public static JEResponse updateRunnerSettings(Object requestModel) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.UPDATE_CONFIG;
        JELogger.debug(JEMessages.RUNNER_CONFFIG_UPDATE, LogCategory.DESIGN_MODE,
                null, LogSubModule.JEBUILDER, null);
        return sendRequestWithBody(requestUrl, requestModel);
    }

    public static void deleteWorkflow(String projectId, String workflowId) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + DELETE_WORKFLOW + "/" + projectId + "/" + workflowId;
        JELogger.debug(JEMessages.NETWORK_DELETE_WF + " project id = " + projectId + "workflow id = " + workflowId);
        sendDeleteRequest(requestUrl);
    }

    public static void addVariable(String projectId, String varId, Object body) throws JERunnerErrorException {
        String url = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeRunner() + APIConstants.ADD_VARIABLE;
        JELogger.debug(JEMessages.NETWORK_ADD_VAR + " project id = " + projectId + " variable id = " + varId);
        sendRequestWithBody(url, body);
    }

    public static void removeVariable(String projectId, String varId) throws JERunnerErrorException {
        String url = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeRunner() + DELETE_VARIABLE + "/" + projectId + "/" + varId;
        JELogger.debug(JEMessages.NETWORK_DELETE_VAR + " project id = " + projectId + " var id = " + varId);
        sendDeleteRequest(url);
    }

    public static JEResponse addJarToRunner(HashMap<String, String> payload) throws JERunnerErrorException {
        String url = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeRunner() + APIConstants.ADD_JAR;
        JELogger.debug(JEMessages.ADDING_JAR_FILE_TO_RUNNER + payload, LogCategory.DESIGN_MODE,
                null, LogSubModule.JEBUILDER, null);
        return sendRequestWithBody(url, payload);
    }

    public static JEResponse untriggerEvent(String eventId, String projectId) throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + UNTRIGGER_EVENT + projectId + "/" + eventId;
        return sendRequest(requestUrl);
    }

    /*	public static JEResponse writeVariableValue(String projectId,String variableId, Object value, boolean ignoreIfSameValue) throws JERunnerErrorException {
             String requestUrl = runtimeManagerBaseApi + "/variable" + WRITE_TO_VARIABLE + projectId + "/" + variableId;
             HashMap<String, Object> payload = new HashMap<String, Object>();
             payload.put("value", value);
             payload.put("ignoreIfSameValue", ignoreIfSameValue);

             return sendRequestWithBody(requestUrl, payload);


        }
    */

    public static JEResponse runProjectRules(String projectId)
            throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.RUN_PROJECT_RULES + projectId;
        return sendRequest(requestUrl);

    }

    public static JEResponse runRuleEngine(String projectId)
            throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.RUN_RULE_ENGINE + projectId;
        return sendRequest(requestUrl);

    }

    public static JEResponse shutDownRuleEngine(String projectId)
            throws JERunnerErrorException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.SHUT_DOWN_RULE_ENGINE + projectId;
        return sendRequest(requestUrl);
    }


}
