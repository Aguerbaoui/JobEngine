package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class SingleInputArithmeticBlock extends ArithmeticBlock {
	
	protected String defaultType = "number";
	
	public SingleInputArithmeticBlock(BlockModel blockModel) {
		super(blockModel);

	}
	

	protected SingleInputArithmeticBlock() {
	}

	
	protected abstract String getFormula();

	
	@Override
	protected String getArithmeticFormula(int level,String type) {
		if(type.equalsIgnoreCase("number"))
		{
			switch(level)
			{
			case 0:
				return " Number() from " +  getFormula() ;
			case 1:
				return " Number(" + Keywords.toBeReplaced +") from " + getFormula() ;
			default: 
				return " Number() from " + getFormula() ;
			
			}
		}else if(type.equalsIgnoreCase("string") )
		{
			switch(level)
			{
			case 0:
				return " String() from " +  getFormula() ;
			case 1:
				return " String(" + Keywords.toBeReplaced +") from " + getFormula() ;
			default: 
				return " String() from " + getFormula() ;
			
			}
		}else if(type.equalsIgnoreCase("date") )
		{
			return "Date() from " + getFormula();
		}
		
		 return " Number() from " + getFormula() ;
	}


	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (0,defaultType));
		return expression.toString();
	}

	@Override
	public String getAsOperandExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
	
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (1,defaultType));
		return expression.toString();
	}

	@Override
	public String getJoinExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinExpression());
		expression.append("\n");
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (0,defaultType));
		return expression.toString();
	}



	@Override
	public String getJoinedExpression(String joinId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinedExpression(joinId));
		expression.append("\n");
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (0,defaultType));
		return expression.toString();
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joinId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinedExpression(joinId));
		expression.append("\n");
	
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (1,defaultType));
		return expression.toString();
	}

	@Override
	public String getJoinExpressionAsFirstOperand() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinExpression());
		expression.append("\n");
	
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (1,defaultType));
		return expression.toString();
	}


	public String getDefaultType() {
		return defaultType;
	}


	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}
	
}
