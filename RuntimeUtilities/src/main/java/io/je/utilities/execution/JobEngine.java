package io.je.utilities.execution;

import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.logger.*;
import io.je.utilities.models.VariableModel;
import io.je.utilities.network.JEResponse;
import io.je.utilities.string.JEStringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class JobEngine {

    /*
    * Run a workflow from script task
    * */
    public static int runWorkflow(String projectId, String workflowName) {
        try {
            JEResponse response = JEBuilderApiHandler.runWorkflow(projectId, workflowName);
            return response.getCode();
        } catch (Exception e) {
           JELogger.info("Error running the workflow");
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
            JELogger.info("Error running the workflow");
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
            JELogger.info("Error adding the variable");
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
            JELogger.info("Error adding the variable");
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
            JELogger.error("Error adding the variable");
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
                JELogger.error("Error adding the variable");
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
            JELogger.info("Error adding the variable");
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    public static int addVariables(ArrayList<VariableModel> vars) {
        if(vars != null) {
            for(VariableModel variableModel: vars) {
                try {
                    JEResponse response = JEBuilderApiHandler.addVariable(variableModel.getProjectId(),
                            variableModel.getId(), getVariableBody(variableModel.getId(), variableModel.getProjectId(),
                                    variableModel.getName(), variableModel.getValue(), variableModel.getType()));
                } catch (Exception e) {
                    JELogger.info("Error adding the variables ");
                    return ResponseCodes.UNKNOWN_ERROR;
                }
            }
        }

        return ResponseCodes.CODE_OK;
    }

    //Get variable body from parameters
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
    public static int removeVariable(String projectId, String variableName) {
        try {
            JEResponse response = JEBuilderApiHandler.removeVariable(projectId, variableName);
            return response.getCode();
        }  catch (Exception e) {
            JELogger.error("Error adding the variable");
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    /*
    * Send user message
    * */
    public static int informUser(String message) {
        if(!JEStringUtils.isEmpty(message)) {
            JELogger.info(message);
            //send to monitoring when its ready
        }

        return ResponseCodes.CODE_OK;
    }

    /*
     * Send user message
     * */
    public static int informUser(String level, String message, String projectId, String processId, String taskName) {
        if(!JEStringUtils.isEmpty(message)) {
            JELogger.info(message);
            LogMessage msg = new LogMessage(LogLevel.INFORM,  message,  LocalDateTime.now().toString(), "JobEngine",  projectId,
                    processId, LogSubModule.WORKFLOW, taskName, null, "Log", "") ;
            ZMQLogPublisher.publish(msg);
            //send to monitoring when its ready
        }

        return ResponseCodes.CODE_OK;
    }


    /*
    * Trigger event from script
    * */
    public static int triggerEvent(String projectId, String eventName) {
        try {
            JEResponse response = JERunnerAPIHandler.triggerEvent(eventName, projectId);
            return response.getCode();
        } catch (Exception e) {
            JELogger.error("Error triggering event");
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    //Remove workflow from project
    public static int removeWorkflow(String projectId, String workflowId) {
        try {
            JEResponse response = JEBuilderApiHandler.removeWorkflow(projectId, workflowId);
            return response.getCode();
        } catch (Exception e) {
            JELogger.error("Error removing workflow");
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    public static int removeEvent(String projectId, String eventId) {
        try {
            JEResponse response = JEBuilderApiHandler.removeEvent(projectId, eventId);
            return response.getCode();
        } catch (Exception e) {
            JELogger.error("Error removing event");
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    //Remove rule from JobEngine
    public static int removeRule(String projectId, String ruleId) {
        try {
            JEResponse response = JEBuilderApiHandler.removeRule(projectId, ruleId);
            return response.getCode();
        } catch (Exception e) {
            JELogger.error("Error removing rule");
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }
}
