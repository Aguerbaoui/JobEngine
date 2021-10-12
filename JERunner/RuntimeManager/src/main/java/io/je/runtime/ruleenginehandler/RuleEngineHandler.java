package io.je.runtime.ruleenginehandler;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.mapping.InstanceModelMapping;
import io.je.utilities.models.InstanceModel;

import org.json.JSONObject;

import io.je.ruleengine.impl.RuleEngine;
import io.je.ruleengine.models.Rule;
import io.je.runtime.models.RuleModel;
import io.je.utilities.beans.JEData;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.instances.InstanceManager;
import io.je.utilities.log.JELogger;
import io.je.utilities.runtimeobject.JEObject;
import utils.log.LogCategory;
import utils.log.LogSubModule;

/*
 * class responsible for Rule Engine calls
 */
public class RuleEngineHandler {


    private RuleEngineHandler() {

    }

    
    private static String verifyRuleIsValid(RuleModel ruleModel) throws RuleFormatNotValidException
    {
		JELogger.debug(JEMessages.VALIDATING_RULE ,
				LogCategory.RUNTIME, ruleModel.getProjectId(),
				LogSubModule.RULE,ruleModel.getRuleId());
    	String errorMsg = null;
    	if(ruleModel.getRuleId() == null || ruleModel.getRuleId().isEmpty())
    	{
    		
    		errorMsg = JEMessages.ID_NOT_FOUND;
    		throw new RuleFormatNotValidException(errorMsg);
    	}
    	if(ruleModel.getRulePath() == null || ruleModel.getRulePath().isEmpty())
    	{
    		errorMsg = JEMessages.RULE_FILE_NOT_FOUND;
    		throw new RuleFormatNotValidException(errorMsg);
    	}
    	if(ruleModel.getProjectId() == null || ruleModel.getProjectId().isEmpty())
    	{
    		errorMsg = JEMessages.RULE_PROJECT_ID_NULL;
    		throw new RuleFormatNotValidException(errorMsg);
    	}
    	
    	return errorMsg;
    }

    /*
     * add rule to rule engine
     */

    public static void addRule(RuleModel ruleModel) throws RuleAlreadyExistsException, RuleCompilationException, RuleNotAddedException, JEFileNotFoundException, RuleFormatNotValidException {
    	verifyRuleIsValid(ruleModel);       
        Rule rule = new Rule(ruleModel.getRuleId(), ruleModel.getProjectId(), ruleModel.getRuleId(), ruleModel.getFormat(), ruleModel.getRulePath());
        RuleEngine.addRule(rule);  
      


    }

    /*
     * update rule in rule engine
     */
    public static void updateRule(RuleModel ruleModel) throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {

    	verifyRuleIsValid(ruleModel); 
        Rule rule = new Rule(ruleModel.getRuleId(), ruleModel.getProjectId(), ruleModel.getRuleId(), ruleModel.getFormat(), ruleModel.getRulePath());
        rule.setTopics(ruleModel.getTopics());
        RuleEngine.updateRule(rule);

    }


    /*
     * start running a project given a project id
     */
    public static  void runRuleEngineProject(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {
        RuleEngine.fireRules(projectId);
    }


    public static void injectData(String projectId,JEObject instance)  {
    try
    {
    	
        RuleEngine.assertFact(projectId,instance);
    }catch(Exception e)
    {
		JELogger.warn(JEMessages.ADD_INSTANCE_FAILED + e.getMessage(),
				LogCategory.RUNTIME, projectId,
				LogSubModule.RULE,null);
    	}
    	
        
    }
    /*
     * stop running a project given a project id
     */
    public static void stopRuleEngineProjectExecution(String projectId)  {
        RuleEngine.stopRuleExecution(projectId);
    }

    
    /*
     * build project
     */
	public static void buildProject(String projectId) throws RuleBuildFailedException {
		RuleEngine.buildProject(projectId);
		
	}

	/*
	 * compile rule 
	 */
	public static void compileRule(RuleModel ruleModel) throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException {
		verifyRuleIsValid(ruleModel); 
        Rule rule = new Rule(ruleModel.getRuleId(), ruleModel.getProjectId(), ruleModel.getRuleId(), ruleModel.getFormat(), ruleModel.getRulePath());
        RuleEngine.compileRule(rule);
       
		
	}

	public static void deleteRule(String projectId,String ruleId) throws DeleteRuleException {
		RuleEngine.deleteRule(projectId,ruleId);
		
	}

	public static void addEvent(JEEvent event) {
		RuleEngine.assertFact(event.getJobEngineProjectID(), event);
		
	}

	public static void deleteProjectRules(String projectId) {
		JELogger.debug("[project id = " + projectId+"]"+JEMessages.DELETING_RULES,
				LogCategory.RUNTIME, projectId,
				LogSubModule.RULE,null);
		RuleEngine.deleteProjectRules(projectId);
	}


	public static List<String> getRuleTopics(String projectId, String ruleId) {
		Rule rule = RuleEngine.getRule(projectId,ruleId);
		if(rule!=null)
		{
			return rule.getTopics();
		}
		return new ArrayList<>();
	}


	public static void addVariable(JEVariable variable) {
		RuleEngine.assertFact(variable.getJobEngineProjectID(), variable);
		
	}

	public static void deleteVariable(String projectId, String id) {
		RuleEngine.deleteFact(projectId,id);
		
	}
	
	public static void deleteEvent(String projectId, String id) {
		RuleEngine.deleteFact(projectId,id);
		
	}


	public static void clearRuleTopics(String projectId, String ruleId) {
		Rule rule = RuleEngine.getRule(projectId,ruleId);
		if(rule!=null)
		{
			rule.getTopics().clear();
		}
		
	}

   /* public static void setClassLoader(JEClassLoader loader) {
		RuleEngine.setClassLoader(loader);
    }*/
}
