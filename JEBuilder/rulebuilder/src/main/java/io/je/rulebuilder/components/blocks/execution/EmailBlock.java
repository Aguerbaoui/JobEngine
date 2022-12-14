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
public class EmailBlock extends ExecutionBlock {

    //Constants
    String executionerMethod = "Executioner.sendEmail(";
    private boolean bEnableSSL = false;
    private String strSenderAddress;
    private List<String> lstRecieverAddress;
    private List<String> lstAttachementPaths;
    private List<String> lstUploadedFiles;
    private List<String> lstCCs;
    private List<String> lstBCCs;
    private String strSMTPServer;
    private int iPort;
    private int iSendTimeOut;
    private HashMap<String, String> emailMessage;
    private String strUserName;
    private String strPassword;


    public EmailBlock(BlockModel blockModel) {
        super(blockModel);


        try {
            //FIXME: change string to constants
            bEnableSSL = (boolean) blockModel.getBlockConfiguration()
                    .get("bEnableSSL");
            strSenderAddress = (String) blockModel.getBlockConfiguration()
                    .get("strSenderAddress");
            lstRecieverAddress = (List<String>) blockModel.getBlockConfiguration()
                    .get("lstRecieverAddress");


            lstAttachementPaths = (List<String>) blockModel.getBlockConfiguration()
                    .get("lstAttachementPaths");
            lstCCs = (List<String>) blockModel.getBlockConfiguration()
                    .get("lstCCs");
            lstBCCs = (List<String>) blockModel.getBlockConfiguration()
                    .get("lstBCCs");
            strSMTPServer = (String) blockModel.getBlockConfiguration()
                    .get("strSMTPServer");
            iPort = (int) blockModel.getBlockConfiguration()
                    .get("iPort");
            iSendTimeOut = (int) blockModel.getBlockConfiguration()
                    .get("iSendTimeOut");
            emailMessage = (HashMap<String, String>) blockModel.getBlockConfiguration()
                    .get("emailMessage");
            strUserName = (String) blockModel.getBlockConfiguration()
                    .get("strUserName");
            strPassword = (String) blockModel.getBlockConfiguration()
                    .get("strPassword");
            isProperlyConfigured = true;
            misConfigurationCause = "";

        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = JEMessages.EMAIL_BLOCK + EXCEPTION_OCCURRED_WHILE_INITIALIZE + e.getMessage();
            JELogger.logException(e);
        }


    }

    public EmailBlock() {
        super();
    }


    @Override
    public String getExpression() {
        StringBuilder expression = new StringBuilder();
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(USERNAME, getStrUserName());
        attributes.put(PASSWORD, getStrPassword());
        attributes.put(PORT, getIPort());
        attributes.put(SENDER_ADDRESS, getStrSenderAddress());
        attributes.put(SEND_TIME_OUT, getISendTimeOut());
        attributes.put(RECEIVER_ADDRESS, getLstRecieverAddress());
        var subject = formatMessage(getEmailMessage().get("strSubject"));
        var body = formatMessage(getEmailMessage().get("strBody"));
        var emailBody = new HashMap<String, String>();
        emailBody.put("strSubject", subject);
        emailBody.put("strBody", body);
        attributes.put(EMAIL_MESSAGE, emailBody);
        attributes.put(SMTP_SERVER, getStrSMTPServer());
        attributes.put(CC_LIST, getLstCCs());
        attributes.put(BCC_LIST, getLstBCCs());
        attributes.put(ATTACHEMENT_URLS, getLstAttachementPaths());
        attributes.put(UPLOADED_FILES_PATHS, getLstUploadedFiles());
        String json = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer();

            json = ow.writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
            LoggerUtils.logException(e);
            throw new RuntimeException(e);
        }
   
        expression.append("Executioner.sendEmail( " + "\"")
                .append(this.jobEngineProjectID)
                .append("\",")
                .append("\"")
                .append(this.ruleId)
                .append("\",")
                .append("\"")
                .append(this.blockName)
                .append("\",")
                .append(json.replace("\\\"", "\""))
                .append(");\r\n");
        expression.append("\n");


        return expression.toString();

    }


}
