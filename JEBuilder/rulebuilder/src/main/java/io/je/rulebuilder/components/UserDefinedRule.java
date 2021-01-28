package io.je.rulebuilder.components;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.template.ObjectDataCompiler;

import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.config.RuleBuilderConfig;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.utilities.constants.RuleBuilderErrors;
import io.je.utilities.exceptions.AddRuleBlockException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.ClassDefinition;

/*
 * Rules defined graphically by the user.
 * One GraphicalRule can be equivalents to multiple JobEngine rules ( or drls)
 * Each Job engine rule is defined by a root block ( a logic or comparison block that precedes and execution sequence)
 */
public class UserDefinedRule extends JERule {

	/*
	 * rule attributes
	 */
	RuleParameters ruleParameters;

	/*
	 * Map of all the blocks that define this rule
	 */
	BlockManager blocks = new BlockManager();
	
	/*
	 * class names - data model topics -
	 */
	
	public UserDefinedRule()
	{
		
	}
	
	public UserDefinedRule(String projectId, RuleModel ruleModel)
	{
		super(ruleModel.getRuleId(), projectId, ruleModel.getRuleName());
		ruleParameters = new RuleParameters();
		ruleParameters.setSalience(String.valueOf(ruleModel.getSalience()));
		ruleParameters.setTimer(ruleModel.getTimer());
		ruleParameters.setEnabled(ruleModel.isEnabled());
		ruleParameters.setDateEffective(ruleModel.getDateEffective());
		ruleParameters.setDateExpires(ruleModel.getDateExpires());
		
		
		
	}
	

	/*
	 * generate script rules
	 */
	public List<ScriptedRule> scriptRule() throws RuleBuildFailedException {
		blocks.init();
		int scriptedRulesCounter = 0;
		String scriptedRuleid = "";
		List<ScriptedRule> scriptedRules = new ArrayList<>();
		Set<ConditionBlock> rootBlocks = blocks.getRootBlocks();
		for (ConditionBlock root : rootBlocks) {
			scriptedRuleid = "[" + jobEngineElementID + "]" + ruleName + ++scriptedRulesCounter;
			String condition = root.getExpression(); 
			String consequences = root.getConsequences();
			String script = generateScript(scriptedRuleid,condition,consequences); 
			JELogger.info(script);
			ScriptedRule rule = new ScriptedRule(jobEngineProjectID, scriptedRuleid, script, ruleName + scriptedRulesCounter);
			scriptedRules.add(rule);			
		}
		return scriptedRules;
	}
	
    /*generate DRL for this rule */
    
	private String generateScript(String ruleId,String condition , String consequences) throws RuleBuildFailedException
	{
		
		// set rule attributes
        Map<String, String> ruleTemplateAttributes = new HashMap<>();
        ruleTemplateAttributes.put("ruleName", ruleId);
        ruleTemplateAttributes.put("salience", ruleParameters.getSalience());   
        ruleTemplateAttributes.put("enabled", String.valueOf(ruleParameters.isEnabled()));        
        ruleTemplateAttributes.put("condition", condition);        
        ruleTemplateAttributes.put("consequence", consequences);        
        ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();
        String ruleContent = "";
        try {
            ruleContent = objectDataCompiler.compile(Arrays.asList(ruleTemplateAttributes), new FileInputStream(RuleBuilderConfig.ruleTemplatePath));
        } catch(Exception e)
        {
        	throw new RuleBuildFailedException(RuleBuilderErrors.RuleBuildFailed + e.getMessage() );
        }
        return ruleContent;
		
	}

	/*
	 * add a block to this user defined rule
	 */
	public void addBlock(BlockModel blockModel) throws AddRuleBlockException {
		blocks.addBlock(blockModel);
		isBuilt = false;
	}

	/*
	 * update a block in this user defined rule
	 */
	public void updateBlock(BlockModel blockModel) throws AddRuleBlockException {
		blocks.updateBlock(blockModel);
		isBuilt = false;

	}

	/*
	 * delete a block in this user defined rule
	 */
	public void deleteBlock(String blockId) {
		blocks.deleteBlock(blockId);
		isBuilt = false;

	}

	public RuleParameters getRuleParameters() {
		return ruleParameters;
	}

	public void setRuleParameters(RuleParameters ruleParameters) {
		this.ruleParameters = ruleParameters;
		isBuilt = false;

	}

	@Override
	public List<ClassDefinition> getTopics() {
		return blocks.getTopics();
	}
	
	

}
