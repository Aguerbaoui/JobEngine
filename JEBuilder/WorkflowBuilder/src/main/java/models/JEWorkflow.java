package models;

import blocks.WorkflowBlock;
import blocks.basic.EndBlock;
import blocks.basic.ScriptBlock;
import blocks.basic.StartBlock;
import blocks.events.ErrorBoundaryEvent;
import io.je.utilities.beans.Status;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.runtimeobject.JEObject;
import org.springframework.data.mongodb.core.mapping.Document;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

import java.util.concurrent.ConcurrentHashMap;

import static io.je.utilities.constants.JEMessages.FAILED_TO_DELETE_FILES;

/*
 * Model class for a workflow
 * */
@Document(collection = "JEWorkflowCollection")
public class JEWorkflow extends JEObject {


    /*public final static String RUNNING = "RUNNING";

    public final static String BUILDING = "BUILDING";

    public final static String BUILT = "BUILT";

    public final static String IDLE = "IDLE";*/

    /*
     * Workflow start block
     */

    /*
     * Bpmn path
     */
    private String bpmnPath;

    /*
     * Current workflow status ( running, building, nothing)
     * */
    private Status status;

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

    /*
    * True if the workflow starts with project boot
    * */
    private boolean onProjectBoot = false;

    /*
    * Workflow description
    * */
    private String description;

    /*
    * True if workflow is enabled for execution
    * */
    private boolean isEnabled;

    private boolean hasErrors = false;

    private String getScript() {
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
        status = Status.NOT_BUILT;
    }

    public boolean isTriggeredByEvent() {
        return triggeredByEvent;
    }

    public void setTriggeredByEvent(boolean triggeredByEvent) {
        this.triggeredByEvent = triggeredByEvent;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public void setIsScript(boolean script) {
        isScript = script;
    }

    /*
     * Return workflow start block
     */
    public StartBlock getWorkflowStartBlock() throws WorkflowStartBlockNotDefinedException, WorkflowStartBlockNotUniqueException{
        StartBlock startBlock = null;

        for (WorkflowBlock block : allBlocks.values()) {
            if (block instanceof StartBlock) {
                if (startBlock != null) {
                    throw new WorkflowStartBlockNotUniqueException();
                }
                startBlock = (StartBlock) block;
            }
        }
        if (startBlock == null) {
            throw new WorkflowStartBlockNotDefinedException();
        }
        return startBlock;
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
    * True if the workflow starts on project boot
    * */
    public boolean isOnProjectBoot() {
        return onProjectBoot;
    }

    /*
     * True if the workflow starts on project boot
     * */
    public void setOnProjectBoot(boolean onProjectBoot) {
        this.onProjectBoot = onProjectBoot;
    }

    /*
     * Add a block to block list
     */
    public void addBlock(WorkflowBlock block) {
        if (block instanceof StartBlock) {
            block.setProcessed(false);
            this.setTriggeredByEvent(((StartBlock) block).getEventId() != null || ((StartBlock) block).getTimerEvent() != null);
        }
        allBlocks.put(block.getJobEngineElementID(), block);
        status = Status.NOT_BUILT;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(description != null)
            this.description = description;
    }

    /*
     * Add a block flow to a block
     */
    public void addBlockFlow(String from, String to, String condition) {
        //TODO Bug with condition ( in case of multiple inflows with multiple conditions currently we are assuming it's 1 condition)
        allBlocks.get(from).getOutFlows().put(to, to);
        allBlocks.get(from).setCondition(condition);
        if (allBlocks.get(to) != null && allBlocks.get(to).getInflows() != null) {
            allBlocks.get(to).getInflows().put(from, from);
        }
        if(allBlocks.get(to) instanceof ErrorBoundaryEvent) {
            ((ErrorBoundaryEvent) allBlocks.get(to)).setAttachedToRef(from);
        }
        status = Status.NOT_BUILT;
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
        status = Status.NOT_BUILT;
    }

    /*
     * Delete a workflow block
     * */
    public void deleteWorkflowBlock(String id) throws InvalidSequenceFlowException, WorkflowBlockNotFoundException {
        if(allBlocks.containsKey(id)) {
            for (String blockId : allBlocks.get(id).getInflows().values()) {
                WorkflowBlock block = this.getBlockById(blockId);
                deleteSequenceFlow(block.getJobEngineElementID(), id);
            }
            for (String blockId : allBlocks.get(id).getOutFlows().values()) {
                WorkflowBlock block = this.getBlockById(blockId);
                deleteSequenceFlow(id, block.getJobEngineElementID());
            }

            WorkflowBlock workflowBlock = allBlocks.get(id);
            if (workflowBlock == null) {
                throw new WorkflowBlockNotFoundException(JEMessages.WORKFLOW_BLOCK_NOT_FOUND);
            }
            if (workflowBlock instanceof ScriptBlock) {
                cleanUpScriptTaskBlock((ScriptBlock) workflowBlock);
            }
            allBlocks.remove(id);
            status = Status.NOT_BUILT;
        }
    }

    public void cleanUpScriptTaskBlock(ScriptBlock b) {
        try {
            FileUtilities.deleteFileFromPath(b.getScriptPath());
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error(FAILED_TO_DELETE_FILES, LogCategory.DESIGN_MODE, jobEngineProjectID, LogSubModule.WORKFLOW, b.getJobEngineElementID());
        }

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
        status = Status.NOT_BUILT;
    }

    public EndBlock getWorkflowEndBlock() throws WorkflowEndBlockNotUniqueException, WorkflowEndBlockNotDefinedException {
        EndBlock endBlock = null;
        for (WorkflowBlock workflowBlock: allBlocks.values()) {
            if(workflowBlock instanceof EndBlock) {
                if (endBlock != null) {
                    throw new WorkflowEndBlockNotUniqueException();
                }
                endBlock = (EndBlock) workflowBlock;
            }
        }
        if (endBlock == null) {
            throw new WorkflowEndBlockNotDefinedException();
        }
        return endBlock;
    }

    public void setScript(boolean script) {
        isScript = script;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    @Override
    public String toString() {
        return "JEWorkflow{" +
                "id='" + jobEngineElementID + '\'' +
                "workflowName='" + jobEngineElementName + '\'' +
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
