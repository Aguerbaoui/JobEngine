package io.je.rulebuilder.components.blocks.getter;


import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

/*
 * operation Id 4005
 */
public  class VariableGetterBlock extends ConditionBlock {
	
	String variableId = null;


	public VariableGetterBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
				blockModel.getDescription());
		try {
			variableId = (String) blockModel.getBlockConfiguration().get("variableId");
			isProperlyConfigured = true;
			if(variableId==null)
			{
				isProperlyConfigured=false;
			}


		}catch (Exception e) {
			isProperlyConfigured = false;
		}
		

	}

	public VariableGetterBlock() {
		super();
	}

	@Override
	public String toString() {
		return "ExecutionBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	
	@Override
	public String getAsOperandExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinedExpression(String joindId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joindId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpression() {
		return blockName.replaceAll("\\s+", "")+" : JEVariable ( jobEngineElementID == \""+variableId +"\","+  getAttributeVariableName() + " : value )";
	}
	



	@Override
	public String getJoinExpressionAsFirstOperand() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAttributeVariableName() {
		return blockName.replaceAll("\\s+", "")+"Value";
	}

}
