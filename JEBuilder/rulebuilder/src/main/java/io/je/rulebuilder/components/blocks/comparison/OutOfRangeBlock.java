package io.je.rulebuilder.components.blocks.comparison;

import io.je.rulebuilder.components.blocks.getter.InstanceGetterBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

/*
 * block inputs are : source , minRange , maxRange
 */
public class OutOfRangeBlock extends ComparisonBlock {

	String minRange;


	public OutOfRangeBlock(BlockModel blockModel) {
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
			minRange = getInputRefName(1,getInputByName(1));
			maxRange = getInputRefName(2,getInputByName(2));
			return;
		} else if (maxRange == null && minRange != null) {
			maxRange = getInputRefName(1,getInputByName(2));
			return;
		} else if (minRange == null && maxRange!=null) {
			minRange = getInputRefName(1,getInputByName(1));
		} 

	}

	public OutOfRangeBlock() {
		super();
	}

	@Override
	public String toString() {
		return "ExecutionBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	
	@Override
	protected String getOperationExpression() {

		String firstOperand = (inputBlocks.get(0) instanceof InstanceGetterBlock) ? inputBlocks.get(0).getRefName(getInputByName(0))
				: "doubleValue ";

		if (includeBounds) {
			return "("+firstOperand + "<=" + minRange + "||" + firstOperand + ">=" + maxRange+")";
		} 
			return "("+firstOperand + "<" + minRange + "||" + firstOperand + ">" + maxRange+")";
		

	}


	

}
