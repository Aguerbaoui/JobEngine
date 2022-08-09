package io.je.rulebuilder.components.blocks.logic;


import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class OrBlock extends LogicBlock {


    public OrBlock(BlockModel blockModel) {
        super(blockModel);
        operator = " or ";
    }

    public OrBlock() {
        // FIXME constant
        operator = " or ";
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();

        expression.append(getNotExpression() + "\n");

        expression.append(
                "then"
                        + "\n"
                        + "    System.err.println(\"OrLogicManager.getRuleMatchCounter('@{ruleName}') : \""
                        + "  +  OrLogicManager.getRuleMatchCounter(\"@{ruleName}\") );" + "\n"

                        + "    OrLogicManager.resetRuleMatch(\"@{ruleName}\");" + "\n"

                        + "    System.err.println(\"OrLogicManager.getRuleMatchCounter('@{ruleName}') after RESET : \""
                        + "  +  OrLogicManager.getRuleMatchCounter(\"@{ruleName}\") );" + "\n"

                        + "end"
                        + "\n\n"

                        + "rule \"Logic Or reset @{ruleName}\"  @Propagation(IMMEDIATE)\n" +
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

        for (int i = 0; i < inputBlocks.size(); i++) {

            expression.append("    " + inputBlocks.get(i).getExpression() + "\n");

            expression.append(
                    "then"
                            + "\n"
                            + "    System.err.println(\"OrLogicManager.getRuleMatchCounter('@{ruleName}') : \""
                            + "  +  OrLogicManager.getRuleMatchCounter(\"@{ruleName}\") );" + "\n"

                            + "    OrLogicManager.addRuleMatch(\"@{ruleName}\");" + "\n"

                            + "    System.err.println(\"OrLogicManager.getRuleMatchCounter('@{ruleName}') after ADD : \""
                            + "  +  OrLogicManager.getRuleMatchCounter(\"@{ruleName}\") );" + "\n"

                            + "end"
                            + "\n\n"

                            + "rule \"" + i + " @{ruleName}\"  @Propagation(IMMEDIATE)\n" +
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

        }

        expression.append( // FIXME : Add JEVariable Definitions if needed (currently used for debug)
                ""
        );

        return expression.toString();
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();

        expression.append(
                " not ( "
        );
        // Do not change by getExpression()
        for (int i = 0; i < inputBlocks.size(); i++) {

            expression.append("\n"
                            + inputBlocks.get(i).getExpression()
                            + "\n");

            if (i < inputBlocks.size() - 1) {
                expression.append( operator);
            }
        }

        expression.append(
                " ) "
        );

        return expression.toString();
    }


}
