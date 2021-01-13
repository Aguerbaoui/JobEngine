package io.je.runtime.ruleenginehandler;


import io.je.ruleengine.impl.RuleEngine;
import io.je.ruleengine.models.Rule;
import io.je.runtime.models.RuleModel;

import io.je.utilities.constants.RuleEngineErrors;

import io.je.utilities.beans.JEData;

import io.je.utilities.exceptions.*;

/*
 * class responsible for Rule Engine calls
 */
public class RuleEngineHandler {


    private RuleEngineHandler() {

    }

    
    private static String verifyRuleIsValid(RuleModel ruleModel) throws RuleFormatNotValidException
    {
    	String errorMsg = null;
    	if(ruleModel.getRuleId() == null || ruleModel.getRuleId().isEmpty())
    	{
    		
    		errorMsg = RuleEngineErrors.ID_NOT_FOUND;
    		throw new RuleFormatNotValidException(errorMsg);
    	}
    	if(ruleModel.getRulePath() == null || ruleModel.getRulePath().isEmpty())
    	{
    		errorMsg = RuleEngineErrors.RULE_FILE_NOT_FOUND;
    		throw new RuleFormatNotValidException(errorMsg);
    	}
    	if(ruleModel.getProjectId() == null || ruleModel.getProjectId().isEmpty())
    	{
    		errorMsg = RuleEngineErrors.RULE_PROJECT_ID_NOT_FOUND;
    		throw new RuleFormatNotValidException(errorMsg);
    	}
    	
    	return errorMsg;
    }

    /*
     * add rule to rule engine
     */

    public static void addRule(RuleModel ruleModel) throws RuleAlreadyExistsException, RuleCompilationException, RuleNotAddedException, JEFileNotFoundException, RuleFormatNotValidException {
    	verifyRuleIsValid(ruleModel);       
        Rule rule = new Rule(ruleModel.getRuleId(), ruleModel.getProjectId(), ruleModel.getRuleId(), ruleModel.getFormat(), ruleModel.getRulePath(),ruleModel.getTopics());
        RuleEngine.addRule(rule);  
       /* if ( !RuleEngine.addTopics(ruleModel.getProjectId(), ruleModel.getTopics())) {
			throw new RuleNotAddedException("Failed to add topics");
		}*/


    }

    /*
     * update rule in rule engine
     */
    public static void updateRule(RuleModel ruleModel) throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {

    	verifyRuleIsValid(ruleModel); 
        Rule rule = new Rule(ruleModel.getRuleId(), ruleModel.getProjectId(), ruleModel.getRuleId(), ruleModel.getFormat(), ruleModel.getRulePath(),ruleModel.getTopics());
        RuleEngine.updateRule(rule);

    }


    /*
     * start running a project given a project id
     */
    public static  void runRuleEngineProject(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {
        RuleEngine.fireRules(projectId);
    }


    public static void injectData(JEData data) {
        RuleEngine.injectData(data);
    }
    /*
     * stop running a project given a project id
     */
    public static void stopRuleEngineProjectExecution(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {
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
        Rule rule = new Rule(ruleModel.getRuleId(), ruleModel.getProjectId(), ruleModel.getRuleId(), ruleModel.getFormat(), ruleModel.getRulePath(),ruleModel.getTopics());
        RuleEngine.compileRule(rule);
       
		
	}


	public static void deleteRule(String projectId,String ruleId) throws DeleteRuleException {
		RuleEngine.deleteRule(projectId,ruleId);
		
	}

	public static void addTopic(String projectId, String topic) {
		RuleEngine.addTopic(projectId, topic);
	}

}
