package io.je.utilities.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.je.utilities.apis.DatabaseApiHandler;
import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.apis.JERunnerRequester;
import io.je.utilities.beans.*;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.instances.InstanceManager;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.VariableModel;
import io.je.utilities.runtimeobject.JEObject;
import io.siothconfig.SIOTHConfigUtility;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.log.LogCategory;
import utils.log.LogLevel;
import utils.log.LogMessage;
import utils.log.LogSubModule;
import utils.string.StringUtilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class JobEngine {

    /*
     * Run a workflow from script task
     * */
   /* public static int runWorkflow(String projectId, String workflowName) {

        try {
            JEResponse response = JEBuilderApiHandler.runWorkflow(projectId, workflowName);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.WORKFLOW_RUN_ERROR, projectId, LogLevel.Error, workflowName, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }*/

    /*
     * Stop a workflow from script task
     * */
    /*public static int stopWorkflow(String projectId, String workflowName) {

        try {
            JEResponse response = JEBuilderApiHandler.stopWorkflow(projectId, workflowName);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.DELETE_WORKFLOW_FAILED, projectId, LogLevel.Error, workflowName, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }*/

    /*
     * Add a variable to runner from script task
     * */
    public static int addIntegerVariable(String projectName, String varName, int varValue) {

        try {
            JEResponse response = JEBuilderApiHandler.addVariable(projectName, varName, getVariableBody(StringUtilities.generateUUID(), projectName, varName, varValue, JEType.INT.toString()));
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, projectName, LogLevel.ERROR, varName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static int addLongVariable(String projectName, String varName, long varValue) {

        try {
            JEResponse response = JEBuilderApiHandler.addVariable(projectName, varName, getVariableBody(StringUtilities.generateUUID(), projectName, varName, varValue, JEType.LONG.toString()));
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, projectName, LogLevel.ERROR, varName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static int addDoubleVariable(String projectName, String varName, double varValue) {

        try {
            JEResponse response = JEBuilderApiHandler.addVariable(projectName, varName, getVariableBody(StringUtilities.generateUUID(), projectName, varName, varValue, JEType.DOUBLE.toString()));
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, projectName, LogLevel.ERROR, varName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    /*
     * Add a variable to runner from script task
     * */
    public static int addStringVariable(String projectName, String varName, String varValue) {

        try {
            JEResponse response = JEBuilderApiHandler.addVariable(projectName, varName, getVariableBody(StringUtilities.generateUUID(), projectName, varName, varValue, JEType.STRING.toString()));
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, projectName, LogLevel.ERROR, varName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    /*
     * Add a variable to runner from script task
     * */
    public static int addBooleanVariable(String projectName, String varName, boolean varValue) {

        try {
            JEResponse response = JEBuilderApiHandler.addVariable(projectName, varName, getVariableBody(StringUtilities.generateUUID(), projectName, varName, varValue, JEType.BOOLEAN.toString()));
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, projectName, LogLevel.ERROR, varName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    public static int addVariables(ArrayList<VariableModel> vars) {

        if (vars != null) {
            for (VariableModel variableModel : vars) {
                try {
                    JEResponse response = JEBuilderApiHandler.addVariable(variableModel.getProjectId(),
                            StringUtilities.generateUUID(), getVariableBody(variableModel.getId(), variableModel.getProjectId(),
                                    variableModel.getName(), variableModel.getValue(), variableModel.getType()));
                } catch (Exception e) {
                    sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, variableModel.getProjectId(), LogLevel.ERROR,
                            variableModel.getId(), LogCategory.RUNTIME, LogSubModule.VARIABLE);
                    return ResponseCodes.UNKNOWN_ERROR;
                }
            }//testKey123456789
        }

        return ResponseCodes.CODE_OK;
    }

    public static Object getVariable(String projectName, String variableName) {
        try {
            JEConfiguration.loadProperties();
            JEZMQResponse var = JERunnerRequester.readVariable(projectName, variableName);
            if (var != null && !var.getResponse()
                    .equals(ZMQResponseType.FAIL)) {
                ObjectMapper mapper = new ObjectMapper();
                VariableModel jeVariable = mapper.readValue((String) var.getResponseObject(), VariableModel.class);
                return jeVariable.getValue();
            } else return null;
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, projectName, LogLevel.ERROR, variableName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    public static int setVariable(String projectName, String variableName, Object value) {
        try {
            JEConfiguration.loadProperties();
            JEZMQResponse response = JERunnerRequester.updateVariable(projectName, variableName, value.toString(), false);
            if (response.getResponse()
                    .equals(ZMQResponseType.SUCCESS)) {
                return ResponseCodes.CODE_OK;
            }
            return ResponseCodes.VARIABLE_ERROR;
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT, projectName, LogLevel.ERROR, variableName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    //Get variable body from parameters
    public static Map<String, Object> getVariableBody(String varId, String varProjectId, String varName, Object varValue, String type) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("id", varId);
        body.put("projectId", varProjectId);
        body.put("name", varName);
        body.put("type", type);
        body.put("value", varValue);
        body.put("initialValue", varValue);
        return body;
    }

    /*
     * Add a variable to runner from script task
     * */
   /* public static int removeVariable(String projectId, String variableName) {
        try {

            JEResponse response = JEBuilderApiHandler.removeVariable(projectId, variableName);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_DELETING_VARIABLE_FROM_PROJECT, projectId, LogLevel.Error,
                    variableName, LogCategory.RUNTIME, LogSubModule.VARIABLE);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }*/

    /*
     * Inform user of a message with inform level
     * */
    public static int informUser(String message, String projectName, String workflowName) {

        //Network.makeGetNetworkCallWithResponse()
        int respCode = -1;
        JEConfiguration.loadProperties();
        try {
            InformModel informModel = new InformModel(message, projectName, workflowName);
            JEZMQResponse response = JERunnerRequester.informUser(informModel);
            if (response.getResponse()
                    .equals(ZMQResponseType.SUCCESS)) return ResponseCodes.CODE_OK;
            else {
                sendLogMessage(JEMessages.ERROR_SENDING_INFORM_MESSAGE, projectName, LogLevel.ERROR,
                        "", LogCategory.RUNTIME, LogSubModule.JERUNNER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendLogMessage(JEMessages.ERROR_SENDING_INFORM_MESSAGE, projectName, LogLevel.ERROR,
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
        JEConfiguration.loadProperties();
        try {
            LogMessage logMessage = new LogMessage(level, message, Instant.now()
                    .toString(), projectName, logSubModule,
                    objectName, objectName);
            JEZMQResponse response = JERunnerRequester.sendLogMessage(logMessage);
            if (response.getResponse()
                    .equals(ZMQResponseType.SUCCESS)) return ResponseCodes.CODE_OK;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return respCode;
    }

    /*
     * Trigger event from script
     * */
    public static int triggerEvent(String projectId, String eventName) {
        try {
            JEConfiguration.loadProperties();
            JEResponse response = JERunnerAPIHandler.triggerEvent(eventName, projectId);
            return response.getCode();
        } catch (Exception e) {
            JELogger.error(JEMessages.ERROR_TRIGGERING_EVENT, LogCategory.RUNTIME,
                    projectId, LogSubModule.EVENT, eventName);
            sendLogMessage(JEMessages.ERROR_TRIGGERING_EVENT, projectId, LogLevel.ERROR,
                    eventName, LogCategory.RUNTIME, LogSubModule.EVENT);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    //Remove workflow from project
    /*public static int removeWorkflow(String projectId, String workflowId) {
        try {

            JEResponse response = JEBuilderApiHandler.removeWorkflow(projectId, workflowId);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.DELETE_WORKFLOW_FAILED, projectId, LogLevel.Error,
                    workflowId, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }*/

    /*public static int removeEvent(String projectId, String eventId) {
        try {

            JEResponse response = JEBuilderApiHandler.removeEvent(projectId, eventId);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_DELETING_EVENT, projectId, LogLevel.Error,
                    eventId, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }*/

    //Remove rule from JobEngine
    /*public static int removeRule(String projectId, String ruleId) {

        try {
            JEResponse response = JEBuilderApiHandler.removeRule(projectId, ruleId);
            return response.getCode();
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_REMOVING_RULE, projectId, LogLevel.Error,
                    ruleId, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }*/

    //Execute a database sql query
    public static HashMap<String, Object> executeSqlQuery(String dbId, String query) {
        JEConfiguration.loadProperties();
        HashMap<String, Object> response = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String data = DatabaseApiHandler.executeCommand(dbId, query);
            response = mapper.readValue(data, HashMap.class);
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_EXECUTING_DB_QUERY, "", LogLevel.ERROR,
                    null, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
        }
        return response;
    }

    public static JSONArray executeSelectQuery(String dbId, String query) {
        JEConfiguration.loadProperties();
        JSONArray jsonArray = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String data = DatabaseApiHandler.executeCommand(dbId, query);
            JSONObject jsonObject = new JSONObject(data);
            jsonArray = new JSONArray(jsonObject.getJSONArray("values"));
        } catch (Exception e) {
            sendLogMessage(JEMessages.ERROR_EXECUTING_DB_QUERY, "", LogLevel.ERROR,
                    null, LogCategory.RUNTIME, LogSubModule.WORKFLOW);
        }
        return jsonArray;
    }

    public static void endJob() {
        System.exit(0);
    }

    public static void display1() {
        System.out.println("step 7");
    }

    public static JEObject getDataModelInstance(String instanceName) throws IOException {
        System.out.println("step 1");
        JEConfiguration.loadProperties();
        ConfigurationConstants.setJavaGenerationPath(SIOTHConfigUtility.getSiothConfig()
                .getJobEngine()
                .getGeneratedClassesPath());
        JEZMQResponse var = JERunnerRequester.readClass(instanceName);
        JEObject instance = null;
        if (var != null && !var.getResponse()
                .equals(ZMQResponseType.FAIL)) {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(ConfigurationConstants.getJavaGenerationPath());

            Class<?> cls = null;
            HashMap<String, String> a = null;
            try {
                // Convert File to a URL
                URL url = file.toURI()
                        .toURL();          // file:/c:/myclasses/
                URL[] urls = new URL[]{url};

                // Create a new class loader with the directory
                ClassLoader cl = new URLClassLoader(urls);

                // Load in the class; MyClass.class should be located in
                // the directory file:/c:/myclasses/com/mycompany
                a = (HashMap<String, String>) var.getResponseObject();
                cls = cl.loadClass(a.get("className"));
                mapper.registerModule(new JavaTimeModule());
                mapper.findAndRegisterModules();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (a != null) {
                instance = (JEObject) mapper.readValue(a.get("instance"), cls);
            }
            //instance = mapper.readValue((String) var.getResponseObject(), cls.class);
            System.out.println(instance);
            // instance = (JEObject) var.getResponseObject();


        } else return null;
        System.out.println(instance);

        //  JEObject obj = InstanceManager.getInstance(instanceName);
        // System.out.println("step 2" + obj.toString());
    /*    if (obj == null) {
            JELogger.error("Failed to load instance from dataModel" + ClassRepository.loadedClasses.toString(), LogCategory.RUNTIME,
                    null, LogSubModule.JERUNNER, null);
        }*/
        return instance;
    }

    public static void setDataModelInstanceAttribute(String instanceId, String attributeName, Object attributeValue) {
        JEConfiguration.loadProperties();
        InstanceManager.writeToDataModelInstance(instanceId, attributeName, attributeValue, false);
    }


    public static void main(String... args) throws IOException {
        sendLogMessage("testtt", "DM", LogLevel.INFORM, "testwf",
                LogCategory.RUNTIME, LogSubModule.WORKFLOW);
        JobEngine.informUser("testtt", "DM", "testwf")
        ;
        setDataModelInstanceAttribute("23aa0c9c-ee34-c9e6-bbeb-7d407f0139b1", "fuelLevel", 110);
        JEObject a = JobEngine.getDataModelInstance("azerty");
        /*while(true) {
            informUser("test message", "DM", "testwf");
        }*/
        /*System.out.println(JobEngine.getVariable("DM", "testVar"));
        JobEngine.setVariabl   e("DM", "testVar", 12132.0);
        System.out.println(JobEngine.getVariable("DM", "testVar"));*
        setDataModelInstanceAttribute("23aa0c9c-ee34-c9e6-bbeb-7d407f0139b1", "fuelLevel", 110);
         //JobEngine.executeSelectQuery("db", "SELECT * FROM siothdatabase.testtable;");
         /*int code = JobEngine.addDoubleVariable("test", "DoubleVar", 3.3);
        System.out.println(code);
        code = JobEngine.addLongVariable("test", "LongVar", 333333);
        System.out.println(code);

        code = JobEngine.addIntegerVariable("test", "IntVar", 3);
        System.out.println(code);

       String testString = (String) JobEngine.getVariable("test", "testBool");
        System.out.println(testString);
        JobEngine.setVariable("test", "testBool", true);
        testString = (String) JobEngine.getVariable("test", "testBool");
        JobEngine.informUser(testString, "test", "testScriptTwo");
        System.out.println(testString);

        String testString = (String) JobEngine.getVariable("test", "testVarInt");
        System.out.println(testString);
        JobEngine.setVariable("test", "testVarInt", true);
        testString = (String) JobEngine.getVariable("test", "testVarInt");
        JobEngine.informUser(testString, "test", "testScriptTwo");
        System.out.println(testString);
        System.exit(0);

        String testString = (String) JobEngine.getVariable("test", "testVarLong");
        System.out.println(testString);
        JobEngine.setVariable("test", "testVarLong", 12354);
        testString = (String) JobEngine.getVariable("test", "testVarLong");
        JobEngine.informUser(testString, "test", "testScriptTwo");
        System.out.println(testString);
        System.exit(0);*/
/*
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
        }*/
        //System.out.println(requestUrl);
    }
}


