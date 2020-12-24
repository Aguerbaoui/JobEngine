package models;

import blocks.WorkflowBlock;
import blocks.basic.StartBlock;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.InvalidSequenceFlowException;
import io.je.utilities.exceptions.WorkflowBlockNotFound;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.JEObject;

import java.util.HashMap;

/*
 * Model class for a workflow
 * */
public class JEWorkflow extends JEObject {

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
     * checks if the workflow needs a new build
     * */
    private boolean needBuild;

    /*
    * Current workflow status ( running, building, nothing)
    * */
    private String status;
    /*
     * List of all workflow blocks
     */
    private HashMap<String, WorkflowBlock> allBlocks;

    /*
     * Constructor
     * */
    public JEWorkflow() {
        super();
        allBlocks = new HashMap<String, WorkflowBlock>();
        needBuild = true;
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
    public HashMap<String, WorkflowBlock> getAllBlocks() {
        return allBlocks;
    }

    /*
     * set all Workflow blocks
     */
    public void setAllBlocks(HashMap<String, WorkflowBlock> allBlocks) {
        this.allBlocks = allBlocks;
    }

    /*
     * Add a block to block list
     */
    public void addBlock(WorkflowBlock block) {
        if (block instanceof StartBlock) {
            workflowStartBlock = (StartBlock) block;
        }
        allBlocks.put(block.getJobEngineElementID(), block);
    }

    /*
     * Add a block flow to a block
     */
    public void addBlockFlow(String from, String to, String condition) {
        //TODO Bug with condition ( in case of multiple inflows with multiple conditions currently we assuming it's 1 condition
        allBlocks.get(from).getOutFlows().put(to, allBlocks.get(to));
        allBlocks.get(from).setCondition(condition);
        JELogger.info(JEWorkflow.class, allBlocks.get(to).toString());
        if (allBlocks.get(to) != null && allBlocks.get(to).getInflows() != null) {
            allBlocks.get(to).getInflows().put(from, allBlocks.get(from));
        }
    }

    /*
     * Delete sequence flow from workflow
     * */
    public void deleteSequenceFlow(String sourceRef, String targetRef) throws InvalidSequenceFlowException {
        if (!allBlocks.get(sourceRef).getOutFlows().containsKey(targetRef) || !allBlocks.get(targetRef).getInflows().containsKey(sourceRef)) {
            throw new InvalidSequenceFlowException("4", Errors.getMessage(4));
        }
        allBlocks.get(sourceRef).getOutFlows().remove(targetRef);
        allBlocks.get(targetRef).getInflows().remove(sourceRef);
    }

    /*
     * Delete a workflow block
     * */
    public void deleteWorkflowBlock(String id) throws InvalidSequenceFlowException, WorkflowBlockNotFound {
        for (WorkflowBlock block : allBlocks.get(id).getInflows().values()) {
            deleteSequenceFlow(block.getJobEngineElementID(), id);
        }
        for (WorkflowBlock block : allBlocks.get(id).getOutFlows().values()) {
            deleteSequenceFlow(id, block.getJobEngineElementID());
        }

        WorkflowBlock b = allBlocks.get(id);
        if(b == null) {
            throw new WorkflowBlockNotFound("3", Errors.workflowBloclNotFound);
        }
        allBlocks.remove(id);
        b = null;

    }

    public boolean blockExists(String bId) {
        return allBlocks.containsKey(bId);
    }
}
