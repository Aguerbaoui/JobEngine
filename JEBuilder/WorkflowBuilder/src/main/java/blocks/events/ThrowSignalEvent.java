package blocks.events;

import blocks.WorkflowBlock;

public class ThrowSignalEvent extends WorkflowBlock {

    private String signalRef;

    public String getSignalRef() {
        return signalRef;
    }

    public void setSignalRef(String signalRef) {
        this.signalRef = signalRef;
    }
}
