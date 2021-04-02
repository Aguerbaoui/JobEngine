package blocks.basic;

import blocks.WorkflowBlock;

public class InformBlock extends WorkflowBlock {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
