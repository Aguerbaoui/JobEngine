package io.je.rulebuilder.components.blocks.getter;

import io.je.rulebuilder.components.blocks.GetterBlock;
import io.je.rulebuilder.models.BlockModel;

public class AttributeGetterBlock extends GetterBlock{

	String classPath;
	String attributeName;
	//specific instances
	
	public AttributeGetterBlock(BlockModel blockModel) {
		super(blockModel);
		classPath= blockModel.getBlockConfiguration().getClassId();
		attributeName = blockModel.getBlockConfiguration().getAttributeName();
	}

	@Override
	public String getExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "AttributeGetterBlock [classPath=" + classPath + ", attributeName=" + attributeName + ", ruleId="
				+ ruleId + ", inputBlocks=" + inputBlocks + ", outputBlocks=" + outputBlocks + ", jobEngineElementID="
				+ jobEngineElementID + ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate="
				+ jeObjectLastUpdate + "]";
	}




}
