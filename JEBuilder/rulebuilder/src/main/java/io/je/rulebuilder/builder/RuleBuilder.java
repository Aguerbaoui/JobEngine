package io.je.rulebuilder.builder;

import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.RuleTemplate;
import io.je.rulebuilder.models.BlockModel;

public class RuleBuilder {
    RuleConstructor ruleConstructor;
    RuleTemplateBuilder ruleTemplateBuilder;
    DRLBuilder drlBuilder;


    public void addBlockToRule(BlockModel json) {
        //ruleConstructor.addBlockToRule();
        System.out.println(json);
    }

    public void buildRule(Object json) {
        JERule jERule = ruleConstructor.createRule(json);
        RuleTemplate ruleTemplate = ruleTemplateBuilder.createRuleTemplate(jERule);
        drlBuilder.generateDRL(ruleTemplate);


    }

}
