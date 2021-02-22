package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.components.blocks.arithmetic.multipleInput.*;
import io.je.rulebuilder.components.blocks.arithmetic.singleinput.*;
import io.je.rulebuilder.components.blocks.event.AcceptEventBlock;
import io.je.rulebuilder.components.blocks.execution.LogBlock;
import io.je.rulebuilder.components.blocks.execution.TriggerEventBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.components.blocks.logic.JoinBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.RuleBuilderErrors;
import io.je.utilities.exceptions.AddRuleBlockException;

public class BlockGenerator {

	
	public static Block createBlock(BlockModel blockModel) throws AddRuleBlockException
	{
		/*
		 * Comparison blocks
		 */
		if(blockModel.getOperationId()>=2001 && blockModel.getOperationId() <= 2015)
		{
			return new ComparisonBlock(blockModel);
		}
		
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
			return new FactorialBlock(blockModel);
		// Square
		case 1006:
			return new SquareBlock(blockModel);
		// SquareRoot
		case 1007:
			return new SQRTBlock(blockModel);
		// Power
		case 1008:
			return new PowerBlock(blockModel);
		// change sign
		case 1009:
			return new ChangeSignBlock(blockModel);
		case 1010:
			return new BiasBlock(blockModel);
		case 1011:
			return new GainBlock(blockModel);
		case 1012:
			return new MultiplicativeInverseBlock(blockModel);
		case 1013:
			return new AbsBlock(blockModel);
		case 1014:
			return new ExpBlock(blockModel);
		case 1015:
			return new Log10Block(blockModel);
		case 1016:
			return new TanBlock(blockModel);
		case 1017:
			return new ArctanBlock(blockModel);
		case 1018:
			return new ArccosBlock(blockModel);
		case 1019:
			return new ArcsinBlock(blockModel);
		case 1020:
			return new FloorBlock(blockModel);
		case 1021:
			return new TruncateBlock(blockModel);
		case 1022:
			return new CeilingBlock(blockModel);
		case 1023:
			return new SinBlock(blockModel);
		case 1024:
			return new CosBlock(blockModel);
		case 1025:
			return new LnBlock(blockModel);
		case 1026:
			return new LengthBlock(blockModel);
		case 1027:
			return new UnitConversionBlock(blockModel);

	

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
			return new JoinBlock(blockModel);
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
		
		/*
		 * Execution blocks
		 */
		case 5001:
			return new LogBlock(blockModel);
		case 5002:
			return new TriggerEventBlock(blockModel);
		case 6001:
			return new AcceptEventBlock(blockModel);
		// no operation with such id
		default:
			throw new AddRuleBlockException( RuleBuilderErrors.BlockOperationIdUnknown);
		}
		return null;

	}
}

