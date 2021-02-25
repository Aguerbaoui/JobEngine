package blocks.control;

import blocks.WorkflowBlock;

public class EventGatewayBlock extends WorkflowBlock {

    boolean exclusive;

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

}
