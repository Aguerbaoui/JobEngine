package io.je.rulebuilder.components.blocks.execution;

import java.util.List;

import org.springframework.data.annotation.Transient;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

/*
 * Block used to writing in an instance's attribute (from DM) 
 * source : previous block
 * operation id : 5003
 */
public class LinkedSetterBlock extends ExecutionBlock {
	

	boolean isGeneric;
	@Transient
	String primeJoinId;
	/*******************************Instance definition*******************************/
	String classId;
	String classPath;
	String destinationAttributeName;
	String destinationAttributeType;

	List<String> instances ; 
	boolean  ignoreWriteIfSameValue=true;

	public LinkedSetterBlock(BlockModel blockModel) {
		super(blockModel);
		try {
			ignoreWriteIfSameValue=(boolean) blockModel.getBlockConfiguration().get("ignoreWriteIfSameValue");
		}catch (Exception e) {
			// TODO: handle exception
		}
		try
		{
			isGeneric= (boolean) blockModel.getBlockConfiguration().get("isGeneric");	
			classId=(String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID);
			classPath = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSNAME);
			destinationAttributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.ATTRIBUTENAME);
			instances = (List<String>) blockModel.getBlockConfiguration().get(AttributesMapping.SPECIFICINSTANCES);
			isProperlyConfigured=true;
			if(inputBlockIds.isEmpty())
			{
				isProperlyConfigured=false;

			}
		}catch(Exception e) {
			isProperlyConfigured=false;
		}finally {
			if(classId==null || classPath==null || destinationAttributeName==null || instances==null || instances.isEmpty())
			{
				isProperlyConfigured=false;

			}
		}
		


	}

	public LinkedSetterBlock() {
		super();
	}



	 
	@Override
	public String getExpression() {		
		StringBuilder expression = new StringBuilder();
		if(primeJoinId==null)
		{
			for(String instance : instances)
			{
				expression.append(  "Executioner.updateInstanceAttributeValueFromStaticValue( "
						 +"\"" + this.jobEngineProjectID  +"\","
						  +"\"" + this.ruleId  +"\","
						  +"\"" + this.blockName  +"\","				  					  
						  +"\"" + instance  +"\","
						  +"\"" + this.destinationAttributeName  +"\","
						  + inputBlocks.get(0).getReference() +","
						  + this.ignoreWriteIfSameValue 

						  
						  +");\r\n");
				expression.append("\n");
			}
		}else
		{
			expression.append(  "Executioner.updateInstanceAttributeValueFromStaticValue( "
					 +"\"" + this.jobEngineProjectID  +"\","
					  +"\"" + this.ruleId  +"\","
					  +"\"" + this.blockName  +"\","				  					  
					  + primeJoinId  +","
					  +"\"" + this.destinationAttributeName  +"\","
					  + inputBlocks.get(0).getReference()+","
					  + this.ignoreWriteIfSameValue 
					  +");\r\n");
			expression.append("\n");
		}
		
	   return expression.toString();

	}

	public boolean isGeneric() {
		return isGeneric;
	}

	public void setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}
	




}
