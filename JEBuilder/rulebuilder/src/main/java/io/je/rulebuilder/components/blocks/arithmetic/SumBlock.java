package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.models.BlockModel;

/*
 * Sum Block
 */
public class SumBlock extends ArithmeticBlock {

	public SumBlock(BlockModel blockModel) {
		super(blockModel);
	}

	/*
	 * returns a simple drools expression that expresses this block. 
	 */
	public String getExpression() {
		StringBuilder simpleExpression = new StringBuilder();
		simpleExpression.append("$"+this.jobEngineElementID + ": Number() from ");
		
		//TODO: throw exception if number of inputs < 2
		int numberOfInputs = this.inputBlocks.size();
		for(int i = 0; i<numberOfInputs-1;i++)
		{
			simpleExpression.append("$"+ inputBlocks.get(i) + "+");
		}
		simpleExpression.append("$"+ (numberOfInputs-1) );

		return simpleExpression.toString();
	}

	
	/*
	 * returns a comparison drools expression
	 */
	public String getComparableExpression(String constraint) {
		StringBuilder simpleExpression = new StringBuilder();
		simpleExpression.append("$"+this.jobEngineElementID + ": Number( Double "+constraint+" ) from ");
		
		//TODO: throw exception if number of inputs < 2
		int numberOfInputs = this.inputBlocks.size();
		for(int i = 0; i<numberOfInputs-1;i++)
		{
			simpleExpression.append("$"+ inputBlocks.get(i) + "+");
		}
		simpleExpression.append("$"+ (numberOfInputs-1) );

		return simpleExpression.toString();
	}

	@Override
	public String getExpression(String Expression) {
		// TODO Auto-generated method stub
		return null;
	}






}
