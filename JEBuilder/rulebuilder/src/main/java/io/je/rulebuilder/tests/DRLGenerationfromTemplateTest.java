package io.je.rulebuilder.tests;


import io.je.rulebuilder.builder.DRLBuilder;
import io.je.rulebuilder.components.RuleTemplate;

public class DRLGenerationfromTemplateTest {

	
	public static void main(String[] args) {
		
		String ruleName = "testRule";
		String duration = "3s";
		String salience = "10";
		String condition = "p : Person(Value < 10 )";
		String consequence = "logger.info(\"hi\")";
		DRLBuilder drlBuilder = new DRLBuilder();
		
		//RuleTemplate ruleTemplate = new RuleTemplate(ruleName, duration, salience, condition, consequence);
		//drlBuilder.generateDRL(ruleTemplate);
		

	}

}
