package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

/*
 * Block used to writing in an instance's attribute (from DM)
 * source : previous block
 * operation id : 5003
 */
public class SetVariableBlock extends ExecutionBlock {


    /*******************************Instance definition*******************************/
    String variableId;


    public SetVariableBlock(BlockModel blockModel) {
        super(blockModel);
        try {

            variableId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.VALUE);
            isProperlyConfigured = true;
        } catch (Exception e) {
            isProperlyConfigured = false;
        } finally {
            if (variableId == null) {
                isProperlyConfigured = false;

            }
        }


    }

    public SetVariableBlock() {
        super();
    }


    @Override
    public String getExpression() {
        return "Executioner.updateVariable(" + variableId + ", " + inputBlockLinks.get(0).getReference() + ");";

    }


}
