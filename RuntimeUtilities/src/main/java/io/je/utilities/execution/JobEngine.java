package io.je.utilities.execution;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.string.JEStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, getVariableBody(varId, varProjectId, varName, varValue, "int"));
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static void addLongVariable(String varId, String varProjectId, String varName, long varValue) {
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, getVariableBody(varId, varProjectId, varName, varValue, "long"));
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static void addDoubleVariable(String varId, String varProjectId, String varName, double varValue) {
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, getVariableBody(varId, varProjectId, varName, varValue, "double"));
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static void addStringVariable(String varId, String varProjectId, String varName, String varValue) {
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, getVariableBody(varId, varProjectId, varName, varValue, "string"));
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    /*
     * Add a variable to runner from script task
     * */
    public static void addBooleanVariable(String varId, String varProjectId, String varName, String varValue) {
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, getVariableBody(varId, varProjectId, varName, varValue, "boolean"));
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
        }
    }

    public static HashMap<String, Object> getVariableBody(String varId, String varProjectId, String varName, Object varValue, String type) {
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

    /*
    * Trigger event from script
    * */
    public static void triggerEvent(String projectId, String eventId) {
        try {
            JERunnerAPIHandler.triggerEvent(eventId, projectId);
        } catch (Exception e) {
            JELogger.info("Error triggering event");
        }
    }
}
