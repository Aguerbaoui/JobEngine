package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.BlockLinkModel;
import io.je.rulebuilder.models.BlockModel;

import java.util.List;

public abstract class ExecutionBlock extends Block {

    public ExecutionBlock(BlockModel blockModel) {
        super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
                blockModel.getDescription(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());
    }


    public ExecutionBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
                          String blockDescription, List<BlockLinkModel> inputBlockIds, List<BlockLinkModel> outputBlocksIds) {
        super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription, inputBlockIds, outputBlocksIds);
    }


    public ExecutionBlock() {
        super();
    }

    @Override
    public String getReference(String optional) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return "ExecutionBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
                + ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
    }


    @Override
    public String getAsOperandExpression() {
        return null;
    }

}
