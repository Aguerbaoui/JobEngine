package io.je.rulebuilder.components.blocks.execution;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	}
	
	 public LogBlock() {
		 super();
	}

	@Override
	public String getExpression() {
		return "JELogger.info(" +"\" "+formatMessage()+"\");";
	}

	//TODO: to be deleted ! temporary function for testing 
	public String formatMessage()
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



}
