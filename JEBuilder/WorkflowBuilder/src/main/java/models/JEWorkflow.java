package models;

import blocks.WorkflowBlock;
import blocks.basic.StartBlock;
import blocks.events.ErrorBoundaryEvent;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.InvalidSequenceFlowException;
import io.je.utilities.exceptions.WorkflowBlockNotFound;
import io.je.utilities.runtimeobject.JEObject;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.ConcurrentHashMap;

/*
 * Model class for a workflow
 * */
@Document(collection = "JEWorkflowCollection")
public class JEWorkflow extends JEObject {

    public final static String RUNNING = "RUNNING";

    public final static String BUILDING = "BUILDING";

    public final static String BUILT = "BUILT";

    public final static String IDLE = "IDLE";
    /*
     * Workflow name
     */
    private String workflowName;

    /*
     * Workflow start block
     */
    private StartBlock workflowStartBlock;

    /*
     * Bpmn path
     */
    private String bpmnPath;

    /*
     * Current workflow status ( running, building, nothing)
     * */
    private String status;

    /*
     * List of all workflow blocks
     */
    private ConcurrentHashMap<String, WorkflowBlock> allBlocks;

    /*
     * front configuration
     * */
    private String frontConfig;

    /*
     * User scripted bpmn
     * */
    private boolean isScript = false;

    /*
     * Bpmn script
     * */
    private String script;

    /*
     * True if the workflow can be triggered by an event
     * */
    private boolean triggeredByEvent;

    private String description;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    /*
     * Constructor
     * */
    public JEWorkflow() {
        super();
        allBlocks = new ConcurrentHashMap<String, WorkflowBlock>();
        status = IDLE;
    }

    public boolean isTriggeredByEvent() {
        return triggeredByEvent;
    }

    public void setTriggeredByEvent(boolean triggeredByEvent) {
        this.triggeredByEvent = triggeredByEvent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrontConfig() {
        return frontConfig;
    }

    public void setFrontConfig(String frontConfig) {
        this.frontConfig = frontConfig;
    }

    public boolean isScript() {
        return isScript;
    }

    public void setScript(boolean script) {
        isScript = script;
    }

    /*
     * Return workflow name
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /*
     * set workflow name
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /*
     * Return workflow start block
     */
    public StartBlock getWorkflowStartBlock() {
        return workflowStartBlock;
    }

    /*
     * set workflow start block
     */
    public void setWorkflowStartBlock(StartBlock workflowStartBlock) {
        this.workflowStartBlock = workflowStartBlock;
    }

    /*
     * Return bpmn path
     */
    public String getBpmnPath() {
        return bpmnPath;
    }

    /*
     * set bpmn path
     */
    public void setBpmnPath(String bpmnPath) {
        this.bpmnPath = bpmnPath;
    }

    /*
     * Returns all Workflow blocks
     */
    public ConcurrentHashMap<String, WorkflowBlock> getAllBlocks() {
        return allBlocks;
    }

    /*
     * set all Workflow blocks
     */
    public void setAllBlocks(ConcurrentHashMap<String, WorkflowBlock> allBlocks) {
        this.allBlocks = allBlocks;
    }

    /*
     * Add a block to block list
     */
    public void addBlock(WorkflowBlock block) {
        if (block instanceof StartBlock) {
            workflowStartBlock = (StartBlock) block;
            workflowStartBlock.setProcessed(false);
            if (((StartBlock) block).getEventId() != null) {
                this.setTriggeredByEvent(true);
            } else this.setTriggeredByEvent(false);
        }
        allBlocks.put(block.getJobEngineElementID(), block);
        status = IDLE;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * Add a block flow to a block
     */
    public void addBlockFlow(String from, String to, String condition) {
        //TODO Bug with condition ( in case of multiple inflows with multiple conditions currently we assuming it's 1 condition
        allBlocks.get(from).getOutFlows().put(to, to);
        allBlocks.get(from).setCondition(condition);
        if (allBlocks.get(to) != null && allBlocks.get(to).getInflows() != null) {
            allBlocks.get(to).getInflows().put(from, from);
        }
        if(allBlocks.get(to) instanceof ErrorBoundaryEvent) {
            ((ErrorBoundaryEvent) allBlocks.get(to)).setAttachedToRef(from);
        }
        workflowStartBlock = (StartBlock) allBlocks.get(workflowStartBlock.getJobEngineElementID());
        status = IDLE;
    }

    /*
     * Delete sequence flow from workflow
     * */
    public void deleteSequenceFlow(String sourceRef, String targetRef) throws InvalidSequenceFlowException {
        if (!allBlocks.get(sourceRef).getOutFlows().containsKey(targetRef) || !allBlocks.get(targetRef).getInflows().containsKey(sourceRef)) {
            throw new InvalidSequenceFlowException(JEMessages.INVALID_SEQUENCE_FLOW);
        }
        allBlocks.get(sourceRef).getOutFlows().remove(targetRef);
        allBlocks.get(targetRef).getInflows().remove(sourceRef);
        status = IDLE;
    }

    /*
     * Delete a workflow block
     * */
    public void deleteWorkflowBlock(String id) throws InvalidSequenceFlowException, WorkflowBlockNotFound {
        for (String blockId : allBlocks.get(id).getInflows().values()) {
            WorkflowBlock block = this.getBlockById(blockId);
            deleteSequenceFlow(block.getJobEngineElementID(), id);
        }
        for (String blockId : allBlocks.get(id).getOutFlows().values()) {
            WorkflowBlock block = this.getBlockById(blockId);
            deleteSequenceFlow(id, block.getJobEngineElementID());
        }

        WorkflowBlock b = allBlocks.get(id);
        if (b == null) {
            throw new WorkflowBlockNotFound(JEMessages.WORKFLOW_BLOCK_NOT_FOUND);
        }
        allBlocks.remove(id);
        if (allBlocks.size() == 0) workflowStartBlock = null;
        b = null;
        status = IDLE;

    }

    public boolean blockExists(String bId) {
        return allBlocks.containsKey(bId);
    }

    public WorkflowBlock getBlockById(String i) {
        return allBlocks.get(i);
    }

    public void resetBlocks() {
        for (WorkflowBlock block : allBlocks.values()) {
            block.setProcessed(false);
        }
        workflowStartBlock.setProcessed(false);
        status = IDLE;
    }

    @Override
    public String toString() {
        return "JEWorkflow{" +
                "id='" + jobEngineElementID + '\'' +
                "workflowName='" + workflowName + '\'' +
                ", workflowStartBlock=" + workflowStartBlock +
                ", bpmnPath='" + bpmnPath + '\'' +
                ", status='" + status + '\'' +
                ", allBlocks=" + allBlocks +
                ", frontConfig='" + frontConfig + '\'' +
                ", isScript=" + isScript +
                ", script='" + script + '\'' +
                ", triggeredByEvent=" + triggeredByEvent +
                ", description='" + description + '\'' +
                '}';
    }
}
