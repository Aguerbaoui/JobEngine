package io.je.rulebuilder.components.blocks.logic;


import io.je.rulebuilder.builder.RuleBuilder;
import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

import static io.je.rulebuilder.builder.RuleBuilder.*;

public class OrBlock extends LogicBlock {

    public OrBlock(BlockModel blockModel) {
        super(blockModel);
    }

    public OrBlock() {
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();

        // Do not change unless aware
        expression.append(RuleBuilder.getDroolsConditionWithoutRepeatedDeclarations( getNotExpression() ) + "\n");

        expression.append(
                "then"
                        + "\n"
                        //+ "    System.err.println(\"OrLogicManager.getRuleMatchCounter('@{ruleName}') : \""
                        //+ "  +  OrLogicManager.getRuleMatchCounter(\"@{ruleName}\") );" + "\n"

                        + "    OrLogicManager.resetRuleMatch(\"@{ruleName}\");" + "\n"

                        //+ "    System.err.println(\"OrLogicManager.getRuleMatchCounter('@{ruleName}') after RESET : \""
                        //+ "  +  OrLogicManager.getRuleMatchCounter(\"@{ruleName}\") );" + "\n"

                        + "end"
                        + "\n\n"

        );

        for (int i = 0; i < inputBlockLinks.size(); i++) {

            expression.append(
                    "rule \"" + i + " @{ruleName}\"  @Propagation(IMMEDIATE)\n" +
                            "dialect \"mvel\"\n" +
                            "salience @{salience}\n" +
                            "enabled @{enabled}\n" +
                            "no-loop true\n" +
                            "date-effective @{dateEffective}\n" +
                            "date-expires @{dateExpires}\n" +
                            "timer (cron:@{cronExpression})\n" +
                            "\n" +

                            "when"
                            + "\n"
            );

            expression.append("    " + RuleBuilder.getDroolsConditionWithoutRepeatedDeclarations( inputBlockLinks.get(i).getExpression() ) + "\n");

            expression.append(
                    "then"
                            + "\n"
                            //+ "    System.err.println(\"OrLogicManager.getRuleMatchCounter('@{ruleName}') : \""
                            //+ "  +  OrLogicManager.getRuleMatchCounter(\"@{ruleName}\") );" + "\n"

                            + "    OrLogicManager.addRuleMatch(\"@{ruleName}\");" + "\n"

                            //+ "    System.err.println(\"OrLogicManager.getRuleMatchCounter('@{ruleName}') after ADD : \""
                            //+ "  +  OrLogicManager.getRuleMatchCounter(\"@{ruleName}\") );" + "\n"

                            + "end"
                            + "\n\n"

            );

        }

        expression.append(
                "rule \"OR LOGIC @{ruleName}\"  @Propagation(IMMEDIATE)\n" +
                        "dialect \"mvel\"\n" +
                        "salience @{salience}\n" +
                        "enabled @{enabled}\n" +
                        "no-loop true\n" +
                        "date-effective @{dateEffective}\n" +
                        "date-expires @{dateExpires}\n" +
                        "timer (cron:@{cronExpression})\n" +
                        "\n" +

                        "when"
                        + "\n"

        );

        // FIXME : should loop on input block till getters (data sources) : check Bug 5209
        String tmpExpression = "";
        for (var inputBlocksExpression : getAllInputBlocksExpressions()) {
            tmpExpression += "   " + inputBlocksExpression + "\n";
        }

        expression.append( RuleBuilder.getDroolsConditionWithoutRepeatedDeclarations( tmpExpression ) + "\n" );

        return expression.toString();
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();

        expression.append(
                NOT_DROOLS_PREFIX_CONDITION
        );
        // Do not change by getExpression()
        for (int i = 0; i < inputBlockLinks.size(); i++) {

            expression.append(inputBlockLinks.get(i).getExpression());

            if (i < inputBlockLinks.size() - 1) {
                expression.append(OR_DROOLS_CONDITION);
            }
        }

        expression.append(
                NOT_DROOLS_SUFFIX_CONDITION
        );

        return expression.toString();
    }
    
}
