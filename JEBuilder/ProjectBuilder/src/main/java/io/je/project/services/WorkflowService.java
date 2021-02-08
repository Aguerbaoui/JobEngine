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
import io.je.project.models.WorkflowModel;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import models.JEWorkflow;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
/*
 * Service class to handle business logic for workflows
 * */
import java.util.concurrent.CompletableFuture;

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

    /*
     * Add a workflow to a project
     * */
    @Async
    public CompletableFuture<Void> addWorkflow(JEWorkflow wf) throws ProjectNotFoundException {
        JEProject project = ProjectService.getProjectById(wf.getJobEngineProjectID());
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Adding new workflow with id = " + wf.getJobEngineElementID());
        project.addWorkflow(wf);
        return CompletableFuture.completedFuture(null);
    }

    /*
     * Remove a workflow from a project
     * */
    @Async
    public CompletableFuture<Void> removeWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, WorkflowNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Removing workflow with id = " + workflowId);
        project.removeWorkflow(workflowId);
        return CompletableFuture.completedFuture(null);
    }

    /*
     * Add a workflow block to a workflow
     * */
    @Async
    public CompletableFuture<Void> addWorkflowBlock(WorkflowBlockModel block) throws ProjectNotFoundException, WorkflowNotFoundException, InvalidSequenceFlowException, WorkflowBlockNotFound {
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
            if (block.getAttributes().get(EVENT_ID) != null) {
                try {
                    updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), "start");
                    //TODO throw exception in case runner didnt get the event
                    b.setEventId(block.getAttributes().get(EVENT_ID));
                } catch (JERunnerErrorException e) {
                    JELogger.error(getClass(), "Failed to set event type");
                }
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
            MessageCatchEvent b = new MessageCatchEvent();
            b.setName(block.getAttributes().get(NAME));
            if (block.getAttributes().get(EVENT_ID) != null) {

                try {
                    updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), "message");
                    b.setEventId(block.getAttributes().get(EVENT_ID));
                } catch (JERunnerErrorException e) {
                    JELogger.error(getClass(), "Failed to set event type");
                }
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);

        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGE_THROW_EVENT_TYPE)) {
            ThrowMessageEvent b = new ThrowMessageEvent();
            b.setName(block.getAttributes().get(NAME));
            if (block.getAttributes().get(EVENT_ID) != null) {

                try {
                    updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), "message");
                    b.setEventId(block.getAttributes().get(EVENT_ID));
                } catch (JERunnerErrorException e) {
                    JELogger.error(getClass(), "Failed to set event type");
                }
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNALINTERMEDIATECATCHEVENT_TYPE)) {
            SignalCatchEvent b = new SignalCatchEvent();
            b.setName(block.getAttributes().get(NAME));
            if (block.getAttributes().get(EVENT_ID) != null) {

                try {
                    updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), "signal");
                    b.setEventId(block.getAttributes().get(EVENT_ID));
                } catch (JERunnerErrorException e) {
                    JELogger.error(getClass(), "Failed to set event type");
                }
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNAL_THROW_EVENT_TYPE)) {
            ThrowSignalEvent b = new ThrowSignalEvent();
            b.setName(block.getAttributes().get(NAME));
            if (block.getAttributes().get(EVENT_ID) != null) {

                try {
                    updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), "signal");
                    b.setEventId(block.getAttributes().get(EVENT_ID));
                } catch (JERunnerErrorException e) {
                    JELogger.error(getClass(), "Failed to set event type");
                }
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
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
        return CompletableFuture.completedFuture(null);
    }
    
    private void updateEventType(String projectId, String eventId, String type) throws JERunnerErrorException {
        JERunnerAPIHandler.updateEventType(projectId, eventId, type);
    }

    /*
     * Delete a workflow block
     * */
    @Async
    public CompletableFuture<Void> deleteWorkflowBlock(String projectId, String workflowId, String blockId) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Deleting a workflow block with id = " + blockId + " in workflow with id = " + workflowId);

        project.deleteWorkflowBlock(workflowId, blockId);
        return CompletableFuture.completedFuture(null);
    }

    /*
     * Delete a Sequence flow
     * */
    @Async
    public CompletableFuture<Void> deleteSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Deleting a sequence flow with from " + sourceRef + " to  " + targetRef + " in workflow id = " + workflowId);
        project.deleteWorkflowSequenceFlow(workflowId, sourceRef, targetRef);
        return CompletableFuture.completedFuture(null);
    }

    /*
     * Add a Sequence flow
     * */
    @Async
    public CompletableFuture<Void> addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef, String condition) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }

        project.addWorkflowSequenceFlow(workflowId, sourceRef, targetRef, condition);
        return CompletableFuture.completedFuture(null);
    }

    /*
     * Build a workflow
     * */
    @Async
    public CompletableFuture<Void> buildWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException, JERunnerErrorException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Building workflow with id = " + workflowId);
        WorkflowBuilder.buildWorkflow(project.getWorkflowById(workflowId));
        return CompletableFuture.completedFuture(null);
    }

    /*
     * Build all workflow
     * */
    @Async
    public CompletableFuture<Void> buildWorkflows(String projectId) throws ProjectNotFoundException, IOException, JERunnerErrorException {
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
    @Async
    public CompletableFuture<Void> runWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException, WorkflowAlreadyRunningException {
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
        return CompletableFuture.completedFuture(null);
    }

    /*
     * Run all workflows
     * */
    @Async
    public CompletableFuture<Void> runWorkflows(String projectId) throws ProjectNotFoundException, IOException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        for (JEWorkflow wf : project.getWorkflows().values()) {
            WorkflowBuilder.runWorkflow(projectId, wf.getWorkflowName().trim());
        }
        return CompletableFuture.completedFuture(null);
    }

    /*
     *
     * Update workflow block
     * */
    @Async
    public CompletableFuture<Void> updateWorkflowBlock(WorkflowBlockModel block) throws WorkflowBlockNotFound, WorkflowNotFoundException, ProjectNotFoundException {
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
            if (block.getAttributes().get(EVENT_ID) != null) {
                try {
                    updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), "start");
                    //TODO throw exception in case runner didnt get the event
                    b.setEventId(block.getAttributes().get(EVENT_ID));
                } catch (JERunnerErrorException e) {
                    JELogger.error(getClass(), "Failed to set event type");
                }
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
            MessageCatchEvent b = (MessageCatchEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            if (block.getAttributes().get(EVENT_ID) != null) {

                try {
                    updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), "message");
                    b.setEventId(block.getAttributes().get(EVENT_ID));
                } catch (JERunnerErrorException e) {
                    JELogger.error(getClass(), "Failed to set event type");
                }
            }
            project.addBlockToWorkflow(b);

        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGE_THROW_EVENT_TYPE)) {
            ThrowMessageEvent b = (ThrowMessageEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            if (block.getAttributes().get(EVENT_ID) != null) {

                try {
                    updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), "message");
                    b.setEventId(block.getAttributes().get(EVENT_ID));
                } catch (JERunnerErrorException e) {
                    JELogger.error(getClass(), "Failed to set event type");
                }
            }
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNALINTERMEDIATECATCHEVENT_TYPE)) {
            SignalCatchEvent b = (SignalCatchEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            if (block.getAttributes().get(EVENT_ID) != null) {

                try {
                    updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), "signal");
                    b.setEventId(block.getAttributes().get(EVENT_ID));
                } catch (JERunnerErrorException e) {
                    JELogger.error(getClass(), "Failed to set event type");
                }
            }
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNAL_THROW_EVENT_TYPE)) {
            ThrowSignalEvent b = (ThrowSignalEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName(block.getAttributes().get(NAME));
            if (block.getAttributes().get(EVENT_ID) != null) {

                try {
                    updateEventType(block.getProjectId(), block.getAttributes().get(EVENT_ID), "signal");
                    b.setEventId(block.getAttributes().get(EVENT_ID));
                } catch (JERunnerErrorException e) {
                    JELogger.error(getClass(), "Failed to set event type");
                }
            }
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
        return CompletableFuture.completedFuture(null);

    }
    @Async
    public CompletableFuture<Void> addBpmn(String projectId, String workflowId, String bpmn) throws ProjectNotFoundException {
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
        return CompletableFuture.completedFuture(null);

    }
    @Async
    public CompletableFuture<Void> updateWorkflow(String projectId, String workflowId, WorkflowModel m) throws WorkflowNotFoundException, ProjectNotFoundException {
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
        return CompletableFuture.completedFuture(null);
    }
}
