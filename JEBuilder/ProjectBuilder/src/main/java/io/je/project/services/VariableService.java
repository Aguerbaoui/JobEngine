package io.je.project.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.je.utilities.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.project.config.LicenseProperties;
import io.je.project.repository.VariableRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.apis.JERunnerRequester;
import io.je.utilities.beans.JEType;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.VariableModel;
import utils.log.LogCategory;
import utils.log.LogSubModule;

@Service
public class VariableService {

    @Autowired
	VariableRepository variableRepository;

	@Autowired
	@Lazy
	ProjectService projectService;


	public Collection<VariableModel> getAllVariables(String projectId) throws ProjectNotFoundException, LicenseNotActiveException, ProjectLoadException {
    	LicenseProperties.checkLicenseIsActive();


		JEProject project = projectService.getProjectById(projectId);
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
	
	public JEVariable getVariable(String projectId, String variableId) throws ProjectNotFoundException, VariableNotFoundException, LicenseNotActiveException, ProjectLoadException {
    	LicenseProperties.checkLicenseIsActive();
		JEProject project = projectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException("[projectId = " + projectId +"]"+JEMessages.PROJECT_NOT_FOUND);
		}
		JEVariable variable = null;
		Optional<JEVariable> var = variableRepository.findById(variableId);
		if(var.isEmpty()) {
			for(JEVariable jeVariable: variableRepository.findByJobEngineElementName(variableId)) {
				if(jeVariable.getJobEngineProjectID().equals(project.getProjectId())) {
					variable = jeVariable;
					break;
				}
			}
		}
		else variable = var.get();
		if(variable == null)
		{	String strError = JEMessages.VARIABLE_NOT_FOUND+ variableId; 
			JELogger.error(strError,LogCategory.DESIGN_MODE, projectId,LogSubModule.VARIABLE,variableId);
			throw new VariableNotFoundException(strError);
		}
		return variable;
		
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
    public void addVariable(VariableModel variableModel) throws ProjectNotFoundException, VariableAlreadyExistsException, LicenseNotActiveException, VariableException, ProjectLoadException {
    	//LicenseProperties.checkLicenseIsActive();

    	JELogger.debug(JEMessages.ADDING_VARIABLE,
                LogCategory.DESIGN_MODE, variableModel.getProjectId(),
                LogSubModule.VARIABLE,variableModel.getId());
        JEProject project = projectService.getProjectById(variableModel.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if(project.variableExists(variableModel.getId())) {
            throw new VariableAlreadyExistsException(JEMessages.VARIABLE_EXISTS);
        }
        variableModel.setProjectName(project.getProjectName());
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
    public void deleteVariable(String projectId, String varId) throws ProjectNotFoundException, VariableNotFoundException, LicenseNotActiveException, VariableException, ProjectLoadException {
    	LicenseProperties.checkLicenseIsActive();

    	JELogger.debug(JEMessages.DELETING_VARIABLE,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.VARIABLE,varId);
        JEProject project = projectService.getProjectById(projectId);
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
    public void updateVariable(VariableModel variableModel) throws ProjectNotFoundException, VariableNotFoundException, LicenseNotActiveException, VariableException, ProjectLoadException {
    	LicenseProperties.checkLicenseIsActive();

    	JELogger.debug(JEMessages.ADDING_VARIABLE,
                LogCategory.DESIGN_MODE, variableModel.getProjectId(),
                LogSubModule.VARIABLE,variableModel.getId());
        JEProject project = projectService.getProjectById(variableModel.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if(!project.variableExists(variableModel.getId())) {
            throw new VariableNotFoundException(JEMessages.VARIABLE_NOT_FOUND);
        }
        variableModel.setProjectName(project.getProjectName());
        JEVariable var = new JEVariable(variableModel.getId(),variableModel.getProjectId(),variableModel.getName(),variableModel.getType(), variableModel.getInitialValue(),variableModel.getDescription(),variableModel.getCreatedBy(),variableModel.getModifiedBy());
        var.setJobEngineProjectName(project.getProjectName());
        var.setJeObjectCreationDate(Instant.now());
        var.setJeObjectLastUpdate(Instant.now());
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
	public void writeVariableValue(JEVariable variable, Object value) throws LicenseNotActiveException, VariableException {
    	LicenseProperties.checkLicenseIsActive();

		try {
			JERunnerRequester.updateVariable(variable.getJobEngineProjectID(), variable.getJobEngineElementID(), value,true);
			variable.setValue(value);
			variableRepository.save(variable);
		}
		catch (Exception e) {
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

	public void deleteVariables(String projectId, List<String> ids) throws LicenseNotActiveException, ProjectNotFoundException, VariableNotFoundException, ProjectLoadException {
		LicenseProperties.checkLicenseIsActive();

		JELogger.debug(JEMessages.DELETING_VARIABLES,
				LogCategory.DESIGN_MODE, projectId,
				LogSubModule.VARIABLE,null);
		JEProject project = projectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
		}
		if(ids == null) {
			for(JEVariable var: project.getVariables().values()) {
				try {
					deleteVariable(projectId, var.getJobEngineElementID());
				}
				catch (Exception e) {
					JELogger.error("[variable="+var.getJobEngineElementName() + "]"+JEMessages.ERROR_DELETING_VARIABLE_FROM_PROJECT +  e.getMessage(),
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
					JELogger.error("[variable="+project.getVariable(id).getJobEngineElementName() + "]"+JEMessages.ERROR_DELETING_VARIABLE_FROM_PROJECT  + e.getMessage(),
							LogCategory.DESIGN_MODE, projectId,
							LogSubModule.VARIABLE, id);
				}
			}
		}
	}

    public void cleanUpHouse() {
		   variableRepository.deleteAll();
    }

	public VariableModel getVariableModelFromBean(JEVariable jeVariable) {
		   VariableModel variableModel = new VariableModel();
		   variableModel.setId(jeVariable.getJobEngineElementID());
		   variableModel.setName(jeVariable.getJobEngineElementName());
		   variableModel.setValue(jeVariable.getValue().toString());
		   variableModel.setProjectName(jeVariable.getJobEngineProjectName());
		   variableModel.setProjectId(jeVariable.getJobEngineProjectID());
		   variableModel.setDescription(jeVariable.getDescription());
		   variableModel.setModifiedBy(jeVariable.getJeObjectModifiedBy());
		   variableModel.setCreatedAt(jeVariable.getJeObjectCreationDate().toString());
		   variableModel.setCreatedBy(jeVariable.getJeObjectCreatedBy());
		   variableModel.setLastModifiedAt(jeVariable.getJeObjectLastUpdate().toString());
		   variableModel.setInitialValue(jeVariable.getInitialValue().toString());
		   variableModel.setType(jeVariable.getType().toString());
		   return variableModel;
	}
}
