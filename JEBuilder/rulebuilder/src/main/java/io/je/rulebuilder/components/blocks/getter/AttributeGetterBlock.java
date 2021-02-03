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
		if(!inputBlocks.isEmpty())
		{
			expression.append("\n");
			expression.append(inputBlocks.get(0).getExpression());
			expression.append("\n");

		}
		expression.append("$" + blockName.replaceAll("\\s+", "") + " : " + classPath + " ( $" + attributeName.replaceAll(".", "") + " : "
				+ getFinalAttributeName() + " )"); // TODO: nested attributes
		return expression.toString();
	}

	@Override
	public String getAsFirstOperandExpression() {
		StringBuilder expression = new StringBuilder();
		if(!inputBlocks.isEmpty())
		{
			expression.append("\n");
			expression.append(inputBlocks.get(0).getExpression());
			expression.append("\n");

		}
		expression.append("$" + blockName.replaceAll("\\s+", "") + " : " + classPath + " ( " + getFinalAttributeName() + " "
				+ Keywords.toBeReplaced + " )"); // TODO: nested attributes
		return expression.toString();
	}

	@Override
	public String getAsSecondOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("$" + blockName.replaceAll("\\s+", "") + " : " + classPath + " ( " + Keywords.toBeReplaced
				+ getFinalAttributeName() + " )"); // TODO: nested attributes
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



	private String getFinalAttributeName() {
		String s = "";
		String str = attributeName;
		String[] a = str.split("\\.", 5);

		for (int i = 0; i < a.length - 1; i++) {
			a[i] = a[i].substring(0, 1).toUpperCase() + a[i].substring(1);
			a[i] = "get" + a[i] + "()";
			s = s + a[i] + ".";
		}
		a[a.length - 1] = a[a.length - 1].substring(0, 1).toUpperCase() + a[a.length - 1].substring(1);
		a[a.length - 1] = "get" + a[a.length - 1] + "()";
		s = s + a[a.length - 1];
		return s;
	}

}
