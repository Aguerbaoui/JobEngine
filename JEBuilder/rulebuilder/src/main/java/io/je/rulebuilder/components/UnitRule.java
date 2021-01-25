package io.je.rulebuilder.components;

import java.util.List;

import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.utilities.runtimeobject.JEObject;

public class UnitRule extends JEObject {
	
	 //RULE ID = [OriginalRuleId] + originalrule Name + int (counter) 
	RuleParameters ruleParameters;
    ConditionBlock rootBlock;
	public UnitRule(String jobEngineElementID, String jobEngineProjectID, RuleParameters ruleParameters,
			ConditionBlock rootBlock) {
		super(jobEngineElementID, jobEngineProjectID);
		this.ruleParameters = ruleParameters;
		this.rootBlock = rootBlock;
	}
    
    
    
    

    
}
