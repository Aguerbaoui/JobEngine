package io.je.project.services;

import blocks.basic.*;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.InclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.*;
import builder.WorkflowBuilder;
import io.je.project.beans.JEProject;
import io.je.project.models.WorkflowBlockModel;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.EventType;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.string.JEStringUtils;
import models.JEWorkflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
/*
 * Service class to handle business logic for workflows
 * */
import java.util.concurrent.ExecutionException;

@Service
public class WorkflowService {

    public static final String NAME = "name";
    public static final String DURATION = "duration";
    public static final String ENDDATE = "enddate";
    public static final String TIMECYCLE = "timecycle";
    public static final String TIMEDATE = "timedate";
    public static final String SCRIPT = "script";
    public static final String EVENT_ID = "eventId";
    public static final String SOURCE_REF = "sourceRef";
    public static final String TARGET_REF = "targetRef";
    public static final String CONDITION = "condition";


    @Autowired
    EventService eventService;
    /*
     * Add a workflow to a project
     * */
    public void addWorkflow(io.je.utilities.models.WorkflowModel m) throws ProjectNotFoundException {
        JEProject project = ProjectService.getProjectById(m.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JEWorkflow wf = new JEWorkflow();
        wf.setJobEngineElementID(m.getKey());
        wf.setJobEngineProjectID(m.getProjectId());
        wf.setWorkflowName(m.getName());
        wf.setDescription(m.getDescription());
        wf.setJeObjectLastUpdate(LocalDateTime.now());
        wf.setJeObjectCreationDate(LocalDateTime.now());
        JELogger.trace(WorkflowService.class, " Adding new workflow with id = " + wf.getJobEngineElementID());
        project.addWorkflow(wf);
    }

    /*
     * Remove a workflow from a project
     * */
    public void removeWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, WorkflowNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Removing workflow with id = " + workflowId);
        project.removeWorkflow(workflowId);
    }

