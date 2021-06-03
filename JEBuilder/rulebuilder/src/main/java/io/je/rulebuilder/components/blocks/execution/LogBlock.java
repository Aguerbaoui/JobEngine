package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;

public class LogBlock extends ExecutionBlock {
	
	String logMessage = "log output";

	public LogBlock(BlockModel blockModel) {
		super(blockModel);
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			logMessage = blockModel.getBlockConfiguration().getValue();
		}
		
		isProperlyConfigured = logMessage!=null && !logMessage.isEmpty();
	}
	
	 public LogBlock() {
		 super();
	}

	@Override
	public String getExpression() {
		return "JEMessage message = new JEMessage();\r\n"
				+ "JEBlockMessage blockMsg = new JEBlockMessage(\""+blockName+"\",\""+logMessage+"\");\r\n"
				+ "message.addBlockMessage(blockMsg);\r\n"
				+ "message.setType(\"BlockMessage\");\r\n"
				+ "message.setExecutionTime(LocalDateTime.now().toString());\r\n"
				+ "Executioner.informRuleBlock(\"" +jobEngineProjectID +"\",\"" + ruleId +"\", message );";
	}



	//TODO: to be deleted ! temporary function for testing 
	/*public String formatMessage()
	{
		String msg = logMessage;
		Pattern pattern = Pattern.compile("\\$\\w+");

		Matcher matcher = pattern.matcher(msg);
		ArrayList<String> wordsToBeReplaced = new ArrayList<String>();
		while (matcher.find())
		{
			wordsToBeReplaced.add(matcher.group());
		}
		for(String word : wordsToBeReplaced)
		{
			msg=msg.replace(word, "\"+"+word+"+\"");
		}
		return msg;
	}

*/

}
