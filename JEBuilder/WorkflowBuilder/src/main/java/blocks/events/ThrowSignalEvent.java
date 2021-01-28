package blocks.events;

import blocks.WorkflowBlock;

public class ThrowSignalEvent extends WorkflowBlock {

    private String signalRef;

    public String getMessageRef() {
        return signalRef;
    }

    public void setMessageRef(String signalRef) {
        this.signalRef = signalRef;
    }
}
