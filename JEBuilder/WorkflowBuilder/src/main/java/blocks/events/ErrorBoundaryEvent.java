package blocks.events;

import blocks.WorkflowBlock;

public class ErrorBoundaryEvent extends WorkflowBlock {

    private String errorRef = "Error";

    private String attachedToRef;

    public String getErrorRef() {
        return errorRef;
    }

    public void setErrorRef(String errorRef) {
        this.errorRef = errorRef;
    }

    public String getAttachedToRef() {
        return attachedToRef;
    }

    public void setAttachedToRef(String attachedToRef) {
        this.attachedToRef = attachedToRef;
    }
}
