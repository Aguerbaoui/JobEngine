package io.je.runtime.repos;

import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.utilities.beans.JEBlockMessage;
import io.je.utilities.beans.JEMessage;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.beans.JEVariableMessage;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.VariableNotFoundException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VariableManager {

    //Project id => VarId => JEVariable
    public static ConcurrentHashMap<String, HashMap<String, JEVariable>> variables = new ConcurrentHashMap<>();
    private static 	ObjectMapper objectMapper = new ObjectMapper();

    /*
     * Add variable to variable manager
     * */
     public static Object getVariableValue(String  projectId, String variableId) throws VariableNotFoundException {
         try {
        	 return variables.get(projectId).get(variableId).getValue();       	 
         }
         catch(Exception e)
         {
        	 throw new VariableNotFoundException(JEMessages.VARIABLE_NOT_FOUND);
         }
 	      
     }
    
    
    /*
    * Add variable to variable manager
    * */
    public static void addVariable(JEVariable variable) {
        if(!variables.containsKey(variable.getJobEngineProjectID())) {
            variables.put(variable.getJobEngineProjectID(), new HashMap<>());
        }
        variables.get(variable.getJobEngineProjectID()).put(variable.getJobEngineElementID(),variable);
	       RuleEngineHandler.addVariable(variable);
	       JELogger.info("variable added successfully " + variable.toString());

    }

    /*
     * Returns the equialent jevariable
     * */
    public static JEVariable getJeVariable(String projectId, String id) {
        return variables.get(projectId).get(id);
    }

    /*
     * Remove variable from variable manager
     * */
    public static void removeVariable(String projectId, String id) {
        if(variables.containsKey(projectId)) {
            variables.get(projectId).remove(id);
 	       RuleEngineHandler.deleteVariable(projectId,id);

        }
    }

	public static void updateVariableValue(String projectId, String variableId, String value) {
		 if(!variables.containsKey(projectId)) {  
	            variables.put(projectId, new HashMap<>());
	        }
	       JEVariable variable = variables.get(projectId).get(variableId);
	       variable.setValue(value);
	       RuleEngineHandler.addVariable(variable);
	       JEMessage message = new JEMessage();
	       message.setExecutionTime(LocalDate.now().toString());
	       message.setType("Variable");
	       JEVariableMessage varMessage = new JEVariableMessage(variable.getName(), variable.getValue().toString());
	       //to be removed
	       JEBlockMessage blockMessage = new JEBlockMessage(variable.getName(), variable.getValue().toString());
	       message.getBlocks().add(blockMessage);
	       message.getVariables().add(varMessage);
           try {
			JELogger.trace(objectMapper.writeValueAsString(message), LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, variableId);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
