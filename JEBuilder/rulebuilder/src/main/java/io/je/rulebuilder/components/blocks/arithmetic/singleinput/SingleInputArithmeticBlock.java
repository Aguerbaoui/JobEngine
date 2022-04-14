package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class SingleInputArithmeticBlock extends ArithmeticBlock {
	
	protected String defaultType = "number";
	
	public SingleInputArithmeticBlock(BlockModel blockModel) {
		super(blockModel);
		if(inputBlockIds.isEmpty())
		{
			isProperlyConfigured=false;

		}
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
				return " Double() from " +  getFormula() ;
			case 1:
				return " Double(" + Keywords.toBeReplaced +") from " + getFormula() ;
			default: 
				return " Double() from " + getFormula() ;
			
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
		
		 return " Double() from " + getFormula() ;
	}


	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		int x = includesOperation? 1:0;
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (x,defaultType));
		if(stopExecutionIfInvalidInput)
		{
			expression.append("\n"+evaluateExecution(asDouble(getInputRefName(0))));
		}
		return expression.toString();
	}

	
	@Override
	public String getAsOperandExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
	
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (1,defaultType));
		if(stopExecutionIfInvalidInput)
		{
			expression.append("\n"+evaluateExecution(asDouble(getInputRefName(0,getInputByName(0)))));
		}
		return expression.toString();
	}


	

	public String getDefaultType() {
		return defaultType;
	}


	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}
	
}
