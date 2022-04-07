package io.je.rulebuilder.components.blocks.logic;


import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public  class OrBlock extends LogicBlock {

	
	public OrBlock(BlockModel blockModel) {
		super(blockModel);	
		operator = "or";
	}

	 public OrBlock() {
		
	}

	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		

		for(int i=0; i<outputBlocks.size();i++)
		{
			expression.append(outputBlocks.get(i).getExpression());
			expression.append("\n");
		}	
		return expression.toString();
	}


	@Override
	public String getAsOperandExpression() {
		// not applicable for these blocks
		return null;
	}



	











}
