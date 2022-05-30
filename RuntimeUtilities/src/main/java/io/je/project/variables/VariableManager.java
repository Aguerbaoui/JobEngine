package io.je.project.variables;

import io.je.utilities.beans.*;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.VariableException;
import io.je.utilities.exceptions.VariableNotFoundException;
import io.je.utilities.log.JELogger;
import utils.comparator.Comparator;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.string.StringSub;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VariableManager {

	// Project id => VarId => JEVariable
	 static ConcurrentHashMap<String, HashMap<String, JEVariable>> variablesByProjectId = new ConcurrentHashMap<>();
	 static HashMap<String, String> projectIdsByName = new HashMap<>();
	 

	/*
	 * get variable 
	 */
	public static JEVariable getVariableValue(String projectId, String variableId) throws VariableNotFoundException {

		try {
			//by Ids
			if (variablesByProjectId.containsKey(projectId)) {
				return variablesByProjectId.get(projectId).get(variableId);
			//by Names	
			} else if (projectIdsByName.containsKey(projectId)) {
				String _projectId = projectIdsByName.get(projectId);
				if(variablesByProjectId.containsKey(_projectId))
				{
					return getVariableByName(_projectId, variableId);
				}

			}

		} catch (Exception e) {
			throw new VariableNotFoundException(JEMessages.VARIABLE_NOT_FOUND);
		}
		throw new VariableNotFoundException(JEMessages.VARIABLE_NOT_FOUND);


	}
	
	public static JEVariable getVariableByName(String projectId, String variableName) throws VariableNotFoundException
	{
		for ( JEVariable variable : getAllVariables(projectId)) {
			if(variable.getJobEngineElementName().equals(variableName))
			{
				return variable;
			}
	    }
		throw new VariableNotFoundException(JEMessages.VARIABLE_NOT_FOUND);

	}

	/*
	 * Add variable to variable manager
	 */
	public static void addVariable(JEVariable variable) {
		if (!variablesByProjectId.containsKey(variable.getJobEngineProjectID())) {
			variablesByProjectId.put(variable.getJobEngineProjectID(), new HashMap<>());
		}
		variablesByProjectId.get(variable.getJobEngineProjectID()).put(variable.getJobEngineElementID(), variable);
		StringSub.addVariable(variable.getJobEngineProjectID(), variable.getJobEngineElementName(),
				variable.getValue());
		projectIdsByName.put(variable.getJobEngineProjectName(), variable.getJobEngineProjectID());

	}

	/*
	 * Returns the equialent jevariable
	 */
	/*public static JEVariable getJeVariable(String projectId, String id) {
		if(variablesByProjectId.containsKey(projectId)) {
			return variablesByProjectId.get(projectId).get(id);
		}
		else {
			projectId = projectIdsByName.get(projectId);
			return variablesByProjectId.get(projectId).get(id);
		}
	}*/

	/*
	 * Remove variable from variable manager
	 */
	public static void removeVariable(String projectId, String id) {
		if (variablesByProjectId.containsKey(projectId)) {
			variablesByProjectId.get(projectId).remove(id);

		}
	}

	public static JEVariable updateVariableValue(String projectId, String variableId, Object value,
			boolean ignoreIfSameValue) throws VariableException, VariableNotFoundException {
		
		JEVariable variable = getVariableValue(projectId,variableId);

		if (variable != null) {
			projectIdsByName.put(variable.getJobEngineProjectName(), variable.getJobEngineProjectID());
			if (ignoreIfSameValue && Comparator.isSameValue(variable.getValue(), value)) {
				return null;
			}
			variable.setValue(String.valueOf(value));
			JEMessage message = new JEMessage();
			message.setExecutionTime(LocalDateTime.now().toString());
			message.setType("Variable");
			JEVariableMessage varMessage = new JEVariableMessage(variable.getJobEngineElementName(),
					variable.getValue().toString());
			// to be removed
			JEBlockMessage blockMessage = new JEBlockMessage(variable.getJobEngineElementName(),
					variable.getValue().toString());
			message.getBlocks().add(blockMessage);
			message.getVariables().add(varMessage);
			try {
				JELogger.debug("Variable [" + variable.getJobEngineElementName() + "] = " + variable.getValue(),
						LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, variableId);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			JELogger.error(JEMessages.UPDATING_VARIABLE_FAILED, LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE,
					variableId);
		}

		return variable;

	}

	public static Collection<JEVariable> getAllVariables(String projectId) {
		if (!variablesByProjectId.containsKey(projectId)) {
			variablesByProjectId.put(projectId, new HashMap<>());
		}
		return variablesByProjectId.get(projectId).values();
	}

	public static void resetVariableValues(String projectId) throws VariableException {
		if (!variablesByProjectId.containsKey(projectId)) {
			variablesByProjectId.put(projectId, new HashMap<>());
			return;
		}
		for (Map.Entry<String, JEVariable> variable : variablesByProjectId.get(projectId).entrySet()) {
			variable.getValue().setValue(String.valueOf(variable.getValue().getInitialValue()));

		}
	}

}
