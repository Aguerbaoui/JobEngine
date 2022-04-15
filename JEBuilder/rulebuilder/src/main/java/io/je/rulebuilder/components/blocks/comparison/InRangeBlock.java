package io.je.rulebuilder.components.blocks.comparison;

import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

/*
 * block inputs are : source , minRange , maxRange
 */
public class InRangeBlock extends ComparisonBlock {

	String minRange;


	public InRangeBlock(BlockModel blockModel) {
		super(blockModel);
		minRange=threshold;

	}
	
	@Override
	protected void checkBlockConfiguration() throws RuleBuildFailedException {
		if (minRange == null && maxRange == null && inputBlocks.size() != 3) {
			throw new RuleBuildFailedException(blockName + " is not configured properly");
		}
		
	}

	@Override
	protected void setParameters() {
		if (minRange == null && maxRange == null) {
			minRange = inputBlocks.get(1).getReference();
			maxRange = inputBlocks.get(2).getReference();
			return;
		} else if (maxRange == null && minRange != null) {
			maxRange = inputBlocks.get(2).getReference();
			return;
		} else if (minRange == null && maxRange!=null) {
			minRange = inputBlocks.get(1).getReference();
		} 

	}

	public InRangeBlock() {
		super();
	}

	@Override
	public String toString() {
		return "ExecutionBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	
	@Override
	protected String getOperationExpression() {
		String firstOperand =  inputBlocks.get(0).getReference();

		if (includeBounds) {
			return firstOperand + ">=" + minRange + " && " + firstOperand + "<=" + maxRange;
		}
			return firstOperand + ">" + minRange + " && " + firstOperand + "<" + maxRange;
		

	}


	

}
