package blocks.basic;

import blocks.WorkflowBlock;

public class SubProcessBlock extends WorkflowBlock {

    private String subWorkflowId;

    public String getSubWorkflowId() {
        return subWorkflowId;
    }

    public void setSubWorkflowId(String subWorkflowId) {
        this.subWorkflowId = subWorkflowId;
    }
}
