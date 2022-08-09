package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

public class TriggerEventBlock extends ExecutionBlock {

    String eventId = null;
    String eventName = null;

    public TriggerEventBlock(BlockModel blockModel) {
        super(blockModel);
        if (blockModel.getBlockConfiguration() != null && blockModel.getBlockConfiguration()
                .get(AttributesMapping.VALUE) != null) {
            eventId = (String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.VALUE);
            eventName = (String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.VALUE2);

        }

    }

    public TriggerEventBlock() {
        super();
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        //return "Executioner.triggerEvent(\"" +jobEngineProjectID  +"\" , \""+ eventId  + "\");";
        if (eventId == null) {

            JELogger.error("Failed to build block : " + blockName, LogCategory.DESIGN_MODE, jobEngineProjectID, LogSubModule.RULE, ruleId);
            isProperlyConfigured = false;
            throw new RuleBuildFailedException(blockName + " is not configured properly");

        }
        return "Executioner.triggerEvent(\"" + jobEngineProjectID + "\" , \""
                + eventId + "\",\""
                + eventName + "\",\"" +
                ruleId + "\",\"" +
                blockName
                + "\");";

    }


}
