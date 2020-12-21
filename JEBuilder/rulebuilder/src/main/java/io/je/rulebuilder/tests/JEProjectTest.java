package io.je.rulebuilder.tests;

import java.util.HashMap;
import java.util.Map;

import io.je.rulebuilder.components.ComposedRule;
import io.je.rulebuilder.models.BlockModel;

public class JEProjectTest {
	
	Map<String,ComposedRule> allRules = new HashMap<>();
	
	public void addBlock( BlockModel blockModel)
	{
		allRules.get(blockModel.getBlockId()).addBlock(blockModel);
	}

}
