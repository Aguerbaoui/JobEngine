package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.EventException;

public class SetterBlock extends ExecutionBlock {
	
	String instanceId = null;
	String attributeName = null;
	String attributeNewValue;
	public SetterBlock(BlockModel blockModel) {
		super(blockModel);
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			attributeName = blockModel.getBlockConfiguration().getAttributeName();
			attributeNewValue = blockModel.getBlockConfiguration().getValue();
			instanceId = "2517bfa2-bb81-67fa-894b-1c129dbd2f4f";

		}

	}
	
	 public SetterBlock() {
		 super();
	}

	@Override
	public String getExpression() {
		 String req = "{\r\n"
			 		+ "   \"InstanceId\":\"" +instanceId+"\",\r\n"
			 		+ "   \"Attributes\":[\r\n"
			 		+ "      {\r\n"
			 		+ "         \"Name\":\""+attributeName+"\",\r\n"
			 		+ "         \"Value\":\""+attributeNewValue+"\"\r\n"
			 		+ "      }\r\n"
			 		+ "   ]\r\n"
			 		+ "}";
		return "Executioner.writeToDataModel(\"" + req + "\");";
	}



}
