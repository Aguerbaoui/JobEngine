package io.je.runtime.services;

import org.springframework.stereotype.Service;

import io.je.runtime.data.DataListener;
import io.je.runtime.models.ClassModel;
import io.je.runtime.models.InstanceModel;
import io.je.runtime.models.RuleModel;
import io.je.runtime.objects.ClassManager;
import io.je.runtime.objects.InstanceManager;
import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DeleteRuleException;
import io.je.utilities.exceptions.JEFileNotFoundException;
import io.je.utilities.exceptions.ProjectAlreadyRunningException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleCompilationException;
import io.je.utilities.exceptions.RuleFormatNotValidException;
import io.je.utilities.exceptions.RuleNotAddedException;
import io.je.utilities.exceptions.RulesNotFiredException;
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
		
	}
    
    
	//run project
	public void runProject(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException 
	{
		//start listening to datasources
		//start workflows
		RuleEngineHandler.runRuleEngineProject(projectId);
		
	}
	
	//stop project
	//run project
	public void stopProject(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException 
	{
		//start listening to datasources
		//start workflows
		//RuleEngineHandler.stopProject(projectId);
		
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


	
	
	
	
	
	/////////////////////////////Workflows
	
	//add workflow
	//update workflow
	//delete workflow
	
	/////////////////////////////Classes
	//add class
	public void addClass(ClassModel classModel) throws  ClassLoadException {
		
		ClassManager.loadClass(classModel.getClassId(),classModel.getClassName(),classModel.getClassPath());
		
	}
	
	public void setClassLoadPath(String classPath){
		classLoadPath = classPath;
	}
	//update class
	//delete class
	
	///////////////////////////////instance
	public void addInstanceTest(InstanceModel instanceModel)
	{
		InstanceManager.createInstance(instanceModel);
	}

}
