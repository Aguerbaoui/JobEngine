package blocks;

import builder.ModelBuilder;
import io.je.utilities.runtimeobject.JEObject;
import io.je.utilities.string.JEStringUtils;
import org.activiti.bpmn.model.SequenceFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Model class for a workflow block
 * */
public class WorkflowBlock extends JEObject {

    /*
     * Incoming flows
     * */
    private HashMap<String, WorkflowBlock> inflows;

    /*
     * Outgoing flows
     * */
    private HashMap<String, WorkflowBlock> outFlows;

    /*
     * Condition to reach the block
     * */
    private String condition;

    /*
     * Block Name
     * */
    private String name;

    /*
     * Block processing state ( true if parsed in the tree )
     * */
    private boolean processed;

    /*
     * Workflow id
     * */
    private String workflowId;

    /*
     * Constructor
     * */
    public WorkflowBlock() {
        inflows = new HashMap<String, WorkflowBlock>();
        outFlows = new HashMap<String, WorkflowBlock>();
        processed = false;
    }

    /*
     * Returns incoming flows
     * */
    public HashMap<String, WorkflowBlock> getInflows() {
        return inflows;
    }

    /*
     * Sets incoming flows
     * */
    public void setInflows(HashMap<String, WorkflowBlock> inflows) {
        this.inflows = inflows;
    }

    /*
     * Returns outgoing flows
     * */
    public HashMap<String, WorkflowBlock> getOutFlows() {
        return outFlows;
    }

    /*
     * Set outgoing flows
     * */
    public void setOutFlows(HashMap<String, WorkflowBlock> outFlows) {
        this.outFlows = outFlows;
    }

    /*
     * Returns block name
     * */
    public String getName() {
        return name;
    }

    /*
     * Sets block name
     * */
    public void setName(String name) {
        if (JEStringUtils.isEmpty(name)) return;
        this.name = name;
    }

    /*
     * Returns generated bmpn incoming flows
     * */
    public List<SequenceFlow> generateBpmnInflows() {
        List<SequenceFlow> l = new ArrayList<SequenceFlow>();
        for (WorkflowBlock block : inflows.values()) {
            l.add(ModelBuilder.createSequenceFlow(block.getJobEngineElementID(), this.getJobEngineElementID(), ""));
        }
        return l;
    }

    /*
     * Returns generated bmpn outgoing flows
     * */
    public List<SequenceFlow> generateBpmnOutflows() {
        List<SequenceFlow> l = new ArrayList<SequenceFlow>();
        for (WorkflowBlock block : outFlows.values()) {
            l.add(ModelBuilder.createSequenceFlow(this.getJobEngineElementID(), block.getJobEngineElementID(), ""));
        }
        return l;
    }

    /*
     * Returns if block is processed in the tree
     * */
    public boolean isProcessed() {
        return processed;
    }

    /*
     * Sets block processed state
     * */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        if (JEStringUtils.isEmpty(condition)) return;
        this.condition = condition;
    }


}
