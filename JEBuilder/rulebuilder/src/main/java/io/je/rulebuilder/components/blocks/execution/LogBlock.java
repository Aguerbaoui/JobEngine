package io.je.rulebuilder.components.blocks.execution;


import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.JEMessages;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogBlock extends ExecutionBlock {

    String logMessage = "log output";

    public LogBlock(BlockModel blockModel) {
        super(blockModel);
        if (blockModel.getBlockConfiguration() != null && blockModel.getBlockConfiguration()
                .get(AttributesMapping.VALUE) != null) {
            this.logMessage = (String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.VALUE);
        }

        if (this.logMessage == null) {
            this.isProperlyConfigured = false;
            this.misConfigurationCause = JEMessages.INFORM_BLOCK_LOG_MESSAGE_NULL;
        } else if (this.logMessage.isEmpty()) {
            this.isProperlyConfigured = false;
            this.misConfigurationCause = JEMessages.INFORM_BLOCK_LOG_MESSAGE_EMPTY;
        } else {
            this.isProperlyConfigured = true;
            this.misConfigurationCause = "";
        }

    }

    public LogBlock() {
        super();
    }

    @Override
    public String getExpression() {

        //return "JELogger.info(\""+logMessage+"\");";
        return /*"JEMessage message = new JEMessage();\r\n"
				+ "JEBlockMessage blockMsg = new JEBlockMessage(\""+blockName+"\",\""+logMessage+"\");\r\n"
				+ "message.addBlockMessage(blockMsg);\r\n"
				+ "message.setType(\"BlockMessage\");\r\n"
				+ "message.setExecutionTime(LocalDateTime.now().toString());\r\n"
				+ */"Executioner.informRuleBlock(\"" + this.jobEngineProjectID + "\",\"" + this.ruleId + "\",\" " + formatMessage() + "\",Instant.now().toString(),\"" + this.blockName + " \" );";
    }


    //TODO: to be deleted ! temporary function for testing
    //exp $Getter35.getTemp()
    public String formatMessage() {
        String msg = logMessage;
        Pattern pattern = Pattern.compile("\\$(\\w|\\.|\\(|\\))+");

        Matcher matcher = pattern.matcher(msg);
        ArrayList<String> wordsToBeReplaced = new ArrayList<String>();
        while (matcher.find()) {
            wordsToBeReplaced.add(matcher.group());
        }
        for (String word : wordsToBeReplaced) {
            String tword = word.replace("$", "");
            msg = msg.replace(word, "\"+" + tword + "+\"");
        }
        return msg;
    }


}
