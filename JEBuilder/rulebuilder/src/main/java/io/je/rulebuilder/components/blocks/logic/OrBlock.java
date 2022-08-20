package io.je.rulebuilder.components.blocks.logic;


import io.je.rulebuilder.components.BlockLink;
import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class OrBlock extends LogicBlock {


    // TODO constants
    public OrBlock(BlockModel blockModel) {
        super(blockModel);
        operator = " or ";
    }

    public OrBlock() {
        operator = " or ";
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();

        // Do not change unless aware
        expression.append(getNotExpression() + "\n");

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

        for (int i = 0; i < inputBlocks.size(); i++) {

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

        // FIXME : should loop on input block till data sources : check Bug 5209
        for (int i = 0; i < inputBlocks.size(); i++) {
            for (BlockLink blockLink: inputBlocks.get(i).getBlock().getInputBlocks()) {
                expression.append("   " + blockLink.getExpression() + "\n");
            }
        }

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
                expression.append(operator);
            }
        }

        expression.append(
                " ) "
        );

        return expression.toString();
    }


}
