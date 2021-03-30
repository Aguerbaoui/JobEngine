package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;

public class SetterBlock extends ExecutionBlock {
	
	String type = null;
	String attributeNewValue;
	String classId;
	String classPath;
	String attributeName;
	String instanceId ; 
	
	public SetterBlock(BlockModel blockModel) {
		super(blockModel);
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			attributeNewValue = blockModel.getBlockConfiguration().getValue();
			type = blockModel.getBlockConfiguration().getType();
			classId=blockModel.getBlockConfiguration().getClassId();
			classPath = blockModel.getBlockConfiguration().getClassName();
			attributeName = blockModel.getBlockConfiguration().getAttributeName();
			instanceId = blockModel.getBlockConfiguration().getInstanceId();

		}

	}
	
	 public SetterBlock() {
		 super();
	}

	 private String getRequest(String instanceIdentifier )
	 {
			 String req = "{\r\n"
				 		+ "   \"InstanceId\":\"" +instanceIdentifier+"\",\r\n"
				 		+ "   \"Attributes\":[\r\n"
				 		+ "      {\r\n"
				 		+ "         \"Name\":\""+attributeName+"\",\r\n"
				 		+ "         \"Value\":\""+attributeNewValue+"\"\r\n"
				 		+ "      }\r\n"
				 		+ "   ]\r\n"
				 		+ "}";
			return req;
		 }

	 
	 
	@Override
	public String getExpression() {
		if(type.equalsIgnoreCase("instance"))
		{
			return "Executioner.writeToDataModel(\"" + getRequest(instanceId) + "\");";

		}
		
		if(type.equalsIgnoreCase("block"))
		{
			String instanceIdentifier = "$" + blockName.replaceAll("\\s+", "") + ".getJobEngineElementID()" ; 
			return "Executioner.writeToDataModel(\"" + getRequest(instanceIdentifier) + "\");";

		}
		
		//TODO: throw exception
		return null;
	}



}
