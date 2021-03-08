package io.je.runtime.ruleenginehandler;


import java.time.LocalDateTime;

import org.json.JSONObject;

import io.je.ruleengine.impl.RuleEngine;
import io.je.ruleengine.models.Rule;
import io.je.runtime.config.InstanceModelMapping;
import io.je.runtime.models.InstanceModel;
import io.je.runtime.models.RuleModel;
import io.je.runtime.objects.InstanceManager;
import io.je.utilities.constants.RuleEngineErrors;

import io.je.utilities.beans.JEData;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.JEObject;

/*
 * class responsible for Rule Engine calls
 */
public class RuleEngineHandler {


    private RuleEngineHandler() {

    }

    
    private static String verifyRuleIsValid(RuleModel ruleModel) throws RuleFormatNotValidException
    {
    	JELogger.trace("Checking rule validity");
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
        Rule rule = new Rule(ruleModel.getRuleId(), ruleModel.getProjectId(), ruleModel.getRuleId(), ruleModel.getFormat(), ruleModel.getRulePath());
        RuleEngine.addRule(rule);  
      


    }

    /*
     * update rule in rule engine
     */
    public static void updateRule(RuleModel ruleModel) throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {

    	verifyRuleIsValid(ruleModel); 
        Rule rule = new Rule(ruleModel.getRuleId(), ruleModel.getProjectId(), ruleModel.getRuleId(), ruleModel.getFormat(), ruleModel.getRulePath());
        RuleEngine.updateRule(rule);

    }


    /*
     * start running a project given a project id
     */
    public static  void runRuleEngineProject(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {
        RuleEngine.fireRules(projectId);
    }


    public static void injectData(String projectId,JEData data) throws InstanceCreationFailed {
    try
    {
    	JSONObject instanceJson = new JSONObject(data.getData());
		//JELogger.info(RuleEngineHandler.class, instanceJson.toString());

		InstanceModel instanceModel = new InstanceModel();
		instanceModel.setInstanceId(instanceJson.getString(InstanceModelMapping.INSTANCEID));
		instanceModel.setModelId(instanceJson.getString(InstanceModelMapping.MODELID));
		instanceModel.setPayload(instanceJson.getJSONObject(InstanceModelMapping.PAYLOAD));
		JEObject instanceData = (JEObject) InstanceManager.createInstance(instanceModel);
		instanceData.setJeObjectLastUpdate(LocalDateTime.now());
		//JELogger.info("Data : "+ instanceJson );
        RuleEngine.assertFact(projectId,instanceData);
    }catch(InstanceCreationFailed e)
    {
    	JELogger.warning(RuleEngineHandler.class, " failed to create instance" + e.getMessage());
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
		JELogger.trace(" Deleting rules in project id = " + projectId);
		RuleEngine.deleteProjectRules(projectId);
	}
}
