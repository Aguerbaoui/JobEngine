package io.je.utilities.execution;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.logger.JELogger;
import io.je.utilities.string.JEStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class JobEngine {

    /*
    * Run a workflow from script task
    * */
    public static void runWorkflow(String projectId, String key) {
        try {
            JERunnerAPIHandler.runWorkflow(JEConfiguration.getRuntimeManagerURL()+ APIConstants.RUN_WORKFLOW + projectId + "/" + key);
        } catch (Exception e) {
           JELogger.info("Error running the workflow");
        }

    }

    /*
    * Add a variable to runner from script task
    * */
    public static void addIntegerVariable(String varId, String varProjectId, String varName, int varValue) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("id", varId);
        body.put("projectId", varProjectId);
        body.put("name", varName);
        body.put("type", "int");
        body.put("value", varValue);
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, body);
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static void addLongVariable(String varId, String varProjectId, String varName, long varValue) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("id", varId);
        body.put("projectId", varProjectId);
        body.put("name", varName);
        body.put("type", "long");
        body.put("value", varValue);
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, body);
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static void addDoubleVariable(String varId, String varProjectId, String varName, double varValue) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("id", varId);
        body.put("projectId", varProjectId);
        body.put("name", varName);
        body.put("type", "double");
        body.put("value", varValue);
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, body);
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static void addStringVariable(String varId, String varProjectId, String varName, String varValue) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("id", varId);
        body.put("projectId", varProjectId);
        body.put("name", varName);
        body.put("type", "string");
        body.put("value", varValue);
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, body);
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static void addBooleanVariable(String varId, String varProjectId, String varName, String varValue) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("id", varId);
        body.put("projectId", varProjectId);
        body.put("name", varName);
        body.put("type", "boolean");
        body.put("value", varValue.toLowerCase());
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, body);
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static void removeVariable(String projectId, String varId) {
        try {
            JERunnerAPIHandler.deleteVariable(projectId, varId);
        }  catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    /*
    * Send user message
    * */
    public static void informUser(String message) {
        if(!JEStringUtils.isEmpty(message)) {
            JELogger.info(message);
            //send to monitoring when its ready
        }
    }
}
