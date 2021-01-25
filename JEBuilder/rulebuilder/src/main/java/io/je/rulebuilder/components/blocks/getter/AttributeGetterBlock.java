package io.je.rulebuilder.components.blocks.getter;

import io.je.rulebuilder.components.blocks.GetterBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class AttributeGetterBlock extends GetterBlock {

	String classPath;
	String attributeName;
	// specific instances

	public AttributeGetterBlock(BlockModel blockModel) {
		super(blockModel);
		classPath = blockModel.getBlockConfiguration().getClassName();
		attributeName = blockModel.getBlockConfiguration().getAttributeName();
	}

	public AttributeGetterBlock() {
		super();
	}

	
	@Override
	public String toString() {
		return "AttributeGetterBlock [classPath=" + classPath + ", attributeName=" + attributeName + ", ruleId="
				+ ruleId + ", blockName=" + blockName + ", blockDescription=" + blockDescription
				+ ", jobEngineElementID=" + jobEngineElementID + ", jobEngineProjectID=" + jobEngineProjectID
				+ ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}


	@Override
	public String getExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("$" +blockName.replaceAll("\\s+","")+ " : "+   classPath + " ( $" +attributeName+ " : " + attributeName + " )" ); //TODO: nested attributes
		return expression.toString();
	}

	@Override
	public String getAsFirstOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append(  "$" +blockName.replaceAll("\\s+","")+ " : "+classPath + " ( " + attributeName +" " + Keywords.toBeReplaced + " )" ); //TODO: nested attributes
		return expression.toString();
	}

	@Override
	public String getAsSecondOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("$" +blockName.replaceAll("\\s+","")+ " : "+ classPath + " ( "  + Keywords.toBeReplaced + attributeName + " )" ); //TODO: nested attributes
		return expression.toString();
	}

	@Override
	public String getJoinedExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	
	
}
