package io.je.runtime.services;

import io.je.runtime.models.WorkflowModel;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.utilities.exceptions.*;
import org.springframework.stereotype.Service;

import io.je.runtime.data.DataListener;
import io.je.runtime.loader.JEClassLoader;
import io.je.runtime.models.RuleModel;
import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.utilities.logger.JELogger;


/*
 * Service class to handle JERunner inputs
 */
@Service
public class RuntimeDispatcher {
	
	
	public String classLoadPath = ""; 
    
    /////////////////////////////////PROJECT
	//build project
	public void buildProject(String projectId) throws RuleBuildFailedException
	{
		//start listening to datasources
		//start workflows
		RuleEngineHandler.buildProject(projectId);
		WorkflowEngineHandler.buildProject(projectId);
		
	}
    
    
	//run project
	public void runProject(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException, WorkflowNotFoundException {
		//start listening to datasources
		//start workflows
		RuleEngineHandler.runRuleEngineProject(projectId);
		WorkflowEngineHandler.runAllWorkflows(projectId);
	}
	
	//stop project
	//run project
	public void stopProject(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException 
	{
		//start listening to datasources
		//start workflows
		//RuleEngineHandler.stopProject(projectId);
		WorkflowEngineHandler.stopProjectWorfklows(projectId);
	}
	
	//////////////////////////////RULES
	
	//add rule
	public void addRule(RuleModel ruleModel) throws RuleAlreadyExistsException, RuleCompilationException, RuleNotAddedException, JEFileNotFoundException, RuleFormatNotValidException
	{
		RuleEngineHandler.addRule(ruleModel);
		DataListener.addTopics(ruleModel.getTopics());
		
	}
	//update rule
	public void updateRule(RuleModel ruleModel) throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException
	{
		
		RuleEngineHandler.updateRule(ruleModel);
		
	}
	
	//compile rule 
	public void compileRule(RuleModel ruleModel) throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException
	{
		RuleEngineHandler.compileRule(ruleModel);
	}


	//delete rule
	public void deleteRule(String projectId,String ruleId) throws DeleteRuleException
	{
		RuleEngineHandler.deleteRule(projectId,ruleId);
	}

	//***********************************WORKFLOW********************************************************
	/*
	 * Add a workflow to the engine
	 * */
	public void addWorkflow(WorkflowModel wf) {
		WorkflowEngineHandler.addProcess(wf.getKey(), wf.getName(), wf.getPath(), wf.getProjectId());
	}

	/*
	 * Launch a workflow without variables
	 * */
	public void launchProcessWithoutVariables(String key) throws WorkflowNotFoundException {
		WorkflowEngineHandler.launchProcessWithoutVariables(key);
	}

	/*
	 * Run all workflows deployed in the engine without project specification
	 * */
	public void runAllWorkflows() throws WorkflowNotFoundException {
		WorkflowEngineHandler.runAllWorkflows();
	}

	/*
	 * Deploy a workflow to the engine
	 * */
	public void buildWorkflow(String key) {
		WorkflowEngineHandler.deployBPMN(key);
	}
	
	/////////////////////////////Classes
	//add class
	public void addClass(String classPath) throws  ClassLoadException {
		JELogger.info(getClass(), " Loading class from : " + classPath);
		String [] mpath = System.getProperty("java.class.path").split(";");
		//String path = mpath[0]+"\\io\\je\\runtime";
		//String path = mpath[0];
		JEClassLoader.loadClass(classPath, classLoadPath);
		JELogger.info(getClass(), " CLASS LOADED TO :" + classLoadPath);
		
	}
	
	public void setClassLoadPath(String classPath){
		classLoadPath = classPath;
	}


	//update class
	//delete class
	

}
