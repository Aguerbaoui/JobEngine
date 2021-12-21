package io.je.project.variables;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.beans.*;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.VariableNotFoundException;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.string.StringSub;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
			StringSub.addVariable(variable.getJobEngineProjectID(), variable.getJobEngineElementName(), variable.getValue());

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
		       JEVariableMessage varMessage = new JEVariableMessage(variable.getJobEngineElementName(), variable.getValue().toString());
		       //to be removed
		       JEBlockMessage blockMessage = new JEBlockMessage(variable.getJobEngineElementName(), variable.getValue().toString());
		       message.getBlocks().add(blockMessage);
		       message.getVariables().add(varMessage);
	           try {
	   			JELogger.debug("Variable ["+variable.getJobEngineElementName() + "] = " +variable.getValue(), LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, variableId);
	   			//JEMonitor.publish(LocalDateTime.now(), variable.getJobEngineElementID(), ObjectType.JEVARIABLE, variable.getJobEngineProjectID(), variable.getValue(), ArchiveOption.AS_SOURCE_DATA, false);
				//JELogger.debugWithoutPublish(objectMapper.writeValueAsString(message), LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, variableId);
			} catch (Exception e) {
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
			variable.getValue().setValue( String.valueOf(variable.getValue().getInitialValue()));
			
		}
	}


}
