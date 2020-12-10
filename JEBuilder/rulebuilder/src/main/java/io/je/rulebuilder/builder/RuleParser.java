package io.je.rulebuilder.builder;

import static org.drools.model.DSL.supply;
import static org.drools.model.PatternDSL.accFunction;
import static org.drools.model.PatternDSL.accumulate;
import static org.drools.model.PatternDSL.after;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.and;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.declarationOf;
import static org.drools.model.PatternDSL.execute;
import static org.drools.model.PatternDSL.globalOf;
import static org.drools.model.PatternDSL.not;
import static org.drools.model.PatternDSL.on;
import static org.drools.model.PatternDSL.or;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.query;
import static org.drools.model.PatternDSL.reactOn;
import static org.drools.model.PatternDSL.reactiveFrom;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.PatternDSL.valueOf;
import static org.drools.model.PatternDSL.when;

import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.model.Index;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.mvel.DrlDumper;

import io.je.rulebuilder.components.Condition;
import io.je.rulebuilder.components.Consequence;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.RuleTemplate;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.LogicalBlock;
import io.je.rulebuilder.components.blocks.logical.AndBlock;
import io.je.rulebuilder.components.blocks.logical.GreaterThanBlock;
import io.je.rulebuilder.tests.Person;
import io.je.rulebuilder.tests.Result;
import io.je.rulebuilder.utils.logic.Node;

public class RuleParser {

	public static void main(String[] args) {
		/*JERule jeRule = new JERule();
		jeRule.setName("test rule");
		jeRule.setSalience(10);
		jeRule.setDuration(10);
		Block andBlock = new AndBlock();
		Block greaterThanBlock = new GreaterThanBlock(DataType.CLASSATTRIBUTE, "Tank", "volume", DataType.CONSTANT,
				"none", "20");
		Block greaterThanBlock2 = new GreaterThanBlock(DataType.CLASSATTRIBUTE, "Pump", "level", DataType.CONSTANT,
				"none", "1");

		Condition condition = new Condition((LogicalBlock) andBlock);
		condition.getRoot().addChild((LogicalBlock) greaterThanBlock);
		condition.getRoot().addChild((LogicalBlock) greaterThanBlock2);
		jeRule.setCondition(condition);

		Consequence cons = new Consequence();
		jeRule.addConsequence(cons);

		// method 1 : 
		RuleTemplate ruleTemplate = new RuleTemplate(jeRule);
		DRLBuilder drlBuilder = new DRLBuilder();
		drlBuilder.generateDRL(ruleTemplate);
	*/

	}

}
