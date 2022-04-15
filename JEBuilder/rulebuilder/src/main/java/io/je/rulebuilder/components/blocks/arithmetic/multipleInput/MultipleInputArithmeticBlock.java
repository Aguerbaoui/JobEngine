package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import java.util.ArrayList;

import org.springframework.data.annotation.Transient;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.blocks.getter.InstanceGetterBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class MultipleInputArithmeticBlock extends ArithmeticBlock {

	
	@Transient
	int counter = 0;
	
	public MultipleInputArithmeticBlock(BlockModel blockModel) {
		super(blockModel);
	}

	
	public MultipleInputArithmeticBlock()
	{
		
	}
	
	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = generateAllPreviousBlocksExpressions();
		expression.append(generateBlockExpression(false));
		return expression.toString();
	}

	@Override
	public String getAsOperandExpression() throws RuleBuildFailedException {
		StringBuilder expression = generateAllPreviousBlocksExpressions();
		expression.append(generateBlockExpression(true));

		return expression.toString();
	}

	

	private StringBuilder generateAllPreviousBlocksExpressions() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		for (int i = 0; i < inputBlocks.size(); i++) {
			expression.append(inputBlocks.get(i).getExpression());
			expression.append("\n");

		}
		return expression;
	}
	
	private String generateBlockExpression(boolean comparable)
	{
		String comparableExpression = " : Number() from ";
		if(comparable)
		{
			comparableExpression = " : Number("+ Keywords.toBeReplaced + ") from ";
		}
		StringBuilder expression = new StringBuilder();
		
		expression.append(getBlockNameAsVariable() + comparableExpression);
		expression.append(getArithmeticFormula(0,"number") + asDouble(getInputBlockReferenceName(0)));
		for (int i = 1; i < inputBlocks.size(); i++) {
			expression.append(" , " + asDouble(getInputBlockReferenceName(i)));
		}
		expression.append(")");
		if(stopExecutionIfInvalidInput)
		{
			expression.append("\n"+evaluateExecution(asDouble(getInputBlockReferenceName(1))));
		}
		counter = 0;
		return expression.toString();
		
		
	}
	
	private String getInputBlockReferenceName(int index)
	{
		try {
			if(index >= inputBlocks.size()) return "";
			if(inputBlocks.get(index) instanceof InstanceGetterBlock)
			{
				String attName = new ArrayList<String>(getCustomInputs().keySet()).get(counter);
				counter++;
				return inputBlocks.get(index).getRefName(attName);
			}else 
			{
				return inputBlocks.get(index).getRefName(null) ;
			}
		}catch(Exception e){
			return "";
		}
	}
	

}
