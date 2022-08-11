package io.je.rulebuilder.components.blocks.event;


import io.je.rulebuilder.components.BlockLinkModel;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.utilities.exceptions.RuleBuildFailedException;

import java.util.List;

public class TimeOutEventBlock extends PersistableBlock {

    String eventId = null;


    public TimeOutEventBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
                             String blockDescription, int timePersistenceValue, String timePersistenceUnit, String eventId, List<BlockLinkModel> inputBlockIds, List<BlockLinkModel> outputBlocksIds) {
        super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription, timePersistenceValue,
                timePersistenceUnit, inputBlockIds, outputBlocksIds);
        this.eventId = eventId;
    }


    public TimeOutEventBlock() {
        super();
    }

    @Override
    public String getReference(String optional) {
        return getBlockNameAsVariable();
    }

    @Override
    public String toString() {
        return "ExecutionBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
                + ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
    }


    @Override
    public String getExpression() {
        return " $e : JEEvent(jobEngineElementID ==\"" + eventId + "\",isTriggered()==true)";
    }


    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        // FIXME
        StringBuilder expression = new StringBuilder();

        expression.append("\n not ( " + getExpression() + " ) \n");

        return expression.toString();
    }

}
