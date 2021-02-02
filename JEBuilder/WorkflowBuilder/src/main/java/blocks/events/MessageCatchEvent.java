package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.string.JEStringUtils;

public class MessageCatchEvent extends WorkflowBlock {

    private String messageRef;

    public String getMessageRef() {
        return messageRef;
    }

    public void setMessageRef(String messageRef) {
        if (JEStringUtils.isEmpty(messageRef)) return;
        if(messageRef.equalsIgnoreCase(APIConstants.DEFAULT)) this.messageRef = null;
        else
            this.messageRef = messageRef;
    }
}
