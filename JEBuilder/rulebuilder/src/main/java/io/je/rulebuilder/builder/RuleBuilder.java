package io.je.rulebuilder.builder;

import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.RuleTemplate;

public class RuleBuilder {
	RuleConstructor ruleConstructor;
	RuleTemplateBuilder ruleTemplateBuilder;
	DRLBuilder drlBuilder;
	
	
	public void addBlockToRule (Object json)
	{
		//ruleConstructor.addBlockToRule();
	}
	
	public void buildRule(Object json)
	{
		JERule jERule = ruleConstructor.createRule(json);
		RuleTemplate ruleTemplate= ruleTemplateBuilder.createRuleTemplate(jERule);
		drlBuilder.generateDRL(ruleTemplate);
		
		
		
		
	}

}
