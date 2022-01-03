package io.je.utilities.execution;

import io.je.utilities.apis.DatabaseApiHandler;
import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.VariableModel;
import io.je.utilities.beans.JEResponse;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.string.StringUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class JobEngine {

   // private static HashMap<String, JarFile> libraries;

    //Map of projectName=>ProjectId
    private static HashMap<String, String> projects;

    /*public static void addJarFile(String name, JarFile file) {
        if(libraries == null) {
            libraries = new HashMap<>();
        }
        libraries.put(name, file);
        //JELogger.debug("helloThere" + JobEngine.getJarFile("org.eclipse.jdt.core-3.7.1.jar").getName());
    }

    public static JarFile getJarFile(String name) {
        return libraries.get(name);
    }*/

    /*
    * Run a workflow from script task
    * */
    public static int runWorkflow(String projectId, String workflowName) {
        try {
            JEResponse response = JEBuilderApiHandler.runWorkflow(projectId, workflowName);
            return response.getCode();
        } catch (Exception e) {
            JELogger.error(JEMessages.WORKFLOW_RUN_ERROR ,  LogCategory.RUNTIME,
                    projectId, LogSubModule.WORKFLOW, workflowName);
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
            JELogger.error(JEMessages.DELETE_WORKFLOW_FAILED ,  LogCategory.RUNTIME,
                    projectId, LogSubModule.WORKFLOW, workflowName);
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
            //JELogger.info("Error adding the variable");
            JELogger.error(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT,  LogCategory.RUNTIME,
                    varProjectId, LogSubModule.VARIABLE, varName);
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
            JELogger.error(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT,  LogCategory.RUNTIME,
                    varProjectId, LogSubModule.VARIABLE, varName);
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
            JELogger.error(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT,  LogCategory.RUNTIME,
                    varProjectId, LogSubModule.VARIABLE, varName);
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
                JELogger.error(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT,  LogCategory.RUNTIME,
                        varProjectId, LogSubModule.VARIABLE, varName);
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
            JELogger.error(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT,  LogCategory.RUNTIME,
                    varProjectId, LogSubModule.VARIABLE, varName);
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
                    JELogger.error(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT,  LogCategory.RUNTIME,
                            variableModel.getProjectId(), LogSubModule.VARIABLE, variableModel.getId());
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
        }  catch (Exception e) {
            //JELogger.error("Error adding the variable");
            JELogger.error(JEMessages.ERROR_DELETING_VARIABLE_FROM_PROJECT,  LogCategory.RUNTIME,
                    projectId, LogSubModule.VARIABLE, variableName);
            return ResponseCodes.UNKNOWN_ERROR;
        }

    }

    /*
    * Send user message
    * */
    public static int informUser(String message, String projectName) {
        if(!StringUtilities.isEmpty(message)) {
            String projectId = projectName;
            if(projects.containsKey(projectName)){
                projectId = projects.get(projectName);
            }
            JELogger.info( message,  LogCategory.RUNTIME,  projectId,
                    LogSubModule.WORKFLOW, null);
        }

        return ResponseCodes.CODE_OK;
    }

    /*
     * Send user message
     * */
    public static int informUser(String level, String message, String projectId, String processId, String taskName) {
        if(!StringUtilities.isEmpty(message)) {
            JELogger.info( message,  LogCategory.RUNTIME,  projectId,
                    LogSubModule.WORKFLOW, processId);
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
            JELogger.error(JEMessages.ERROR_TRIGGERING_EVENT,  LogCategory.RUNTIME,
                    projectId, LogSubModule.EVENT, eventName);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    //Remove workflow from project
    public static int removeWorkflow(String projectId, String workflowId) {
        try {
            JEResponse response = JEBuilderApiHandler.removeWorkflow(projectId, workflowId);
            return response.getCode();
        } catch (Exception e) {
            JELogger.error(JEMessages.DELETE_WORKFLOW_FAILED ,  LogCategory.RUNTIME,
                    projectId, LogSubModule.WORKFLOW, workflowId);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    public static int removeEvent(String projectId, String eventId) {
        try {
            JEResponse response = JEBuilderApiHandler.removeEvent(projectId, eventId);
            return response.getCode();
        } catch (Exception e) {
            JELogger.error(JEMessages.ERROR_DELETING_EVENT ,  LogCategory.RUNTIME,
                    projectId, LogSubModule.WORKFLOW, eventId);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    //Remove rule from JobEngine
    public static int removeRule(String projectId, String ruleId) {
        try {
            JEResponse response = JEBuilderApiHandler.removeRule(projectId, ruleId);
            return response.getCode();
        } catch (Exception e) {
            JELogger.error(JEMessages.ERROR_REMOVING_RULE ,  LogCategory.RUNTIME,
                    projectId, LogSubModule.WORKFLOW, ruleId);
            return ResponseCodes.UNKNOWN_ERROR;
        }
    }

    //Execute a database sql query
    public static int executeSqlQuery(String projectId, String dbId, String query) {
        int responseCode = 500;
        try {
            responseCode = DatabaseApiHandler.executeCommand(dbId, query);
        }
        catch (Exception e) {
            JELogger.error(JEMessages.ERROR_EXECUTING_DB_QUERY ,  LogCategory.RUNTIME,
                    projectId, LogSubModule.WORKFLOW, null);
        }
        return responseCode;
    }

    public static void updateProjects(String projectId, String projectName) {
        if(projects == null) {
            projects = new HashMap<>();
        }
        projects.put(projectName, projectId);
    }
}
