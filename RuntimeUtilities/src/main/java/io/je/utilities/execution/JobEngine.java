package io.je.utilities.execution;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.VariableModel;
import io.je.utilities.string.JEStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class JobEngine {

    /*
    * Run a workflow from script task
    * */
    public static JobEngineApiErrors runWorkflow(String projectId, String key) {
        try {
            JERunnerAPIHandler.runWorkflow(JEConfiguration.getRuntimeManagerURL()+ APIConstants.RUN_WORKFLOW + projectId + "/" + key);
        } catch (Exception e) {
           JELogger.info("Error running the workflow");
            return JobEngineApiErrors.JERunnerException;
        }

        return JobEngineApiErrors.NoError;
    }

    /*
    * Add a variable to runner from script task
    * */
    public static JobEngineApiErrors addIntegerVariable(String varId, String varProjectId, String varName, int varValue) {
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, getVariableBody(varId, varProjectId, varName, varValue, "int"));
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
            return JobEngineApiErrors.JERunnerException;
        }

        return JobEngineApiErrors.NoError;
    }

    /*
     * Add a variable to runner from script task
     * */
    public static JobEngineApiErrors addLongVariable(String varId, String varProjectId, String varName, long varValue) {
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, getVariableBody(varId, varProjectId, varName, varValue, "long"));
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
            return JobEngineApiErrors.JERunnerException;
        }

        return JobEngineApiErrors.NoError;
    }

    /*
     * Add a variable to runner from script task
     * */
    public static JobEngineApiErrors addDoubleVariable(String varId, String varProjectId, String varName, double varValue) {
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, getVariableBody(varId, varProjectId, varName, varValue, "double"));
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
            return JobEngineApiErrors.JERunnerException;
        }

        return JobEngineApiErrors.NoError;
    }

    /*
     * Add a variable to runner from script task
     * */
    public static JobEngineApiErrors addStringVariable(String varId, String varProjectId, String varName, String varValue) {

            try {
                JERunnerAPIHandler.addVariable(varProjectId, varId, getVariableBody(varId, varProjectId, varName, varValue, "string"));
            } catch (InterruptedException | ExecutionException | JERunnerErrorException e) {
                JELogger.info("Error adding the variable");
                return JobEngineApiErrors.JERunnerException;
            }

            return JobEngineApiErrors.NoError;

    }

    /*
     * Add a variable to runner from script task
     * */
    public static JobEngineApiErrors addBooleanVariable(String varId, String varProjectId, String varName, String varValue) {
        try {
            JERunnerAPIHandler.addVariable(varProjectId, varId, getVariableBody(varId, varProjectId, varName, varValue, "boolean"));
        } catch (Exception e) {
            JELogger.info("Error adding the variable");
            return JobEngineApiErrors.JERunnerException;
        }

        return JobEngineApiErrors.NoError;
    }

    public static JobEngineApiErrors addVariables(ArrayList<VariableModel> vars) {
        if(vars != null) {
            for(VariableModel variableModel: vars) {
                try {
                    JERunnerAPIHandler.addVariable(variableModel.getProjectId(),
                            variableModel.getId(), getVariableBody(variableModel.getId(), variableModel.getProjectId(),
                                    variableModel.getName(), variableModel.getValue(), variableModel.getType()));
                } catch (Exception e) {
                    JELogger.info("Error adding the variables ");
                    return JobEngineApiErrors.JERunnerException;
                }
            }
        }

        return JobEngineApiErrors.NoError;
    }

    public static void main(String args[] ) {
        ArrayList<VariableModel> v = new ArrayList<>();
        v.add(new VariableModel("tess", "1110", "a", "String", "value of a"));
        v.add(new VariableModel("tess", "1111", "b", "String", "value of b"));
        JobEngine.addVariables(v);
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
    public static JobEngineApiErrors removeVariable(String projectId, String varId) {
        try {
            JERunnerAPIHandler.removeVariable(projectId, varId);
        }  catch (Exception e) {
            JELogger.info("Error adding the variable");
            return JobEngineApiErrors.JERunnerException;
        }

        return JobEngineApiErrors.NoError;
    }

    /*
    * Send user message
    * */
    public static JobEngineApiErrors informUser(String message) {
        if(!JEStringUtils.isEmpty(message)) {
            JELogger.info(message);
            //send to monitoring when its ready
        }

        return JobEngineApiErrors.NoError;
    }

    /*
    * Trigger event from script
    * */
    public static JobEngineApiErrors triggerEvent(String projectId, String eventId) {
        try {
            JERunnerAPIHandler.triggerEvent(eventId, projectId);
        } catch (Exception e) {
            JELogger.info("Error triggering event");
            return JobEngineApiErrors.JERunnerException;
        }

        return JobEngineApiErrors.NoError;
    }
}
