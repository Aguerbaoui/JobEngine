package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.repository.VariableRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.models.EventModel;
import io.je.utilities.models.VariableModel;
import models.JEWorkflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
public class VariableService {

    @Autowired
	VariableRepository variableRepository;
	
	public Collection<VariableModel> getAllVariables(String projectId) throws ProjectNotFoundException {
        JELogger.debug("[project id = " + projectId + "] " + JEMessages.LOADING_VARIABLES,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.VARIABLE,null);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}
		ArrayList<VariableModel> variableModels = new ArrayList<>();
		for(JEVariable variable: variableRepository.findByJobEngineProjectID(projectId))
		{
			variableModels.add(new VariableModel(variable));
		}
        JELogger.debug(" Found " + variableModels.size() + " variables",
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.VARIABLE,null);
		return variableModels;
	}

	/*
	 * retrieve event from project by id
	 */
	
	public JEVariable getVariable(String projectId, String variableId) throws  ProjectNotFoundException, VariableNotFoundException {
        JELogger.debug(JEMessages.LOADING_VARIABLES +" [ id="+variableId+"] in project id =  " + projectId,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.VARIABLE,variableId);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}
		return variableRepository.findById(variableId).get();
	}
	
	
	public void addVariableToRunner(JEVariable variable) throws JERunnerErrorException, InterruptedException, ExecutionException
	{
        JELogger.debug(JEMessages.SENDING_VARIABLE_TO_RUNNER,
                LogCategory.DESIGN_MODE, variable.getJobEngineProjectID(),
                LogSubModule.VARIABLE,variable.getJobEngineElementID());
        JERunnerAPIHandler.addVariable(variable.getJobEngineProjectID(), variable.getJobEngineElementID(), new VariableModel(variable));

	}
	
	
    /*
    * Add a new variable to the project
    * */
    public void addVariable(VariableModel variableModel) throws ConfigException, ProjectNotFoundException, VariableAlreadyExistsException, JERunnerErrorException, ExecutionException, InterruptedException {
        JELogger.debug(JEMessages.ADDING_VARIABLE,
                LogCategory.DESIGN_MODE, variableModel.getProjectId(),
                LogSubModule.VARIABLE,variableModel.getId());
        JEProject project = ProjectService.getProjectById(variableModel.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if(project.variableExists(variableModel.getId())) {
            throw new VariableAlreadyExistsException(JEMessages.VARIABLE_EXISTS);
        }

        JEVariable var = new JEVariable(variableModel.getId(),variableModel.getProjectId(),variableModel.getName(),variableModel.getType(),variableModel.getInitialValue());

        JERunnerAPIHandler.addVariable(variableModel.getProjectId(), variableModel.getId(), variableModel);
        project.addVariable(var);
        variableRepository.save(var);
    }

    /*
    * Delete a variable from the project
    * */
    public void deleteVariable(String projectId, String varId) throws ProjectNotFoundException, VariableNotFoundException, InterruptedException, JERunnerErrorException, ExecutionException {
        JELogger.debug(JEMessages.DELETING_VARIABLE,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.VARIABLE,varId);
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if(!project.variableExists(varId)) {
            throw new VariableNotFoundException(JEMessages.VARIABLE_NOT_FOUND);
        }
        JERunnerAPIHandler.removeVariable(projectId, varId);
        project.removeVariable(varId);
        variableRepository.deleteById(varId);
    }

    /*
    * Update an existing variable in the project
    * */
    public void updateVariable(VariableModel variableModel) throws ConfigException, ProjectNotFoundException, VariableNotFoundException, InterruptedException, JERunnerErrorException, ExecutionException {
        JELogger.debug(JEMessages.ADDING_VARIABLE,
                LogCategory.DESIGN_MODE, variableModel.getProjectId(),
                LogSubModule.VARIABLE,variableModel.getId());
        JEProject project = ProjectService.getProjectById(variableModel.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if(!project.variableExists(variableModel.getId())) {
            throw new VariableNotFoundException(JEMessages.VARIABLE_NOT_FOUND);
        }
        JEVariable var = new JEVariable(variableModel.getId(),variableModel.getProjectId(),variableModel.getName(),variableModel.getType(), variableModel.getInitialValue());
        var.setJeObjectCreationDate(LocalDateTime.now());
        var.setJeObjectLastUpdate(LocalDateTime.now());
        JERunnerAPIHandler.addVariable(variableModel.getProjectId(), variableModel.getId(), variableModel);
        project.addVariable(var);
        variableRepository.save(var);
    }

    //TODO: only allowed when project is stopped?
	public void writeVariableValue(String projectId,String variableId, Object value) throws ConfigException, JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        
        JERunnerAPIHandler.writeVariableValue(projectId, variableId, value);


		
	}

	public void deleteAll(String projectId) {
		variableRepository.deleteByJobEngineProjectID(projectId);
		
	}
	
	   public ConcurrentHashMap<String, JEVariable> getAllJEVariables(String projectId) throws ProjectNotFoundException {
			List<JEVariable> variables = variableRepository.findByJobEngineProjectID(projectId);
			ConcurrentHashMap<String, JEVariable> map = new ConcurrentHashMap<String, JEVariable>();
			for(JEVariable variable : variables )
			{
				map.put(variable.getJobEngineElementID(), variable);
			}
			return map;
		}
}
