package io.je.ruleengine.tests;

import io.je.ruleengine.impl.RuleEngine;
import io.je.ruleengine.models.Rule;

public class RuleCompilationTest {
	public static void main(String[] args) {
		Rule rule = new Rule();
		rule.setName("myrule");
		rule.setPath("D:\\Desktop\\test\\test.drl");
		rule.setJobEngineProjectID("123");		
		RuleEngine engine = new RuleEngine();
		engine.addRule(rule);
		engine.fireRules("123");
		
		
		
	}

}
