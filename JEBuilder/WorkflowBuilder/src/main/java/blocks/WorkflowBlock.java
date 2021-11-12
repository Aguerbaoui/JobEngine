package blocks;

import builder.ModelBuilder;
import io.je.utilities.runtimeobject.JEObject;
import models.JEWorkflow;
import org.activiti.bpmn.model.SequenceFlow;
import utils.string.StringUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Model class for a workflow block
 * */
public class WorkflowBlock extends JEObject {

    /*
     * Incoming flows
     * */
    private ConcurrentHashMap<String, String> inflows;

    /*
     * Outgoing flows
     * */
    private ConcurrentHashMap<String, String> outFlows;

    /*
     * Condition to reach the block
     * */
    private String condition;

    /*Block description*/
    private String description;

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
        inflows = new ConcurrentHashMap<String, String>();
        outFlows = new ConcurrentHashMap<String, String>();
        processed = false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * Returns incoming flows
     * */
    public ConcurrentHashMap<String, String> getInflows() {
        return inflows;
    }

    /*
     * Sets incoming flows
     * */
    public void setInflows(ConcurrentHashMap<String, String> inflows) {
        this.inflows = inflows;
    }

    /*
     * Returns outgoing flows
     * */
    public ConcurrentHashMap<String, String> getOutFlows() {
        return outFlows;
    }

    /*
     * Set outgoing flows
     * */
    public void setOutFlows(ConcurrentHashMap<String, String> outFlows) {
        this.outFlows = outFlows;
    }


    /*
     * Returns generated bmpn incoming flows
     * */
    public List<SequenceFlow> generateBpmnInflows(JEWorkflow wf) {
        List<SequenceFlow> l = new ArrayList<SequenceFlow>();
        for (String id : inflows.values()) {
            WorkflowBlock block = wf.getBlockById(id);
            l.add(ModelBuilder.createSequenceFlow(block.getJobEngineElementID(), this.getJobEngineElementID(), ""));
        }
        return l;
    }

    /*
     * Returns generated bmpn outgoing flows
     * */
    public List<SequenceFlow> generateBpmnOutflows(JEWorkflow wf) {
        List<SequenceFlow> l = new ArrayList<SequenceFlow>();
        for (String id : outFlows.values()) {
            WorkflowBlock block = wf.getBlockById(id);
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
        if (StringUtilities.isEmpty(condition)) return;
        this.condition = condition;
    }


}