    /*
     * Add a workflow block to a workflow
     * */
    public void addWorkflowBlock(WorkflowBlockModel block) throws ProjectNotFoundException, WorkflowNotFoundException, InvalidSequenceFlowException, WorkflowBlockNotFound, EventException {
        JEProject project = ProjectService.getProjectById(block.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(block.getWorkflowId())) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Adding a workflow block with id = " + block.getId() + " in workflow with id = " + block.getWorkflowId());
        if (block.getType().equalsIgnoreCase(WorkflowConstants.START_TYPE)) {
            StartBlock b = new StartBlock();
            b.setName(block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty(block.getAttributes().get(EVENT_ID))) {
                eventService.updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), EventType.START_WORKFLOW.toString());
                //TODO throw exception in case runner didnt get the event
                b.setEventId(block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.END_TYPE)) {
            EndBlock b = new EndBlock();
            b.setName(block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EVENTGATEWAY_TYPE)) {
            EventGatewayBlock b = new EventGatewayBlock();
            b.setName(block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGEINTERMEDIATECATCHEVENT_TYPE)) {
            MessageEvent b = new MessageEvent();
            b.setName(block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty(block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID),  EventType.MESSAGE_EVENT.toString());
                b.setEventId(block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setThrowMessage(false);
            project.addBlockToWorkflow(b);

        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGE_THROW_EVENT_TYPE)) {
            MessageEvent b = new MessageEvent();
            b.setName(block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty(block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID),  EventType.MESSAGE_EVENT.toString());
                b.setEventId(block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setThrowMessage(true);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNALINTERMEDIATECATCHEVENT_TYPE)) {
            SignalEvent b = new SignalEvent();
            b.setName(block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty(block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID),  EventType.SIGNAL_EVENT.toString());
                b.setEventId(block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setThrowSignal(false);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNAL_THROW_EVENT_TYPE)) {
            SignalEvent b = new SignalEvent();
            b.setName(block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty(block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID),  EventType.SIGNAL_EVENT.toString());
                b.setEventId(block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setThrowSignal(true);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EXCLUSIVEGATEWAY_TYPE)) {
            ExclusiveGatewayBlock b = new ExclusiveGatewayBlock();
            b.setName(block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SCRIPTTASK_TYPE)) {
            ScriptBlock b = new ScriptBlock();
            b.setName(block.getAttributes().get(NAME));
            b.setScript(block.getAttributes().get(SCRIPT));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.PARALLELGATEWAY_TYPE)) {
            ParallelGatewayBlock b = new ParallelGatewayBlock();
            b.setName(block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.INCLUSIVEGATEWAY_TYPE)) {
            InclusiveGatewayBlock b = new InclusiveGatewayBlock();
            b.setName(block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DATETIMEREVENT)) {
            DateTimerEvent b = new DateTimerEvent();
            b.setName(block.getAttributes().get(NAME));
            b.setTimeDate(block.getAttributes().get(TIMEDATE));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.CYCLETIMEREVENT)) {
            CycleTimerEvent b = new CycleTimerEvent();
            b.setName(block.getAttributes().get(NAME));
            b.setTimeCycle(block.getAttributes().get(TIMECYCLE));
            b.setEndDate(block.getAttributes().get(ENDDATE));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DURATIONTIMEREVENT)) {
            DurationDelayTimerEvent b = new DurationDelayTimerEvent();
            b.setName(block.getAttributes().get(NAME));
            b.setTimeDuration(block.getAttributes().get(DURATION));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DBSERVICETASK_TYPE)) {
            DBWriteBlock b = new DBWriteBlock();
            b.setName(block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MAILSERVICETASK_TYPE)) {
            MailBlock b = new MailBlock();
            b.setName(block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SEQ_FLOW_TYPE)) {
            addSequenceFlow(block.getProjectId(), block.getWorkflowId(),
                    block.getAttributes().get(SOURCE_REF), block.getAttributes().get(TARGET_REF),
                    block.getAttributes().get(CONDITION));
        }
    }


    /*
     * Delete a workflow block
     * */
    public void deleteWorkflowBlock(String projectId, String workflowId, String blockId) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Deleting a workflow block with id = " + blockId + " in workflow with id = " + workflowId);


        project.deleteWorkflowBlock(workflowId, blockId);
    }

    /*
     * Delete a Sequence flow
     * */
    public void deleteSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Deleting a sequence flow with from " + sourceRef + " to  " + targetRef + " in workflow id = " + workflowId);
        project.deleteWorkflowSequenceFlow(workflowId, sourceRef, targetRef);
    }

    /*
     * Add a Sequence flow
     * */
    public void addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef, String condition) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }

        project.addWorkflowSequenceFlow(workflowId, sourceRef, targetRef, condition);
    }

    /*
     * Build a workflow
     * */
    public void buildWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException, JERunnerErrorException, InterruptedException, ExecutionException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Building workflow with id = " + workflowId);
        JEWorkflow workflow = project.getWorkflowById(workflowId);
        WorkflowBuilder.buildWorkflow(workflow);
    }

    /*
     * Build all workflow
     * */
    @Async
    public CompletableFuture<Void> buildWorkflows(String projectId) throws ProjectNotFoundException, IOException, JERunnerErrorException, InterruptedException, ExecutionException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

        for (JEWorkflow wf : project.getWorkflows().values()) {
            WorkflowBuilder.buildWorkflow(wf);
        }
        return CompletableFuture.completedFuture(null);

    }

    /*
     * Run a workflow
     * */
    public void runWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException, WorkflowAlreadyRunningException, InterruptedException, ExecutionException, JERunnerErrorException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }

        //set statuses wesh
        if (!project.getWorkflowById(workflowId).getStatus().equals(JEWorkflow.RUNNING)) {
            JELogger.trace(WorkflowService.class, " Running workflow with id = " + workflowId);
            project.getWorkflowById(workflowId).setStatus(JEWorkflow.RUNNING);
            WorkflowBuilder.runWorkflow(projectId, workflowId);
        } else {
            throw new WorkflowAlreadyRunningException(Errors.WORKFLOW_ALREADY_RUNNING);
        }
    }


    /*
     *
     * Update workflow block
     * */
    public void updateWorkflowBlock(WorkflowBlockModel block) throws WorkflowBlockNotFound, WorkflowNotFoundException, ProjectNotFoundException, IOException, InterruptedException, ExecutionException, EventException {
        JEProject project = ProjectService.getProjectById(block.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        if (!project.workflowExists(block.getWorkflowId())) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }

        if (!project.getWorkflowById(block.getWorkflowId()).blockExists(block.getId())) {
            throw new WorkflowBlockNotFound(Errors.WORKFLOW_BLOCK_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Updating a workflow block with id = " + block.getId() + " in workflow with id = " + block.getWorkflowId());
        if (block.getType().equalsIgnoreCase(WorkflowConstants.START_TYPE)) {
            StartBlock b = (StartBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty(block.getAttributes().get(EVENT_ID))) {
                eventService.updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID),  EventType.START_WORKFLOW.toString());
                //TODO throw exception in case runner didnt get the event
                b.setEventId(block.getAttributes().get(EVENT_ID));
            }
            else {
                b.setEventId(null);
            }
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.END_TYPE)) {
            EndBlock b = (EndBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EVENTGATEWAY_TYPE)) {
            EventGatewayBlock b = (EventGatewayBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGEINTERMEDIATECATCHEVENT_TYPE)) {
            MessageEvent b = (MessageEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty(block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID),  EventType.MESSAGE_EVENT.toString());
                b.setEventId(block.getAttributes().get(EVENT_ID));
            }
            else {
                b.setEventId(null);
            }
            b.setThrowMessage(false);
            project.addBlockToWorkflow(b);

        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGE_THROW_EVENT_TYPE)) {
            MessageEvent b = (MessageEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty(block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID),  EventType.MESSAGE_EVENT.toString());
                b.setEventId(block.getAttributes().get(EVENT_ID));
            }
            else {
                b.setEventId(null);
            }
            b.setThrowMessage(true);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNALINTERMEDIATECATCHEVENT_TYPE)) {
            SignalEvent b = (SignalEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty(block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID),  EventType.SIGNAL_EVENT.toString());
                b.setEventId(block.getAttributes().get(EVENT_ID));
            }
            else {
                b.setEventId(null);
            }
            b.setThrowSignal(false);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNAL_THROW_EVENT_TYPE)) {
            SignalEvent b = (SignalEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty(block.getAttributes().get(EVENT_ID))){

                eventService.updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID),  EventType.SIGNAL_EVENT.toString());
                b.setEventId(block.getAttributes().get(EVENT_ID));
            }
            else {
                b.setEventId(null);
            }
            b.setThrowSignal(true);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EXCLUSIVEGATEWAY_TYPE)) {
            ExclusiveGatewayBlock b = (ExclusiveGatewayBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SCRIPTTASK_TYPE)) {
            ScriptBlock b = (ScriptBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            b.setScript(block.getAttributes().get(SCRIPT));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.PARALLELGATEWAY_TYPE)) {
            ParallelGatewayBlock b = (ParallelGatewayBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.INCLUSIVEGATEWAY_TYPE)) {
            InclusiveGatewayBlock b = (InclusiveGatewayBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DATETIMEREVENT)) {
            DateTimerEvent b = (DateTimerEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            b.setTimeDate(block.getAttributes().get(TIMEDATE));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.CYCLETIMEREVENT)) {
            CycleTimerEvent b = (CycleTimerEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            b.setTimeCycle(block.getAttributes().get(TIMECYCLE));
            b.setEndDate(block.getAttributes().get(ENDDATE));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DURATIONTIMEREVENT)) {
            DurationDelayTimerEvent b = (DurationDelayTimerEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            b.setTimeDuration(block.getAttributes().get(DURATION));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DBSERVICETASK_TYPE)) {
            DBWriteBlock b = (DBWriteBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MAILSERVICETASK_TYPE)) {
            MailBlock b = new MailBlock();
            b.setName(block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        }

    }

    public void addBpmn(String projectId, String workflowId, String bpmn) throws ProjectNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Adding a bpmn script");
        JEWorkflow wf = new JEWorkflow();
        wf.setWorkflowName(workflowId);
        wf.setJobEngineProjectID(projectId);
        wf.setJobEngineElementID(workflowId);
        wf.setBpmnPath(WorkflowConstants.BPMN_PATH + wf.getWorkflowName().trim() + WorkflowConstants.BPMN_EXTENSION);
        wf.setScript(true);
        wf.setScript(bpmn);
        WorkflowBuilder.saveBpmn(wf, bpmn);
        project.addWorkflow(wf);

    }

    public void updateWorkflow(String projectId, String workflowId, WorkflowModel m) throws WorkflowNotFoundException, ProjectNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }

        if (m.getName() != null) {
            JELogger.trace(WorkflowService.class, " updating workflow with id = " + workflowId);
            project.getWorkflowById(workflowId).setWorkflowName(m.getName());
        }

    }

    public void setFrontConfig(String projectId, String workflowId, String config) throws ProjectNotFoundException, WorkflowNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException( Errors.PROJECT_NOT_FOUND);
        }

        if(! project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException( Errors.WORKFLOW_NOT_FOUND);
        }
        project.getWorkflowById(workflowId).setFrontConfig(config);
    }


    public void removeWorkflows(String projectId, List<String> ids) throws ProjectNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

        for(String id: ids) {
            try {
                removeWorkflow(projectId, id);
            }
            catch (WorkflowNotFoundException e) {
                JELogger.error(WorkflowService.class, " Error deleting a workflow: " + Arrays.toString(e.getStackTrace()));
            }
        }
    }
}
