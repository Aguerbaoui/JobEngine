package io.je.rulebuilder.components.blocks.getter;

import java.util.ArrayList;
import java.util.List;

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
	public String getExpression()
	{
		
		return classPath+"($"+attributeName +":"+ attributeName+ ")";
	}
	
	public String getExpression(String varName)
	{
		
		return classPath+"($"+varName +":"+ attributeName+ ")";
	}

	
	@Override
	public String getComparableExpression(String constraint)
	{
		
		return classPath+"(" +attributeName + constraint +" )";
	}
	

	public String getArithmeticComparableExpression(String constraint, String arithmeticExpression)
	{
		
		return classPath+"($"+attributeName +":"+ attributeName+ ")";
	}
	
	
	
	@Override
	public String toString() {
		return "AttributeGetterBlock [classPath=" + classPath + ", attributeName=" + attributeName + ", ruleId="
				+ ruleId + ", inputBlocks=" + inputBlocks + ", outputBlocks=" + outputBlocks + ", jobEngineElementID="
				+ jobEngineElementID + ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate="
				+ jeObjectLastUpdate + "]";
	}




}
