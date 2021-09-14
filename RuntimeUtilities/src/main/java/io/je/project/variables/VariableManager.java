package io.je.project.variables;

import io.je.utilities.beans.ArchiveOption;
import io.je.utilities.beans.JEBlockMessage;
import io.je.utilities.beans.JEMessage;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.beans.JEVariableMessage;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.VariableNotFoundException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.monitoring.JEMonitor;
import io.je.utilities.monitoring.ObjectType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.je.utilities.string.JEStringSubstitutor;
import io.je.utilities.time.JEDate;

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
			JEStringSubstitutor.addVariable(variable.getJobEngineProjectID(), variable.getName(), variable.getValue());

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

        }
    }

	public static JEVariable updateVariableValue(String projectId, String variableId, Object value) {
		 if(!variables.containsKey(projectId)) {  
	            variables.put(projectId, new HashMap<>());
	        }
		
	       JEVariable variable = variables.get(projectId).get(variableId);
	       
 	       if(variable!=null)
	       {
	    	   variable.setValue(String.valueOf(value));
		       JEMessage message = new JEMessage();
		       message.setExecutionTime(LocalDateTime.now().toString());
		       message.setType("Variable");
		       JEVariableMessage varMessage = new JEVariableMessage(variable.getName(), variable.getValue().toString());
		       //to be removed
		       JEBlockMessage blockMessage = new JEBlockMessage(variable.getName(), variable.getValue().toString());
		       message.getBlocks().add(blockMessage);
		       message.getVariables().add(varMessage);
	           try {
	   			JELogger.info("Variable "+variable.getName() + " = " +variable.getValue(), LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, variableId);
	   			JEMonitor.publish(LocalDateTime.now(), variable.getJobEngineElementID(), ObjectType.JEVARIABLE, variable.getJobEngineProjectID(), variable.getValue(), ArchiveOption.asSourceData, false);
				JELogger.debug(objectMapper.writeValueAsString(message), LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, variableId);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	   
	       }else {
	    	   JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED , LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, variableId);
	       }
	       
           return variable;

	}

	public static Collection<JEVariable> getAllVariables(String projectId)
	{
		 if(!variables.containsKey(projectId)) {  
	            variables.put(projectId, new HashMap<>());
	        }
		 return variables.get(projectId).values();
	}
	
	public static void resetVariableValues(String projectId) {
		 if(!variables.containsKey(projectId)) {  
	            variables.put(projectId, new HashMap<>());
	            return ;
	        }
		for(Map.Entry<String, JEVariable>  variable : variables.get(projectId).entrySet())
		{
			variable.getValue().setValue( (String) variable.getValue().getInitialValue());
			
		}
	}


}
