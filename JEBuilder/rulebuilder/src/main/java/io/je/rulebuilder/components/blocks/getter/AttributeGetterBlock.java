package io.je.rulebuilder.components.blocks.getter;

import java.util.List;

import io.je.rulebuilder.components.blocks.GetterBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class AttributeGetterBlock extends GetterBlock {

	String classId;
	String classPath;
	String attributeName;
	List<String> specificInstances ; 

	public AttributeGetterBlock(BlockModel blockModel) {
		super(blockModel);
		try
		{
			classId=blockModel.getBlockConfiguration().getClassId();
			classPath = blockModel.getBlockConfiguration().getClassName();
			attributeName = blockModel.getBlockConfiguration().getAttributeName();
			specificInstances = blockModel.getBlockConfiguration().getSpecificInstances();
			isProperlyConfigured=true;
		}catch(Exception e) {
			isProperlyConfigured=false;
		}finally {
			if(classId==null || classPath==null || attributeName==null)
			{
				isProperlyConfigured=false;

			}
		}
		


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

	/*
	 * returns the instances in the following format : instance1,instance2...,instancen
	 */
	private String getInstances()
	{
		String instanceIds = "";
		instanceIds += "\""+ specificInstances.get(0)+ "\"";
		for(int i = 1  ; i<specificInstances.size(); i++)
		{
			instanceIds += " , " +"\""+specificInstances.get(i)+"\"";
		}
		return instanceIds;
	}

	@Override
	// returns variable name holding the join attribute example $myId
	//default value is now set to Id
	public String getJoinId() {
		
		return getBlockNameAsVariable() +"jobEngineElementID";
	}
	
	public String getAttributeVariableName()
	{
 		return getBlockNameAsVariable() + attributeName.replace(".", "");
	}
	
	/*
	 * returns drl expression 
	 * example : $blockname : Person(id==2, $age:age)
	 */
	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		if(!inputBlocks.isEmpty())
		{
			expression.append(inputBlocks.get(0).getExpression());
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable() + " : " +classPath  );
		expression.append(  " ( " );
		if(specificInstances != null && !specificInstances.isEmpty())
		{
			expression.append("jobEngineElementID in ( " + getInstances() + ")");
			expression.append(  " , " );

		}
		expression.append(getAttributeVariableName() + " : "+ getattributeGetterExpression() );
		expression.append(  " ) " );
		return expression.toString();
	}

	/*
	 * return example $blockName: Person( $age Keywords.toBeReplaced )
	 */
	@Override
	public String getAsOperandExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		
		//input blocks can be an event block
		if(!inputBlocks.isEmpty())
		{
			expression.append(inputBlocks.get(0).getExpression());
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable() + " : " +classPath  );
		expression.append(  " ( " );
		if(specificInstances != null && !specificInstances.isEmpty())
		{
			expression.append("jobEngineElementID in ( " + getInstances() + ")");
			expression.append(  " , " );

		}
		expression.append(getAttributeVariableName() + " : "+ getattributeGetterExpression() );
		expression.append(  " , " );

		expression.append(  Keywords.toBeReplaced ); //tbrp
		expression.append(  " ) " );

		return expression.toString();
	}

	@Override
	public String getJoinExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		//add input blocks
		if(!inputBlocks.isEmpty())
		{
			expression.append(inputBlocks.get(0).getExpression());
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable() + " : " +classPath  );
		expression.append(  " ( " );
		
		
		expression.append(getJoinId() + " : jobEngineElementID ,");		
		if(specificInstances != null && !specificInstances.isEmpty())
		{
			expression.append("jobEngineElementID in ( " + getInstances() + ")");
			expression.append(  " , " );

		}
		
		expression.append(getAttributeVariableName() + " : "+ getattributeGetterExpression() );		
		expression.append(  " ) " );

		return expression.toString();
	}

	@Override
	public String getJoinExpressionAsFirstOperand() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		//add input blocks
		if(!inputBlocks.isEmpty())
		{
			expression.append(inputBlocks.get(0).getExpression());
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable() + " : " +classPath  );
		expression.append(  " ( " );
		
		
		expression.append(getJoinId() + " : jobEngineElementID ,");		
		if(specificInstances != null && !specificInstances.isEmpty())
		{
			expression.append("jobEngineElementID in ( " + getInstances() + ")");
			expression.append(  " , " );

		}
		expression.append(getAttributeVariableName() + " : "+ getattributeGetterExpression() );
		expression.append(  " , " );
		expression.append(  Keywords.toBeReplaced ); //tbrp
		expression.append(  " ) " );

		return expression.toString();
	}

	
	@Override
	public String getJoinedExpression(String joindId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		if(!inputBlocks.isEmpty())
		{
			expression.append(inputBlocks.get(0).getExpression());
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable() + " : " +classPath  );
		expression.append(  " ( " );
		if(specificInstances != null && !specificInstances.isEmpty())
		{
			expression.append("jobEngineElementID in ( " + getInstances() + ")");
			expression.append(  " , " );

		}
		else
		{
			expression.append("jobEngineElementID == " + joindId);
			expression.append(  " , " );


		}
		expression.append(getAttributeVariableName() + " : "+ getattributeGetterExpression() );
		expression.append(  " ) " );
		return expression.toString();
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joindId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		if(!inputBlocks.isEmpty())
		{
			expression.append(inputBlocks.get(0).getExpression());
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable() + " : " +classPath  );
		expression.append(  " ( " );
		if(specificInstances != null && !specificInstances.isEmpty())
		{
			expression.append("jobEngineElementID in ( " + getInstances() + ")");
			expression.append(  " , " );

		}
		else
		{
			expression.append("jobEngineElementID == " + joindId);
			expression.append(  " , " );


		}
		expression.append(getAttributeVariableName() + " : "+ getattributeGetterExpression() );
		expression.append(  " , " );
		expression.append(  Keywords.toBeReplaced ); //tbrp
		expression.append(  " ) " );
		return expression.toString();
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


	//TODO: remove this. All attribute names will starts with lowercase
	private String getattributeGetterExpression() {
		return attributeName;
		/*String s = "";
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
		return s;*/
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}




}
