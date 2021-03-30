package io.je.project.services;

import blocks.basic.*;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.InclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.*;
import builder.WorkflowBuilder;
import io.je.classbuilder.builder.ClassBuilder;
import io.je.classbuilder.entity.JEClass;
import io.je.classbuilder.models.ClassModel;
import io.je.classbuilder.models.MethodModel;
import io.je.project.beans.JEProject;
import io.je.project.models.WorkflowBlockModel;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    public static final String DESCRIPTION = "description";
    public static final String METHOD = "method";
    public static final String URL = "url";
    public static final String INPUTS = "inputs";
    public static final String OUTPUTS = "outputs";

    @Autowired
    EventService eventService;

    @Autowired
    ClassService classService;
    /*
     * Add a workflow to a project
     * */
    public void addWorkflow(io.je.utilities.models.WorkflowModel m) throws ProjectNotFoundException, ConfigException {
    	ConfigurationService.checkConfig();
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

    public void removeWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, WorkflowNotFoundException, ConfigException, InterruptedException, JERunnerErrorException, ExecutionException {
    	ConfigurationService.checkConfig();

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Removing workflow with id = " + workflowId);
        String wfName = project.getWorkflowById(workflowId).getWorkflowName().trim();
        project.removeWorkflow(workflowId);
        JERunnerAPIHandler.deleteWorkflow(projectId, wfName);
    }

    /*
     * Add a workflow block to a workflow
     * */
    public void addWorkflowBlock(WorkflowBlockModel block) throws ProjectNotFoundException, WorkflowNotFoundException, InvalidSequenceFlowException, WorkflowBlockNotFound, EventException, ConfigException, WorkflowBlockException {
    	ConfigurationService.checkConfig();
    	JEProject project = ProjectService.getProjectById(block.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(block.getWorkflowId())) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Adding a workflow block with id = " + block.getId() + " in workflow with id = " + block.getWorkflowId());
        if (block.getType().equalsIgnoreCase(WorkflowConstants.START_TYPE)) {
            StartBlock b = new StartBlock();
            b.setName((String) block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty((String) block.getAttributes().get(EVENT_ID))) {
                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.START_WORKFLOW.toString());
                //TODO throw exception in case runner didnt get the event
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.END_TYPE)) {
            EndBlock b = new EndBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EVENTGATEWAY_TYPE)) {
            EventGatewayBlock b = new EventGatewayBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGEINTERMEDIATECATCHEVENT_TYPE)) {
            MessageEvent b = new MessageEvent();
            b.setName((String) block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID),  EventType.MESSAGE_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setThrowMessage(false);
            project.addBlockToWorkflow(b);

        } /*else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGE_THROW_EVENT_TYPE)) {
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
        }*/ else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNALINTERMEDIATECATCHEVENT_TYPE)) {
            SignalEvent b = new SignalEvent();
            b.setName((String) block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID),  EventType.SIGNAL_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setThrowSignal(false);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNAL_THROW_EVENT_TYPE)) {
            SignalEvent b = new SignalEvent();
            b.setName((String) block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID),  EventType.SIGNAL_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setThrowSignal(true);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EXCLUSIVEGATEWAY_TYPE)) {
            ExclusiveGatewayBlock b = new ExclusiveGatewayBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SCRIPTTASK_TYPE)) {
            ScriptBlock b = new ScriptBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setScript((String) block.getAttributes().get(SCRIPT));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.PARALLELGATEWAY_TYPE)) {
            ParallelGatewayBlock b = new ParallelGatewayBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.INCLUSIVEGATEWAY_TYPE)) {
            InclusiveGatewayBlock b = new InclusiveGatewayBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DATETIMEREVENT)) {
            DateTimerEvent b = new DateTimerEvent();
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeDate((String) block.getAttributes().get(TIMEDATE));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.CYCLETIMEREVENT)) {
            CycleTimerEvent b = new CycleTimerEvent();
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeCycle((String) block.getAttributes().get(TIMECYCLE));
            b.setEndDate((String) block.getAttributes().get(ENDDATE));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DURATIONTIMEREVENT)) {
            DurationDelayTimerEvent b = new DurationDelayTimerEvent();
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeDuration((String) block.getAttributes().get(DURATION));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DBSERVICETASK_TYPE)) {
            DBWriteBlock b = new DBWriteBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MAILSERVICETASK_TYPE)) {
            MailBlock b = new MailBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        }
        else if (block.getType().equalsIgnoreCase(WorkflowConstants.WEBSERVICETASK_TYPE)) {
            WebApiBlock b = new WebApiBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setDescription((String) block.getAttributes().get(DESCRIPTION));
            b.setMethod((String) block.getAttributes().get(METHOD));
            b.setUrl((String) block.getAttributes().get(URL));
            b.setInputs((HashMap<String, String>) block.getAttributes().get(INPUTS));
            b.setOutputs((HashMap<String, String>) block.getAttributes().get(OUTPUTS));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        }

        else if (block.getType().equalsIgnoreCase(WorkflowConstants.SEQ_FLOW_TYPE)) {
            addSequenceFlow(block.getProjectId(), block.getWorkflowId(),
                    (String) block.getAttributes().get(SOURCE_REF), (String) block.getAttributes().get(TARGET_REF),
                    (String) block.getAttributes().get(CONDITION));
        }
        else {
            throw new WorkflowBlockNotFound(Errors.WORKFLOW_BLOCK_NOT_FOUND);
        }
    }


    /*
     * Delete a workflow block
     * */
    public void deleteWorkflowBlock(String projectId, String workflowId, String blockId) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException, ConfigException {
    	ConfigurationService.checkConfig();
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
    public void deleteSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException, ConfigException {
    	ConfigurationService.checkConfig();
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
    public void addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef, String condition) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException, ConfigException {
    	ConfigurationService.checkConfig();
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }
        JELogger.trace(WorkflowService.class, " Adding a sequence flow with from " + sourceRef + " to  " + targetRef + " in workflow id = " + workflowId);
        project.addWorkflowSequenceFlow(workflowId, sourceRef, targetRef, condition);
    }

    /*
     * Build a workflow
     * */
    public void buildWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException, JERunnerErrorException, InterruptedException, ExecutionException, ConfigException {
    	ConfigurationService.checkConfig();
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
    public CompletableFuture<Void> buildWorkflows(String projectId) throws ProjectNotFoundException, IOException, JERunnerErrorException, InterruptedException, ExecutionException, ConfigException {
    	ConfigurationService.checkConfig();
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JELogger.trace(" Building workflows in project id = " + projectId);
        for (JEWorkflow wf : project.getWorkflows().values()) {
            WorkflowBuilder.buildWorkflow(wf);
        }
        return CompletableFuture.completedFuture(null);

    }

    /*
     * Run a workflow
     * */
    public void runWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException, WorkflowAlreadyRunningException, InterruptedException, ExecutionException, JERunnerErrorException, ConfigException {
    	ConfigurationService.checkConfig();
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(Errors.WORKFLOW_NOT_FOUND);
        }

        if(!project.getWorkflowById(workflowId).getStatus().equals(JEWorkflow.BUILT)) {
            throw new WorkflowAlreadyRunningException(Errors.WORKFLOW_NEEDS_BUILD);
        }
        //set statuse
        if (!project.getWorkflowById(workflowId).getStatus().equals(JEWorkflow.RUNNING)) {
            JELogger.trace(WorkflowService.class, " Running workflow with id = " + workflowId);
            project.getWorkflowById(workflowId).setStatus(JEWorkflow.RUNNING);
            try {
                WorkflowBuilder.runWorkflow(projectId, project.getWorkflowById(workflowId).getWorkflowName().trim());
            }
            catch(JERunnerErrorException e ) {
                project.getWorkflowById(workflowId).setStatus(JEWorkflow.IDLE);
                JELogger.error(WorkflowService.class, " Error running workflow in runner msg = " + e.getMessage());
            }
        } else {
            throw new WorkflowAlreadyRunningException(Errors.WORKFLOW_ALREADY_RUNNING);
        }
    }


    /*
     *
     * Update workflow block
     * */
    public void updateWorkflowBlock(WorkflowBlockModel block) throws WorkflowBlockNotFound, WorkflowNotFoundException, ProjectNotFoundException, IOException, InterruptedException, ExecutionException, EventException, ConfigException, WorkflowBlockException, ClassLoadException, JERunnerErrorException, AddClassException, DataDefinitionUnreachableException {
    	ConfigurationService.checkConfig();
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
            b.setName((String) block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty((String) block.getAttributes().get(EVENT_ID))) {
                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID),  EventType.START_WORKFLOW.toString());
                //TODO throw exception in case runner didnt get the event
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            else {
                b.setEventId(null);
            }
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.END_TYPE)) {
            EndBlock b = (EndBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EVENTGATEWAY_TYPE)) {
            EventGatewayBlock b = (EventGatewayBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGEINTERMEDIATECATCHEVENT_TYPE)) {
            MessageEvent b = (MessageEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID),  EventType.MESSAGE_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            else {
                b.setEventId(null);
            }
            b.setThrowMessage(false);
            project.addBlockToWorkflow(b);

        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGE_THROW_EVENT_TYPE)) {
            MessageEvent b = (MessageEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID),  EventType.MESSAGE_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            else {
                b.setEventId(null);
            }
            b.setThrowMessage(true);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNALINTERMEDIATECATCHEVENT_TYPE)) {
            SignalEvent b = (SignalEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID),  EventType.SIGNAL_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            else {
                b.setEventId(null);
            }
            b.setThrowSignal(false);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNAL_THROW_EVENT_TYPE)) {
            SignalEvent b = (SignalEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            if (!JEStringUtils.isEmpty((String) block.getAttributes().get(EVENT_ID))){

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID),  EventType.SIGNAL_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            else {
                b.setEventId(null);
            }
            b.setThrowSignal(true);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EXCLUSIVEGATEWAY_TYPE)) {
            ExclusiveGatewayBlock b = (ExclusiveGatewayBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SCRIPTTASK_TYPE)) {
            if(JEStringUtils.isEmpty((String) block.getAttributes().get(SCRIPT))) {
                throw new WorkflowBlockException(ResponseCodes.EMPTY_SCRIPT, ResponseMessages.EMPTY_SCRIPT);
            }
            ScriptBlock b = (ScriptBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setScript((String) block.getAttributes().get(SCRIPT));
            ClassModel c = getClassModel(b.getJobEngineElementID(), b.getName(), b.getScript());
            classService.addClass(c);
            //JEClassLoader.generateScriptTaskClass(b.getName(), b.getScript());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.PARALLELGATEWAY_TYPE)) {
            ParallelGatewayBlock b = (ParallelGatewayBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.INCLUSIVEGATEWAY_TYPE)) {
            InclusiveGatewayBlock b = (InclusiveGatewayBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DATETIMEREVENT)) {
            DateTimerEvent b = (DateTimerEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeDate((String) block.getAttributes().get(TIMEDATE));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.CYCLETIMEREVENT)) {
            CycleTimerEvent b = (CycleTimerEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeCycle((String) block.getAttributes().get(TIMECYCLE));
            b.setEndDate((String) block.getAttributes().get(ENDDATE));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DURATIONTIMEREVENT)) {
            DurationDelayTimerEvent b = (DurationDelayTimerEvent) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeDuration((String) block.getAttributes().get(DURATION));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DBSERVICETASK_TYPE)) {
            DBWriteBlock b = (DBWriteBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MAILSERVICETASK_TYPE)) {
            MailBlock b = (MailBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setbUseDefaultCredentials((Boolean) block.getAttributes().get("useDefaultCredentials"));
            b.setiPort((Integer) block.getAttributes().get("port"));
            b.setStrSenderAddress((String) block.getAttributes().get("senderAddress"));
            b.setiSendTimeOut((Integer) block.getAttributes().get("timeout"));
            b.setStrSMTPServer((String) block.getAttributes().get("smtpHost"));
            b.setLstRecieverAddress((List<String>) block.getAttributes().get("recipientsList"));
            b.setEmailMessage((HashMap<String, String>) block.getAttributes().get("message"));
            b.setStrPassword((String) block.getAttributes().get("password"));
            b.setStrUserName((String) block.getAttributes().get("username"));

            project.addBlockToWorkflow(b);
        }
        else if (block.getType().equalsIgnoreCase(WorkflowConstants.WEBSERVICETASK_TYPE)) {
            WebApiBlock b = (WebApiBlock) project.getWorkflowById(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setDescription((String) block.getAttributes().get(DESCRIPTION));
            b.setMethod((String) block.getAttributes().get(METHOD));
            b.setUrl((String) block.getAttributes().get(URL));
            b.setInputs((HashMap<String, String>) block.getAttributes().get(INPUTS));
            b.setOutputs((HashMap<String, String>) block.getAttributes().get(OUTPUTS));
            project.addBlockToWorkflow(b);
        }


    }

    private ClassModel getClassModel(String id, String name, String script) {
        ClassModel c = new ClassModel();
        c.setClass(true);
        c.setIdClass(id);
        c.setName(name);
        c.setClassVisibility("public");
        List<String> imports = new ArrayList<>();
        imports.add("io.je.utilities.logger.JELogger");
        imports.add("java.lang.*");
        imports.add("java.util.*");
        imports.add("java.sql.*");
        imports.add("javax.sql.*");
        c.setImports(imports);
        MethodModel m = new MethodModel();
        m.setMethodName("executeScript");
        m.setReturnType("VOID");
        m.setMethodScope("STATIC");
        m.setCode(script);
        m.setMethodVisibility("PUBLIC");
        List<MethodModel> methodModels = new ArrayList<>();
        methodModels.add(m);
        c.setMethods(methodModels);
        return c;
    }

    public void addBpmn(String projectId, String workflowId, String bpmn) throws ProjectNotFoundException, ConfigException {
    	ConfigurationService.checkConfig();
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

    public void updateWorkflow(String projectId, String workflowId, WorkflowModel m) throws WorkflowNotFoundException, ProjectNotFoundException, ConfigException {
    	ConfigurationService.checkConfig();
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


    public void removeWorkflows(String projectId, List<String> ids) throws ProjectNotFoundException, ConfigException {
    	ConfigurationService.checkConfig();
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

        JELogger.trace(" Removing workflows " + ids + " in project id = " + projectId);
        for(String id: ids) {
            try {
                removeWorkflow(projectId, id);
            }
            catch (WorkflowNotFoundException | InterruptedException | JERunnerErrorException | ExecutionException e) {
                JELogger.error(WorkflowService.class, " Error deleting a workflow: " + Arrays.toString(e.getStackTrace()));
            }
        }
    }
}
