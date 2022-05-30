package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.arithmetic.multipleInput.*;
import io.je.rulebuilder.components.blocks.arithmetic.singleinput.CalculationBlock;
import io.je.rulebuilder.components.blocks.arithmetic.singleinput.TypeConverterBlock;
import io.je.rulebuilder.components.blocks.arithmetic.singleinput.UnitConversionBlock;
import io.je.rulebuilder.components.blocks.comparison.ComparisonBlock;
import io.je.rulebuilder.components.blocks.comparison.InRangeBlock;
import io.je.rulebuilder.components.blocks.comparison.OutOfRangeBlock;
import io.je.rulebuilder.components.blocks.comparison.TimeComparisonBlock;
import io.je.rulebuilder.components.blocks.event.AcceptEventBlock;
import io.je.rulebuilder.components.blocks.execution.*;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.components.blocks.getter.VariableGetterBlock;
import io.je.rulebuilder.components.blocks.logic.AndBlock;
import io.je.rulebuilder.components.blocks.logic.JoinBlock;
import io.je.rulebuilder.components.blocks.logic.NotBlock;
import io.je.rulebuilder.components.blocks.logic.OrBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.AddRuleBlockException;

public class BlockFactory {


    /**
     * creates a block from a block Model based on the operationId specified in the blockModel
     *
     * @param blockModel
     * @return
     * @throws AddRuleBlockException
     */
    public static Block createBlock(BlockModel blockModel) throws AddRuleBlockException {
        int operationId = blockModel.getOperationId();

        /***************************************** COMPARISON BLOCKS ************************************************/

        if (operationId >= 2001 && operationId <= 2013) {
            return new ComparisonBlock(blockModel);
        }

        switch (operationId) {
            case 2014:
                return new TimeComparisonBlock(blockModel);
            case 2015:
                return new TimeComparisonBlock(blockModel);
            case 2016:
                return new InRangeBlock(blockModel);
            case 2017:
                return new OutOfRangeBlock(blockModel);
            default:
                break;
        }


        /***************************************** ARITHMETIC BLOCKS ************************************************/

        if (operationId >= 1005 && operationId <= 1026 || operationId == 9999) {
            return new CalculationBlock(blockModel);
        }
        switch (operationId) {

            // sum
            case 1001:
                return new SumBlock(blockModel);
            // Subtract
            case 1002:
                return new SubtractBlock(blockModel);
            // Multiply
            case 1003:
                return new MultiplyBlock(blockModel);
            // Divide
            case 1004:
                return new DivideBlock(blockModel);
            case 1027:
                return new UnitConversionBlock(blockModel);
            case 1028:
                return new MinimumBlock(blockModel);
            case 1029:
                return new HighLimitingBlock(blockModel);
            case 1030:
                return new LowLimitingBlock(blockModel);
            case 1031:
                return new TypeConverterBlock(blockModel);
            case 1032:
                return new MaximumBlock(blockModel);
            case 1033:
                return new AverageBlock(blockModel);



            /*
             * Logic blocks
             */

            // And Block
            case 3001:
                return new AndBlock(blockModel);
            // Or Block
            case 3002:
                return new OrBlock(blockModel);
            // XOR Block
            case 3003:
                //return new XorBlock(blockModel);
                // Join Block
            case 3004:
                return new JoinBlock(blockModel);
            // NOT Block
            case 3005:
                return new NotBlock(blockModel);

            case 3:
                break;
            /*
             * Getter blocks
             */
            case 4001:
                break;
            case 4002:
                return new AttributeGetterBlock(blockModel);
            case 4003:
                break;
            case 4005:
                return new VariableGetterBlock(blockModel);

            /*
             * Execution blocks
             */
            case 5001:
                return new LogBlock(blockModel);
            case 5002:
                return new TriggerEventBlock(blockModel);
            case 5003:
                return new LinkedSetterBlock(blockModel);
            case 5004:
                return new AttachedSetterBlock(blockModel);
            case 5005:
                return new SetterBlock(blockModel);
            case 5007:
                return new LinkedVariableSetterBlock(blockModel);
            case 6001:
                return new AcceptEventBlock(blockModel);
            // no operation with such id
            default:
                throw new AddRuleBlockException(JEMessages.BLOCK_OPERATION_ID_UNKNOWN);
        }
        throw new AddRuleBlockException("[" + blockModel.getBlockName() + "] " + JEMessages.BLOCK_OPERATION_ID_UNKNOWN);

    }
}

