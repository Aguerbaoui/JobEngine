package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.components.blocks.arithmetic.BiasBlock;
import io.je.rulebuilder.components.blocks.arithmetic.DivideBlock;
import io.je.rulebuilder.components.blocks.arithmetic.GainBlock;
import io.je.rulebuilder.components.blocks.arithmetic.MultiplyBlock;
import io.je.rulebuilder.components.blocks.arithmetic.PowerBlock;
import io.je.rulebuilder.components.blocks.arithmetic.SQRTBlock;
import io.je.rulebuilder.components.blocks.arithmetic.SubtractBlock;
import io.je.rulebuilder.components.blocks.arithmetic.SumBlock;
import io.je.rulebuilder.components.blocks.arithmetic.UnitConversionBlock;
import io.je.rulebuilder.components.blocks.comparison.EqualsBlock;
import io.je.rulebuilder.components.blocks.comparison.GreaterOrEqualBlock;
import io.je.rulebuilder.components.blocks.comparison.GreaterThanBlock;
import io.je.rulebuilder.components.blocks.comparison.LessOrEqualBlock;
import io.je.rulebuilder.components.blocks.comparison.LessThanBlock;
import io.je.rulebuilder.components.blocks.event.AcceptEventBlock;
import io.je.rulebuilder.components.blocks.execution.LogBlock;
import io.je.rulebuilder.components.blocks.execution.TriggerEventBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.RuleBuilderErrors;
import io.je.utilities.exceptions.AddRuleBlockException;

public class BlockGenerator {

	
	public static Block createBlock(BlockModel blockModel) throws AddRuleBlockException
	{
		switch (blockModel.getOperationId()) {
		/*
		 * Arithmetic blocks
		 */

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
		// Factorial
		case 1005:
			//switch (blockModel.getOperationId()) {blockModel, 1005);
		// Square
		case 1006:
			//switch (blockModel.getOperationId()) {blockModel, 1006);
		// SquareRoot
		case 1007:
			return new SQRTBlock(blockModel);
		// Power
		case 1008:
			return new PowerBlock(blockModel);
		// change sign
		case 1009:
			//switch (blockModel.getOperationId()) {blockModel, 1009);
		case 1010:
			return new BiasBlock(blockModel);
		case 1011:
			return new GainBlock(blockModel);
		case 1012:
			//switch (blockModel.getOperationId()) {blockModel, 1012);
		case 1013:
			//switch (blockModel.getOperationId()) {blockModel, 1013);
		case 1014:
			//switch (blockModel.getOperationId()) {blockModel, 1014);
		case 1015:
			//switch (blockModel.getOperationId()) {blockModel, 1015);
		case 1016:
			//switch (blockModel.getOperationId()) {blockModel, 1016);
		case 1017:
			//switch (blockModel.getOperationId()) {blockModel, 1017);
		case 1018:
			//switch (blockModel.getOperationId()) {blockModel, 1018);
		case 1019:
			//switch (blockModel.getOperationId()) {blockModel, 1019);
		case 1020:
			//switch (blockModel.getOperationId()) {blockModel, 1020);
		case 1021:
			//switch (blockModel.getOperationId()) {blockModel, 1021);
		case 1022:
			//switch (blockModel.getOperationId()) {blockModel, 1022);
		case 1023:
			//switch (blockModel.getOperationId()) {blockModel, 1023);
		case 1024:
			//switch (blockModel.getOperationId()) {blockModel, 1024);
		case 1025:
			//switch (blockModel.getOperationId()) {blockModel, 1025);
		case 1026:
			break;
		case 1027:
			return new UnitConversionBlock(blockModel);

		/*
		 * Comparison blocks
		 */
		case 2001:
			return new EqualsBlock(blockModel);
		case 2002:
			break;
		case 2003:
			return new GreaterThanBlock(blockModel);
		case 2004:
			return new GreaterOrEqualBlock(blockModel);
		case 2005:
			return new LessThanBlock(blockModel);
		case 2006:
			return new LessOrEqualBlock(blockModel);
		case 2007:
			break;
		case 2008:
			break;
		case 2009:
			break;
		case 2010:
			break;
		case 2011:
			break;
		case 2012:
			break;
		case 2013:
			break;
		case 2014:
			break;
		case 2015:
			break;
		/*
		 * Logic blocks
		 */

		// And Block
		case 3001:
			return new LogicBlock(blockModel);
		// Or Block
		case 3002:
			return new LogicBlock(blockModel);
		// XOR Block
		case 3003:
			//return new XorBlock(blockModel);
		// Join Block
		case 3004:
			//return new JoinBlock(blockModel);
		// NOT Block
		case 3005:
			//return new NotBlock(blockModel);

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
		case 6001:
			return new AcceptEventBlock(blockModel);
		/*
		 * Execution blocks
		 */
		case 5001:
			return new LogBlock(blockModel);
		case 5002:
			return new TriggerEventBlock(blockModel);

		// no operation with such id
		default:
			throw new AddRuleBlockException( RuleBuilderErrors.BlockOperationIdUnknown);
		}
		return null;

	}
}

