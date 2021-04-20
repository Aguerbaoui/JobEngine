package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.models.VariableModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Service
public class VariableService {

    /*
    * Add a new variable to the project
    * */
    public void addVariable(VariableModel variableModel) throws ConfigException, ProjectNotFoundException, VariableAlreadyExistsException, JERunnerErrorException, ExecutionException, InterruptedException {
        ConfigurationService.checkConfig();
        JEProject project = ProjectService.getProjectById(variableModel.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if(project.variableExists(variableModel.getId())) {
            throw new VariableAlreadyExistsException(JEMessages.VARIABLE_EXISTS);
        }

        JEVariable var = new JEVariable();
        var.setVariableName(variableModel.getName());
        var.setJobEngineElementID(variableModel.getId());
        var.setJobEngineProjectID(variableModel.getProjectId());
        var.setVariableTypeString(variableModel.getType());
        var.setVariableTypeClass(JEVariable.getType(variableModel.getType()));
        var.setVariableValue(variableModel.getValue());
        var.setJeObjectCreationDate(LocalDateTime.now());
        var.setJeObjectLastUpdate(LocalDateTime.now());
        JERunnerAPIHandler.addVariable(variableModel.getProjectId(), variableModel.getId(), variableModel);
        project.addVariable(var);
    }

    /*
    * Delete a variable from the project
    * */
    public void deleteVariable(String projectId, String varId) throws ConfigException, ProjectNotFoundException, VariableNotFoundException, InterruptedException, JERunnerErrorException, ExecutionException {
        ConfigurationService.checkConfig();
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if(!project.variableExists(varId)) {
            throw new VariableNotFoundException(JEMessages.VARIABLE_NOT_FOUND);
        }
        JERunnerAPIHandler.removeVariable(projectId, varId);
        project.removeVariable(varId);
    }

    /*
    * Update an existing variable in the project
    * */
    public void updateVariable(VariableModel variableModel) throws ConfigException, ProjectNotFoundException, VariableNotFoundException, InterruptedException, JERunnerErrorException, ExecutionException {
        ConfigurationService.checkConfig();
        JEProject project = ProjectService.getProjectById(variableModel.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if(!project.variableExists(variableModel.getId())) {
            throw new VariableNotFoundException(JEMessages.VARIABLE_NOT_FOUND);
        }
        JEVariable var = new JEVariable();
        var.setVariableName(variableModel.getName());
        var.setJobEngineElementID(variableModel.getId());
        var.setJobEngineProjectID(variableModel.getProjectId());
        var.setVariableTypeString(variableModel.getType());
        var.setVariableTypeClass(JEVariable.getType(variableModel.getType()));
        var.setVariableValue(variableModel.getValue());
        var.setJeObjectCreationDate(LocalDateTime.now());
        var.setJeObjectLastUpdate(LocalDateTime.now());
        JERunnerAPIHandler.addVariable(variableModel.getProjectId(), variableModel.getId(), variableModel);
        project.addVariable(var);
    }
}
