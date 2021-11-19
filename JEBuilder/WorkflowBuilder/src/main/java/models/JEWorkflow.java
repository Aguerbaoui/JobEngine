package models;

import blocks.WorkflowBlock;
import blocks.basic.EndBlock;
import blocks.basic.StartBlock;
import blocks.events.ErrorBoundaryEvent;
import io.je.utilities.beans.Status;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.InvalidSequenceFlowException;
import io.je.utilities.exceptions.WorkflowBlockNotFound;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.monitoring.JEMonitor;
import io.je.utilities.monitoring.MonitoringMessage;
import io.je.utilities.monitoring.ObjectType;
import io.je.utilities.runtimeobject.JEObject;
import org.springframework.data.mongodb.core.mapping.Document;
import utils.date.DateUtils;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import static io.je.utilities.constants.JEMessages.SENDING_WORKFLOW_MONITORING_DATA_TO_JEMONITOR;

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
    private StartBlock workflowStartBlock;

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
            workflowStartBlock = (StartBlock) block;
            workflowStartBlock.setProcessed(false);
            if (((StartBlock) block).getEventId() != null || ((StartBlock) block).getTimerEvent() != null ) {
                this.setTriggeredByEvent(true);
            } else this.setTriggeredByEvent(false);
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
        //TODO Bug with condition ( in case of multiple inflows with multiple conditions currently we assuming it's 1 condition
        allBlocks.get(from).getOutFlows().put(to, to);
        allBlocks.get(from).setCondition(condition);
        if (allBlocks.get(to) != null && allBlocks.get(to).getInflows() != null) {
            allBlocks.get(to).getInflows().put(from, from);
        }
        if(allBlocks.get(to) instanceof ErrorBoundaryEvent) {
            ((ErrorBoundaryEvent) allBlocks.get(to)).setAttachedToRef(from);
        }
        if(workflowStartBlock != null) {
            workflowStartBlock = (StartBlock) allBlocks.get(workflowStartBlock.getJobEngineElementID());
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
        status = Status.NOT_BUILT;

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
        status = Status.NOT_BUILT;
    }
    

    public static WorkflowModel mapJEWorkflowToModel(JEWorkflow wf) {
        WorkflowModel model = new WorkflowModel();
        model.setName(wf.getJobEngineElementName());
        model.setOnProjectBoot(wf.isOnProjectBoot());
        model.setModifiedBy(wf.getJeObjectModifiedBy());
        model.setDescription(wf.getDescription());
        model.setCreatedBy(wf.getJeObjectCreatedBy());
        model.setId(wf.getJobEngineElementID());
        model.setPath(wf.getBpmnPath());
        model.setProjectId(wf.getJobEngineProjectID());
        model.setTriggeredByEvent(wf.isTriggeredByEvent());
        model.setStatus(wf.getStatus().toString());
        model.setCreatedAt(DateUtils.formatDateToSIOTHFormat(wf.getJeObjectCreationDate()));
        model.setModifiedAt(DateUtils.formatDateToSIOTHFormat(wf.getJeObjectLastUpdate()));
        model.setFrontConfig(wf.getFrontConfig());
        model.setEnabled(wf.isEnabled);
        return model;
    }

    public EndBlock getWorkflowEndBlock() {
        for (WorkflowBlock b: allBlocks.values()) {
            if(b instanceof EndBlock) {
                return (EndBlock) b;
            }
        }
        return null;
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
