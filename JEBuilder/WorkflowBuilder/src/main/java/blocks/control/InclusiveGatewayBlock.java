package blocks.control;

import blocks.WorkflowBlock;

public class InclusiveGatewayBlock extends WorkflowBlock {

    boolean exclusive;

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }
}
