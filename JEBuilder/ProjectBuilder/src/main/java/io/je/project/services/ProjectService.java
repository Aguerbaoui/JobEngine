package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.repository.ProjectRepository;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerUnreachableException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.ProjectRunException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.network.JEResponse;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
/*
* Service class to handle business logic for projects
* */

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    RuleService ruleService;


    // TODO add repo jpa save later
    private static HashMap<String, JEProject> loadedProjects = new HashMap<String, JEProject>();

    /*
    * Add a new project
    * */
    public void saveProject(JEProject project) {
        //Todo add repo jpa save operation
        projectRepository.save(project);
        loadedProjects.put(project.getProjectId(), project);
    }

    /*
    * Most likely won't be used
    * */
    public void removeProject(String id) throws ProjectNotFoundException{
        //TODO remove project from jpa db

        if(!loadedProjects.containsKey(id))
        {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        }
        loadedProjects.remove(id);
    }

    /*
    *  Add a workflow to project
    * */
    public void addWorkflowToProject(JEWorkflow wf) throws ProjectNotFoundException {
        workflowService.addWorkflow(wf);
        saveProject(getProjectById(wf.getJobEngineProjectID()));
    }

    /*
    * Remove workflow from project
    * */
    public void deleteWorkflowFromProject(String projectId, String workflowId) throws ProjectNotFoundException, WorkflowNotFoundException {
        workflowService.removeWorkflow(projectId, workflowId);
        saveProject(getProjectById(projectId));
    }

    /*
    * Add a rule to project
    * */
    public void addRuleToProject(UserDefinedRule rule) throws RuleAlreadyExistsException {
        loadedProjects.get(rule.getJobEngineProjectID()).addRule(rule);
    }

    /*
    * Get all loaded Projects
    * */
    public static HashMap<String, JEProject> getLoadedProjects() {
        return loadedProjects;
    }

    /*
    * Return a project loaded in memory
    * */
    public static JEProject getProjectById(String id) {
        return loadedProjects.get(id);

    }

    /*
     * Set loaded project in memory
     * */
    public static void setLoadedProjects(HashMap<String, JEProject> loadedProjects) {
        ProjectService.loadedProjects = loadedProjects;
    }

    /*
     * Build a workflow by id
     * */
    public void buildWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException {
        workflowService.buildWorkflow(projectId, workflowId);
    }

    /*
    * Run a workflow by id
    * */
    public void runWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, IOException, WorkflowNotFoundException {
        workflowService.runWorkflow(projectId, workflowId);
    }

    /*
    Builds all the rules and workflows
    * */
    public void buildAll(String projectId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException, RuleBuildFailedException, JERunnerUnreachableException {
        //TODO add build all rules
    	//ruleService.buildRules(projectId);
        workflowService.buildWorkflows(projectId);
    }

    
    /*
     * run project => send request to jeRunner to run project
     */
    public void runAll(String projectId) throws IOException, ProjectNotFoundException, JERunnerUnreachableException, ProjectRunException {
    	if(loadedProjects.containsKey(projectId))
    	{
    		JEProject project = loadedProjects.get(projectId);
    		JEResponse jeRunnerResp = null;
    		if(!project.isRunning())
    		{
    			jeRunnerResp = JERunnerAPIHandler.runProject(projectId);
    			project.setRunning(true);

    		}
    		else
    		{
    			throw new ProjectRunException(Errors.PROJECT_RUNNING);

    		}
    		if (jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
    			throw new ProjectRunException(jeRunnerResp.getMessage());
    		}
    	}
    	else
    	{
    		throw new ProjectNotFoundException(Errors.projectNotFound);
    	}


      
    }
    
    public void stopProject(String projectId) throws ProjectNotFoundException, JERunnerUnreachableException, IOException, ProjectRunException {
		if(!loadedProjects.containsKey(projectId)){
			throw new ProjectNotFoundException(Errors.projectNotFound);
    	}
		JEProject project = loadedProjects.get(projectId);		
		JEResponse jeRunnerResp = null;
		if(project.isRunning())
		{
			jeRunnerResp = JERunnerAPIHandler.stopProject(projectId);
			if (jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
    			throw new ProjectRunException(jeRunnerResp.getMessage());
    		}
			project.setRunning(false);

		}
		else
		{
			//TODO change to another exception
			throw new ProjectRunException("PROJECT IS ALREADY STOPPED");

		}

		}

    /*Return all currently available workflows in project*/
    public HashMap<String, JEWorkflow> getAllWorkflows(String projectId) {
       return loadedProjects.get(projectId).getWorkflows();
    }

    /*
    * Return project by id
    * */
    public JEProject getProject(String projectId) {
        if(!loadedProjects.containsKey(projectId)) {
            Optional<JEProject> p =  projectRepository.findById(projectId);
            JEProject project = p.isEmpty() ? null : p.get();
            if(project != null) {
                loadedProjects.put(projectId, project);
            }
            return project;
        }
        return loadedProjects.get(projectId);
    }

	
		
	
}
