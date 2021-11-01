package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.config.LicenseProperties;
import io.je.project.repository.VariableRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEType;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.VariableModel;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
public class VariableService {

    @Autowired
	VariableRepository variableRepository;
	
	public Collection<VariableModel> getAllVariables(String projectId) throws ProjectNotFoundException, LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();


		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}
		JELogger.debug("[project  = " + project.getProjectName() + "] " + JEMessages.LOADING_VARIABLES,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.VARIABLE,null);
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
	
	public JEVariable getVariable(String projectId, String variableId) throws  ProjectNotFoundException, VariableNotFoundException, LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();

		
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException("[projectId = " + projectId +"]"+JEMessages.PROJECT_NOT_FOUND);
		}
		Optional<JEVariable> var=variableRepository.findById(variableId);
		if(var.isEmpty())
		{	String strError = JEMessages.VARIABLE_NOT_FOUND+ variableId; 
			JELogger.error(strError,LogCategory.DESIGN_MODE, projectId,LogSubModule.VARIABLE,variableId);
			throw new VariableNotFoundException(strError);
		}
		return var.get();
		
	}
	
	
	public void addVariableToRunner(JEVariable variable) throws JERunnerErrorException, LicenseNotActiveException
	{
    	LicenseProperties.checkLicenseIsActive();

        JELogger.debug(JEMessages.SENDING_VARIABLE_TO_RUNNER,
                LogCategory.DESIGN_MODE, variable.getJobEngineProjectID(),
                LogSubModule.VARIABLE,variable.getJobEngineElementID());
        JERunnerAPIHandler.addVariable(variable.getJobEngineProjectID(), variable.getJobEngineElementID(), new VariableModel(variable));

	}
	
	
    /*
    * Add a new variable to the project
    * */
    public void addVariable(VariableModel variableModel) throws ProjectNotFoundException, VariableAlreadyExistsException, ExecutionException, InterruptedException, LicenseNotActiveException, VariableException {
    	LicenseProperties.checkLicenseIsActive();

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

        JEVariable var = new JEVariable(variableModel.getId(),variableModel.getProjectId(),variableModel.getName(),variableModel.getType(),variableModel.getInitialValue(),variableModel.getDescription(),variableModel.getCreatedBy(),variableModel.getModifiedBy());
        var.setJobEngineProjectName(project.getProjectName());
        try {
			JERunnerAPIHandler.addVariable(variableModel.getProjectId(), variableModel.getId(), variableModel);
		}
        catch (JERunnerErrorException e) {
        	throw new VariableException(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT);
		}
        project.addVariable(var);
        variableRepository.save(var);
    }

    /*
    * Delete a variable from the project
    * */
    public void deleteVariable(String projectId, String varId) throws ProjectNotFoundException, VariableNotFoundException, LicenseNotActiveException, VariableException {
    	LicenseProperties.checkLicenseIsActive();

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
        try {
			JERunnerAPIHandler.removeVariable(projectId, varId);
		}
        catch(JERunnerErrorException e) {
        	throw new VariableException(JEMessages.ERROR_DELETING_VARIABLE_FROM_PROJECT);
		}
        project.removeVariable(varId);
        variableRepository.deleteById(varId);
    }

    /*
    * Update an existing variable in the project
    * */
    public void updateVariable(VariableModel variableModel) throws ProjectNotFoundException, VariableNotFoundException,   LicenseNotActiveException, VariableException {
    	LicenseProperties.checkLicenseIsActive();

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
        JEVariable var = new JEVariable(variableModel.getId(),variableModel.getProjectId(),variableModel.getName(),variableModel.getType(), variableModel.getInitialValue(),variableModel.getDescription(),variableModel.getCreatedBy(),variableModel.getModifiedBy());
        var.setJobEngineProjectName(project.getProjectName());
        var.setJeObjectCreationDate(LocalDateTime.now());
        var.setJeObjectLastUpdate(LocalDateTime.now());
		try {
			JERunnerAPIHandler.addVariable(variableModel.getProjectId(), variableModel.getId(), variableModel);
		}
		catch (JERunnerErrorException e) {
			throw new VariableException(JEMessages.ERROR_ADDING_VARIABLE_TO_PROJECT);
		}
        project.addVariable(var);
        variableRepository.save(var);
    }

    //TODO: only allowed when project is stopped?
	public void writeVariableValue(String projectId,String variableId, Object value) throws LicenseNotActiveException, VariableException {
    	LicenseProperties.checkLicenseIsActive();


		try {
			JERunnerAPIHandler.writeVariableValue(projectId, variableId, value);
		}
		catch (JERunnerErrorException e) {
			throw new VariableException(JEMessages.ERROR_WRITING_VALUE_TO_VARIABLE);
		}

		
	}

	/*public void deleteAll(String projectId) throws LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();

		variableRepository.deleteByJobEngineProjectID(projectId);
		
	}*/
	
	   public ConcurrentHashMap<String, JEVariable> getAllJEVariables(String projectId) throws  LicenseNotActiveException {
	    	LicenseProperties.checkLicenseIsActive();

		   List<JEVariable> variables = variableRepository.findByJobEngineProjectID(projectId);
			ConcurrentHashMap<String, JEVariable> map = new ConcurrentHashMap<String, JEVariable>();
			for(JEVariable variable : variables )
			{
				map.put(variable.getJobEngineElementID(), variable);
			}
			return map;
		}

	public boolean validateType(HashMap<String, String> model) {
		try {
			JEType type = JEType.valueOf(model.get("type"));
			Object value = JEVariable.castValue(type, model.get("value"));
			if(value != null) {
				return true;
			}
		}catch (Exception e) {
			return false;
		}
		return false;
		//return true;
	}

	public void deleteVariables(String projectId, List<String> ids) throws LicenseNotActiveException, ProjectNotFoundException {
		LicenseProperties.checkLicenseIsActive();

		JELogger.debug(JEMessages.DELETING_VARIABLES,
				LogCategory.DESIGN_MODE, projectId,
				LogSubModule.VARIABLE,null);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
		}
		if(ids == null) {
			for(JEVariable var: project.getVariables().values()) {
				try {
					deleteVariable(projectId, var.getJobEngineElementID());
				}
				catch (Exception e) {
					JELogger.error(JEMessages.ERROR_DELETING_VARIABLE_FROM_PROJECT + " id = " + var.getJobEngineElementID() + " " + e.getMessage(),
							LogCategory.DESIGN_MODE, projectId,
							LogSubModule.VARIABLE, var.getJobEngineElementID());
				}
			}
		}
		else {
			for(String id: ids) {
				try {
					deleteVariable(projectId, id);
				}
				catch (Exception e) {
					JELogger.error(JEMessages.ERROR_DELETING_VARIABLE_FROM_PROJECT + " id = " + id + " " + e.getMessage(),
							LogCategory.DESIGN_MODE, projectId,
							LogSubModule.VARIABLE, id);
				}
			}
		}
	}
}
