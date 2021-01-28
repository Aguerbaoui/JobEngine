package blocks.basic;

import blocks.WorkflowBlock;

public class StartBlock extends WorkflowBlock {

    private String reference = null;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
