package blocks.events;

import blocks.WorkflowBlock;

public class MessageCatchEvent extends WorkflowBlock {

    private String messageRef;

    public String getMessageRef() {
        return messageRef;
    }

    public void setMessageRef(String messageRef) {
        this.messageRef = messageRef;
    }
}
