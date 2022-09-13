package io.je.rulebuilder.components.blocks.execution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import lombok.Getter;
import lombok.Setter;
import utils.log.LoggerUtils;

import java.util.HashMap;
import java.util.List;

import static io.je.utilities.constants.JEMessages.EXCEPTION_OCCURRED_WHILE_INITIALIZE;
import static io.je.utilities.constants.WorkflowConstants.*;

/**
 * Block used to send Email
 * operation id : 5009
 * source:input block
 */
@Getter
@Setter
public class SMSBlock extends ExecutionBlock {


    //Constants
    String executionerMethod = "Executioner.sendSMS(";
    private String serverType;
    private List<String> receiverPhoneNumbers;
    private String accountSID;
    private String accountToken;
    private String senderPhoneNumber;
    private String message;


    public SMSBlock(BlockModel blockModel) {
        super(blockModel);

        try {
            //FIXME: change string to constants
            serverType = (String) blockModel.getBlockConfiguration()
                    .get(SERVER_TYPE);
            accountToken = (String) blockModel.getBlockConfiguration()
                    .get(TWILIO_ACCOUNT_TOKEN);
            accountSID = (String) blockModel.getBlockConfiguration()
                    .get(TWILIO_ACCOUNT_SID);


            senderPhoneNumber = (String) blockModel.getBlockConfiguration()
                    .get(TWILIO_SENDER_PHONE_NUMBER);
            message = (String) blockModel.getBlockConfiguration()
                    .get(SMS_MESSAGE);
            receiverPhoneNumbers = (List<String>) blockModel.getBlockConfiguration()
                    .get(RECEIVER_PHONE_NUMBERS);

            if (serverType != null) {
                isProperlyConfigured = true;
                misConfigurationCause = "";
            } else {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.SMSBLOCK_SERVER_TYPE_NULL;
            }

        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = JEMessages.SMSBLOCK + EXCEPTION_OCCURRED_WHILE_INITIALIZE + e.getMessage();
            JELogger.logException(e);
        }

    }

    public SMSBlock() {
        super();
    }

    //TODO: make it work for more than provider, currently only TWILIO is supported
    @Override
    public String getExpression() {
        StringBuilder expression = new StringBuilder();
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(SERVER_TYPE, getServerType());
        attributes.put(TWILIO_ACCOUNT_SID, getAccountSID());
        attributes.put(TWILIO_ACCOUNT_TOKEN, getAccountToken());
        attributes.put(RECEIVER_PHONE_NUMBERS, getReceiverPhoneNumbers());
        attributes.put(TWILIO_SENDER_PHONE_NUMBER, getSenderPhoneNumber());

        String json = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer()
                    .withDefaultPrettyPrinter();
            json = ow.writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
            LoggerUtils.logException(e);
            throw new RuntimeException(e);
        }
        expression.append("Executioner.sendSMS( " + "\"")
                .append(this.jobEngineProjectID)
                .append("\",")
                .append("\"")
                .append(this.ruleId)
                .append("\",")
                .append("\"")
                .append(this.blockName)
                .append("\",")
                .append(json)
                .append(",")
                .append("\"")
                .append(formatMessage(getMessage()))
                .append("\");\r\n");
        expression.append("\n");

        return expression.toString();

    }


}
