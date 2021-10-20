package io.je.project.services;

import blocks.basic.*;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.InclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.ErrorBoundaryEvent;
import blocks.events.MessageEvent;
import blocks.events.SignalEvent;
import blocks.events.TimerEvent;
import builder.WorkflowBuilder;
import io.je.classbuilder.models.ClassDefinition;
import io.je.project.beans.JEProject;
import io.je.project.config.LicenseProperties;
import io.je.project.repository.WorkflowRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.Timers;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.EventType;
import io.je.utilities.models.WorkflowBlockModel;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.ruleutils.OperationStatusDetails;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.string.StringUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static io.je.utilities.constants.JEMessages.THREAD_INTERRUPTED_WHILE_EXECUTING;
import static io.je.utilities.constants.WorkflowConstants.*;

@Service
public class WorkflowService {



    @Autowired
    WorkflowRepository workflowRepository;

    @Autowired
    EventService eventService;

    @Autowired
    ClassService classService;

    /*
     * Add a workflow to a project
     * */
    @Async
    public void addWorkflow(io.je.utilities.models.WorkflowModel m) throws ProjectNotFoundException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(m.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JEWorkflow wf = new JEWorkflow();
        wf.setJobEngineElementID(m.getId());
        wf.setJobEngineProjectID(m.getProjectId());
        wf.setJobEngineElementName(m.getName());
        wf.setDescription(m.getDescription());
        wf.setJeObjectLastUpdate(LocalDateTime.now());
        wf.setJeObjectCreationDate(LocalDateTime.now());
        wf.setJeObjectCreatedBy(m.getCreatedBy());
        wf.setJeObjectModifiedBy(m.getModifiedBy());
        wf.setEnabled(m.isEnabled());
        if (m.isOnProjectBoot()) {
            JEWorkflow startupWorkflow = project.getStartupWorkflow();
            if (startupWorkflow != null) {
                startupWorkflow.setOnProjectBoot(false);
                workflowRepository.save(startupWorkflow);
            }
            wf.setOnProjectBoot(true);
        }

        JELogger.debug("[projectId =" + m.getProjectId() + " ][workflowId = " +
                        wf.getJobEngineElementID() + "]" + JEMessages.ADDING_WF,
                LogCategory.DESIGN_MODE, m.getProjectId(), LogSubModule.WORKFLOW, m.getId());
        project.addWorkflow(wf);
        workflowRepository.save(wf);
    }

    /*
     * Remove a workflow from a project
     * */

    public void removeWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, WorkflowNotFoundException, LicenseNotActiveException, WorkflowException {
        LicenseProperties.checkLicenseIsActive();


        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }

        JELogger.debug("[projectId =" + projectId + " ][workflowId = " + workflowId + "]" + JEMessages.REMOVING_WF,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.WORKFLOW, workflowId);
        String wfName = project.getWorkflowByIdOrName(workflowId).getJobEngineElementName().trim();
        //delete workflow block names
        Enumeration<String> blockIds = project.getWorkflowByIdOrName(workflowId).getAllBlocks().keys();
        while (blockIds.hasMoreElements()) {
            project.removeBlockName(blockIds.nextElement());
        }
        try {
            JERunnerAPIHandler.deleteWorkflow(projectId, wfName);
        }
        catch(JERunnerErrorException e) {
            throw new WorkflowException(JEMessages.DELETE_WORKFLOW_FAILED);
        }
        workflowRepository.deleteById(workflowId);
        project.removeWorkflow(workflowId);
    }

    /*
     * Add a workflow block to a workflow
     * */
    public String addWorkflowBlock(WorkflowBlockModel block) throws ProjectNotFoundException, WorkflowNotFoundException, InvalidSequenceFlowException, WorkflowBlockNotFound, EventException, ConfigException, WorkflowBlockException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = ProjectService.getProjectById(block.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(block.getWorkflowId())) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug("[projectId =" + block.getProjectId() + " ][workflowId = " +
                        block.getWorkflowId() + "]" + JEMessages.ADDING_WF_BLOCK + "" +
                        " id = " + block.getId(),
                LogCategory.DESIGN_MODE, block.getProjectId(),
                LogSubModule.WORKFLOW, block.getWorkflowId());
        project.getWorkflowByIdOrName(block.getWorkflowId()).setStatus(JEWorkflow.IDLE);
        String generatedBlockName = "";
        if (!block.getType().equalsIgnoreCase(SEQ_FLOW_TYPE)) {
            generatedBlockName = project.generateUniqueBlockName((String) block.getAttributes().get(NAME));
            block.getAttributes().put(NAME, generatedBlockName);
            project.addBlockName(block.getId(), generatedBlockName);
        }

        if (block.getType().equalsIgnoreCase(WorkflowConstants.START_TYPE)) {
            StartBlock b = new StartBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {
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
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EVENTGATEWAY_TYPE)) {
            EventGatewayBlock b = new EventGatewayBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGEINTERMEDIATECATCHEVENT_TYPE)) {
            MessageEvent b = new MessageEvent();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.MESSAGE_EVENT.toString());
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
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.SIGNAL_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setThrowSignal(false);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNAL_THROW_EVENT_TYPE)) {
            SignalEvent b = new SignalEvent();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.SIGNAL_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setThrowSignal(true);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EXCLUSIVEGATEWAY_TYPE)) {
            ExclusiveGatewayBlock b = new ExclusiveGatewayBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SCRIPTTASK_TYPE)) {
            ScriptBlock b = new ScriptBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setScript((String) block.getAttributes().get(SCRIPT));
            b.setTimeout((Integer) block.getAttributes().get(TIMEOUT));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.CALLACTIVITYTASK_TYPE)) {
            SubProcessBlock b = new SubProcessBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.PARALLELGATEWAY_TYPE)) {
            ParallelGatewayBlock b = new ParallelGatewayBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.INCLUSIVEGATEWAY_TYPE)) {
            InclusiveGatewayBlock b = new InclusiveGatewayBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DATETIMEREVENT)) {
            TimerEvent b = new TimerEvent();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setTimeDate((String) block.getAttributes().get(TIMEDATE));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setTimer(Timers.DATE_TIME);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.CYCLETIMEREVENT)) {
            TimerEvent b = new TimerEvent();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setTimeCycle((String) block.getAttributes().get(TIMECYCLE));
            b.setEndDate((String) block.getAttributes().get(ENDDATE));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setTimer(Timers.CYCLIC);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DURATIONTIMEREVENT)) {
            TimerEvent b = new TimerEvent();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setTimeDuration((String) block.getAttributes().get(DURATION));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setTimer(Timers.DELAY);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DBREADSERVICETASK_TYPE)) {
            DBReadBlock b = new DBReadBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DBWRITESERVICETASK_TYPE)) {
            DBWriteBlock b = new DBWriteBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(DBEDITSERVICETASK_TYPE)) {
            DBEditBlock b = new DBEditBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MAILSERVICETASK_TYPE)) {
            MailBlock b = new MailBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.WEBSERVICETASK_TYPE)) {
            WebApiBlock b = new WebApiBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setDescription((String) block.getAttributes().get(DESCRIPTION));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.INFORMSERVICETASK_TYPE)) {
            InformBlock b = new InformBlock();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(BOUNDARYEVENT_TYPE)) {
            ErrorBoundaryEvent b = new ErrorBoundaryEvent();
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setDescription((String) block.getAttributes().get(DESCRIPTION));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SEQ_FLOW_TYPE)) {
            addSequenceFlow(block.getProjectId(), block.getWorkflowId(),
                    (String) block.getAttributes().get(SOURCE_REF), (String) block.getAttributes().get(TARGET_REF),
                    (String) block.getAttributes().get(CONDITION));
        } else {
            throw new WorkflowBlockNotFound(JEMessages.WORKFLOW_BLOCK_NOT_FOUND);
        }
        workflowRepository.save(project.getWorkflowByIdOrName(block.getWorkflowId()));
        return generatedBlockName;
    }


    /*
     * Delete a workflow block
     * */
    public void deleteWorkflowBlock(String projectId, String workflowId, String blockId) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException, ConfigException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug("[projectId =" + projectId + " ][workflowId = " + workflowId + "]" + "[blockId = " + blockId + "]" +
                        JEMessages.DELETING_WF_BLOCK,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        project.deleteWorkflowBlock(workflowId, blockId);
        workflowRepository.deleteById(workflowId);
    }

    /*
     * Delete a Sequence flow
     * */
    public void deleteSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException, ConfigException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug("[projectId =" + projectId + " ][workflowId = " +
                        workflowId + "]" + JEMessages.DELETING_SEQUENCE_FLOW + sourceRef + " to  " + targetRef + " in workflow id = " + workflowId,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        project.deleteWorkflowSequenceFlow(workflowId, sourceRef, targetRef);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));

    }

    /*
     * Add a Sequence flow
     * */
    @Async
    public void addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef, String condition) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException, ConfigException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug("[projectId =" + projectId + " ][workflowId = " + workflowId + "]" +
                        JEMessages.ADDING_SEQUENCE_FLOW + sourceRef + " to  " + targetRef + " in workflow id = " + workflowId,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        project.addWorkflowSequenceFlow(workflowId, sourceRef, targetRef, condition);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));

    }

    /*
     * Build a workflow
     * */
     @Async
    public CompletableFuture<OperationStatusDetails> buildWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, LicenseNotActiveException{
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        OperationStatusDetails result = new OperationStatusDetails(workflowId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        if(!project.workflowExists(workflowId)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_NOT_FOUND);
            return CompletableFuture.completedFuture(result);
        }

        JEWorkflow workflow = project.getWorkflowByIdOrName(workflowId);
        if(!workflow.isEnabled()) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_IS_DISABLED);
            return CompletableFuture.completedFuture(result);
        }
        JELogger.info("[projectId =" + projectId + " ][workflowId = " + workflowId + "]" + JEMessages.BUILDING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);

        if (project.workflowHasError(workflow) || !WorkflowBuilder.buildWorkflow(workflow)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_BUILD_ERROR + " " + workflow.getJobEngineElementName());
            //throw new WorkflowBuildException(JEMessages.WORKFLOW_BUILD_ERROR + " " + workflow.getJobEngineElementName());
        }
        result.setItemName(workflow.getJobEngineElementName());
        result.setItemId(workflowId);
        return CompletableFuture.completedFuture(result);
    }

    /*
     * Build all workflow
     * */
    @Async
    public CompletableFuture<List<OperationStatusDetails>> buildWorkflows(String projectId, List<String> ids) throws ProjectNotFoundException,  LicenseNotActiveException, WorkflowNotFoundException, WorkflowException {
        LicenseProperties.checkLicenseIsActive();
		System.out.println(">>>> building wfs : "+ LocalDateTime.now() );

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JELogger.debug("[projectId =" + projectId + " ]" + JEMessages.BUILDING_WFS,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, null);

        List<OperationStatusDetails> results = new ArrayList<>();
            if (ids == null) {
                for (JEWorkflow wf : project.getWorkflows().values()) {
                    try {
                        results.add(buildWorkflow(projectId, wf.getJobEngineElementID()).get());
                    }
                    catch (Exception e) {
                        results.add(OperationStatusDetails.getResultDetails(wf.getJobEngineElementID(), false, THREAD_INTERRUPTED_WHILE_EXECUTING,
                                wf.getJobEngineElementName()));
                    }
                }
            } else {
                for (String id : ids) {
                    try {
                        results.add(buildWorkflow(projectId, id).get());
                    }
                    catch (Exception e) {
                        results.add(OperationStatusDetails.getResultDetails(id, false, THREAD_INTERRUPTED_WHILE_EXECUTING,
                                project.getWorkflowByIdOrName(id).getJobEngineElementName()));
                    }
                }
            }


        return CompletableFuture.completedFuture(results);

    }

    /*
     * Run a workflow
     * */
    @Async
    public CompletableFuture<OperationStatusDetails> runWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        OperationStatusDetails result = new OperationStatusDetails(workflowId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_NOT_FOUND);

            return CompletableFuture.completedFuture(result);
        }

        JEWorkflow wf = project.getWorkflowByIdOrName(workflowId);
        result.setItemName(wf.getJobEngineElementName());

        if (wf.getStatus().equals(JEWorkflow.BUILT)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_NEEDS_BUILD);
            return CompletableFuture.completedFuture(result);
        }

        if (wf.getStatus().equals(JEWorkflow.RUNNING)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_ALREADY_RUNNING);
            return CompletableFuture.completedFuture(result);
        }

        JELogger.info("[projectId =" + projectId + " ][workflowId = " +
                        workflowId + "]" + JEMessages.RUNNING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        try {
            WorkflowBuilder.runWorkflow(projectId, project.getWorkflowByIdOrName(workflowId).getJobEngineElementName().trim());
            wf.setStatus(JEWorkflow.RUNNING);
            result.setItemName(wf.getJobEngineElementName());
            result.setItemId(workflowId);
        }
        catch (WorkflowRunException e) {
            result.setOperationSucceeded(false);
            result.setOperationError(e.getMessage());
        }

        return CompletableFuture.completedFuture(result);
    }


    /*
     *
     * Update workflow block
     * */
    public void updateWorkflowBlock(WorkflowBlockModel block) throws WorkflowBlockNotFound, WorkflowNotFoundException, ProjectNotFoundException,    EventException, ConfigException, WorkflowBlockException, ClassLoadException,  AddClassException,   LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(block.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        if (!project.workflowExists(block.getWorkflowId())) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }

        if (!project.getWorkflowByIdOrName(block.getWorkflowId()).blockExists(block.getId())) {
            throw new WorkflowBlockNotFound(JEMessages.WORKFLOW_BLOCK_NOT_FOUND);
        }
        JEWorkflow wf = project.getWorkflowByIdOrName(block.getWorkflowId());
        String oldWorkflowBlockName = wf.getBlockById(block.getId()).getJobEngineElementName();
        if (!oldWorkflowBlockName.equals(block.getAttributes().get(NAME))) {
            if (project.blockNameExists((String) block.getAttributes().get(NAME))) {
                throw new WorkflowBlockException(JEMessages.BLOCK_NAME_CAN_T_BE_UPDATED_BECAUSE_IT_ALREADY_EXISTS);
            } else {
                project.removeBlockName(block.getId());
                project.addBlockName(block.getId(), (String) block.getAttributes().get(NAME));
            }
        }

        JELogger.debug(JEMessages.UPDATING_A_WORKFLOW_BLOCK_WITH_ID + " = " +
                        block.getId() + " in workflow with id = " + block.getWorkflowId(),
                LogCategory.DESIGN_MODE, block.getProjectId(),
                LogSubModule.WORKFLOW, block.getWorkflowId());

        wf.setStatus(JEWorkflow.IDLE);
        if (block.getType().equalsIgnoreCase(WorkflowConstants.START_TYPE)) {
            StartBlock b = (StartBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setEventId(null);
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {
                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.START_WORKFLOW.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            else if (WorkflowConstants.DATETIMEREVENT.equals(block.getAttributes().get(WorkflowConstants.EVENT_TYPE))) {
                TimerEvent timerEvent = new TimerEvent();
                timerEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
                timerEvent.setTimeDate((String) block.getAttributes().get(ENDDATE));
                timerEvent.setTimer(Timers.DATE_TIME);
                timerEvent.setTimeDuration(null);
                timerEvent.setEndDate(null);
                timerEvent.setTimeCycle(null);
                timerEvent.setOccurrences(-1);
                b.setTimerEvent(timerEvent);
            }
            else if (WorkflowConstants.DURATIONTIMEREVENT.equals(block.getAttributes().get(WorkflowConstants.EVENT_TYPE))) {
                TimerEvent timerEvent = new TimerEvent();
                timerEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
                timerEvent.setTimeDuration((String) block.getAttributes().get(TIMECYCLE));
                timerEvent.setTimeDate((String) block.getAttributes().get(ENDDATE));
                timerEvent.setTimeCycle(null);
                timerEvent.setEndDate(null);
                timerEvent.setTimer(Timers.DELAY);
                timerEvent.setOccurrences(-1);
                b.setTimerEvent(timerEvent);
            }
            else if (WorkflowConstants.CYCLETIMEREVENT.equals(block.getAttributes().get(WorkflowConstants.EVENT_TYPE))) {
                TimerEvent timerEvent = new TimerEvent();
                timerEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
                if( block.getAttributes().get(NBOCCURENCES) != null) {
                    timerEvent.setOccurrences((Integer) block.getAttributes().get(NBOCCURENCES));
                }
                timerEvent.setTimeCycle((String) block.getAttributes().get(TIMECYCLE));
                timerEvent.setTimer(Timers.CYCLIC);
                timerEvent.setEndDate((String) block.getAttributes().get(ENDDATE));
                timerEvent.setTimeDuration(null);

                b.setTimerEvent(timerEvent);
            }

            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.END_TYPE)) {
            EndBlock b = (EndBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EVENTGATEWAY_TYPE)) {
            EventGatewayBlock b = (EventGatewayBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGEINTERMEDIATECATCHEVENT_TYPE)) {
            MessageEvent b = (MessageEvent) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.MESSAGE_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            } else {
                b.setEventId(null);
            }
            b.setThrowMessage(false);
            project.addBlockToWorkflow(b);

        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGE_THROW_EVENT_TYPE)) {
            MessageEvent b = (MessageEvent) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.MESSAGE_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            } else {
                b.setEventId(null);
            }
            b.setThrowMessage(true);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNALINTERMEDIATECATCHEVENT_TYPE)) {
            SignalEvent b = (SignalEvent) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.SIGNAL_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            } else {
                b.setEventId(null);
            }
            b.setThrowSignal(false);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SIGNAL_THROW_EVENT_TYPE)) {
            SignalEvent b = (SignalEvent) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {

                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.SIGNAL_EVENT.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            } else {
                b.setEventId(null);
            }
            b.setThrowSignal(true);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EXCLUSIVEGATEWAY_TYPE)) {
            ExclusiveGatewayBlock b = (ExclusiveGatewayBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SCRIPTTASK_TYPE)) {
            if (StringUtilities.isEmpty((String) block.getAttributes().get(SCRIPT))) {
                throw new WorkflowBlockException(JEMessages.EMPTY_SCRIPT);
            }
            ScriptBlock b = (ScriptBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            ArrayList<String> imports = (ArrayList) block.getAttributes().get(IMPORTS);
            b.setScript((String) block.getAttributes().get(SCRIPT));
            b.setTimeout((Integer) block.getAttributes().get(TIMEOUT));
            ClassDefinition c = classService.getScriptTaskClassModel(b.getJobEngineElementID(), project.getWorkflowByIdOrName(block.getWorkflowId()).getJobEngineElementName() + b.getJobEngineElementName(), b.getScript());
            c.setImports(imports);
            //True to send directly to JERunner
            classService.addClass(c, true, true);
            //JEClassLoader.generateScriptTaskClass(b.getName(), b.getScript());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.PARALLELGATEWAY_TYPE)) {
            ParallelGatewayBlock b = (ParallelGatewayBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.INCLUSIVEGATEWAY_TYPE)) {
            InclusiveGatewayBlock b = (InclusiveGatewayBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DATETIMEREVENT)) {
            TimerEvent b = (TimerEvent) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setTimeDate((String) block.getAttributes().get(ENDDATE));
            b.setTimer(Timers.DATE_TIME);
            b.setTimeDuration(null);
            b.setEndDate(null);
            b.setTimeCycle(null);
            b.setOccurrences(-1);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.CYCLETIMEREVENT)) {
            TimerEvent b = (TimerEvent) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if( block.getAttributes().get(NBOCCURENCES) != null) {
                b.setOccurrences((Integer) block.getAttributes().get(NBOCCURENCES));
            }
            b.setTimeCycle((String) block.getAttributes().get(TIMECYCLE));
            //b.setEndDate((String) block.getAttributes().get(ENDDATE));
            b.setTimer(Timers.CYCLIC);
            b.setEndDate((String) block.getAttributes().get(ENDDATE));
            b.setTimeDuration(null);

            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DURATIONTIMEREVENT)) {
            TimerEvent b = (TimerEvent) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setTimeDuration((String) block.getAttributes().get(TIMECYCLE));
            b.setTimeDate((String) block.getAttributes().get(ENDDATE));
            b.setTimeCycle(null);
            b.setEndDate(null);
            b.setOccurrences(-1);
            b.setTimer(Timers.DELAY);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DBREADSERVICETASK_TYPE)) {
            DBReadBlock b = (DBReadBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setRequest((String) block.getAttributes().get(REQUEST));
            b.setDatabaseId((String) block.getAttributes().get(DATABASE_ID));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(DBEDITSERVICETASK_TYPE)) {
            DBEditBlock b = (DBEditBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setRequest((String) block.getAttributes().get("request"));
            b.setDatabaseId((String) block.getAttributes().get("databaseId"));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(DBWRITESERVICETASK_TYPE)) {
            DBWriteBlock b = (DBWriteBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setRequest((String) block.getAttributes().get("request"));
            b.setDatabaseId((String) block.getAttributes().get("databaseId"));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MAILSERVICETASK_TYPE)) {
            MailBlock b = (MailBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));

            b.setiPort((Integer) block.getAttributes().get(PORT));
            b.setStrSenderAddress((String) block.getAttributes().get(SENDER_ADDRESS));
            b.setiSendTimeOut((Integer) block.getAttributes().get(SEND_TIME_OUT));
            b.setLstRecieverAddress((List<String>) block.getAttributes().get(RECEIVER_ADDRESS));
            b.setEmailMessage((HashMap<String, String>) block.getAttributes().get(EMAIL_MESSAGE));
            b.setStrSMTPServer((String) block.getAttributes().get(SMTP_SERVER));
            if ((boolean) block.getAttributes().get(USE_DEFAULT_CREDENTIALS)) {
                b.setbUseDefaultCredentials((boolean) block.getAttributes().get(USE_DEFAULT_CREDENTIALS));
                b.setbEnableSSL((boolean) block.getAttributes().get(ENABLE_SSL));
            } else {
                b.setStrPassword((String) block.getAttributes().get(PASSWORD));
                b.setStrUserName((String) block.getAttributes().get(USERNAME));
                b.setbEnableSSL(false);
                b.setbUseDefaultCredentials(false);
            }
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.WEBSERVICETASK_TYPE)) {
            WebApiBlock b = (WebApiBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setDescription((String) block.getAttributes().get(DESCRIPTION));
            b.setMethod((String) block.getAttributes().get(METHOD));
            b.setUrl((String) block.getAttributes().get(URL));
            if (block.getAttributes().containsKey(BODY)) {
                b.setBody((String) block.getAttributes().get(BODY));
                b.setInputs(null);
            } else {
                b.setBody(null);
                b.setInputs((HashMap<String, ArrayList<Object>>) block.getAttributes().get(INPUTS));
            }

            b.setOutputs((HashMap<String, String>) block.getAttributes().get(OUTPUTS));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.INFORMSERVICETASK_TYPE)) {
            InformBlock b = (InformBlock) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setMessage((String) block.getAttributes().get(MESSAGE));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.CALLACTIVITYTASK_TYPE)) {
            if(project.isWorkflowEnabled((String) block.getAttributes().get(SUBWORKFLOWID))) {
                SubProcessBlock b = (SubProcessBlock) wf.getAllBlocks().get(block.getId());
                b.setJobEngineElementName((String) block.getAttributes().get(NAME));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setSubWorkflowId(project.getWorkflowByIdOrName((String) block.getAttributes().get(SUBWORKFLOWID)).getJobEngineElementName());
                b.setJobEngineElementID(block.getId());
                project.addBlockToWorkflow(b);
            }
            else {
                wf.setHasErrors(true);
                workflowRepository.save(wf);
                JELogger.debug( JEMessages.ERROR_WHILE_REFERENCING_A_DISABLED_WORKFLOW +
                                project.getWorkflowByIdOrName((String) block.getAttributes().get(SUBWORKFLOWID)).getJobEngineElementName(),
                        LogCategory.DESIGN_MODE, wf.getJobEngineProjectID(),
                        LogSubModule.WORKFLOW, wf.getJobEngineElementID());
                throw new WorkflowBlockException(JEMessages.ERROR_WHILE_REFERENCING_A_DISABLED_WORKFLOW + project.getWorkflowByIdOrName((String) block.getAttributes().get(SUBWORKFLOWID)).getJobEngineElementName());
            }
        } else if (block.getType().equalsIgnoreCase(BOUNDARYEVENT_TYPE)) {
            ErrorBoundaryEvent b = (ErrorBoundaryEvent) wf.getAllBlocks().get(block.getId());
            b.setJobEngineElementName((String) block.getAttributes().get(NAME));
            b.setDescription((String) block.getAttributes().get(DESCRIPTION));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            //b.setErrorRef((String) block.getAttributes().get(ERROR_REF));
            project.addBlockToWorkflow(b);
        }
        wf.setJeObjectLastUpdate(LocalDateTime.now());
        workflowRepository.save(wf);


    }


    /*
    * Add a scripted xml file to activiti engine
    * */
    public void addBpmn(String projectId, String workflowId, String bpmn) throws ProjectNotFoundException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JELogger.debug(JEMessages.ADDING_BPMN_SCRIPT,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        JEWorkflow wf = new JEWorkflow();
        wf.setJobEngineElementName(workflowId);
        wf.setJobEngineProjectID(projectId);
        wf.setJobEngineElementID(workflowId);
        wf.setBpmnPath(ConfigurationConstants.BPMN_PATH + wf.getJobEngineElementName().trim() + WorkflowConstants.BPMN_EXTENSION);
        wf.setIsScript(true);
        wf.setScript(bpmn);
        WorkflowBuilder.saveBpmn(wf, bpmn);
        project.addWorkflow(wf);
        workflowRepository.save(wf);

    }

    public void updateWorkflow(String projectId, String workflowId, WorkflowModel m) throws WorkflowNotFoundException, ProjectNotFoundException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JEWorkflow wf = project.getWorkflowByIdOrName(workflowId);
        JELogger.debug("[projectId =" + projectId + " ][workflowId = " + workflowId + "]" + JEMessages.UPDATING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        if (m.getName() != null) {
            wf.setJobEngineElementName(m.getName());
        }

        if (m.isOnProjectBoot()) {
            JEWorkflow startupWorkflow = project.getStartupWorkflow();
            if (startupWorkflow != null) {
                startupWorkflow.setOnProjectBoot(false);
                workflowRepository.save(startupWorkflow);
            }
        }

        if(m.isEnabled() != wf.isEnabled()) {
            wf.setEnabled(m.isEnabled());
        }
        wf.setJeObjectLastUpdate(LocalDateTime.now());
        wf.setDescription(m.getDescription());
        wf.setJeObjectCreatedBy(m.getCreatedBy());
        wf.setJeObjectModifiedBy(m.getModifiedBy());
        wf.setOnProjectBoot(m.isOnProjectBoot());
        workflowRepository.save(wf);


    }

    public void setFrontConfig(String projectId, String workflowId, String config) throws ProjectNotFoundException, WorkflowNotFoundException, LicenseNotActiveException {

        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        project.getWorkflowByIdOrName(workflowId).setFrontConfig(config);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));

    }

    public void removeWorkflows(String projectId, List<String> ids) throws ProjectNotFoundException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        JELogger.debug("[projectId =" + projectId + " ]" + JEMessages.REMOVING_WFS,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, null);
        for (String id : ids) {
            try {
                removeWorkflow(projectId, id);
            } catch (Exception e) {
                JELogger.error(JEMessages.DELETE_WORKFLOW_FAILED + " id = " + id + " " + e.getMessage(),
                        LogCategory.DESIGN_MODE, projectId,
                        LogSubModule.WORKFLOW, id);
            }
        }
    }

    @Async
    public CompletableFuture<OperationStatusDetails> stopWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        OperationStatusDetails result = new OperationStatusDetails(workflowId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_NOT_FOUND);
            return CompletableFuture.completedFuture(result);
        }
        JEWorkflow wf = project.getWorkflowByIdOrName(workflowId);
        JELogger.debug("[projectId =" + projectId + " ][workflowId = " + workflowId + "]" + JEMessages.STOPPING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        try {
            result.setItemName(wf.getJobEngineElementName());
            result.setItemId(workflowId);
            JERunnerAPIHandler.deleteWorkflow(projectId, wf.getJobEngineElementName());
            wf.setStatus(JEWorkflow.IDLE);

        }
        catch(JERunnerErrorException e) {
            JELogger.debug("[projectId =" + projectId + " ][workflowId = " + workflowId + "]" + JEMessages.STOPPING_WF + e.getMessage(),
                    LogCategory.DESIGN_MODE, projectId,
                    LogSubModule.WORKFLOW, workflowId);
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.ERROR_STOPPING_WORKFLOW);
        }
        project.getWorkflowByIdOrName(workflowId).setStatus(JEWorkflow.IDLE);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));
        return CompletableFuture.completedFuture(result);
    }


    @Async
    public CompletableFuture<List<WorkflowModel>> getAllWorkflows(String projectId) throws LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();
        List<JEWorkflow> wfs = workflowRepository.findByJobEngineProjectID(projectId);
        List<WorkflowModel> modelList = new ArrayList<>();
        for (JEWorkflow wf : wfs) {
            modelList.add(JEWorkflow.mapJEWorkflowToModel(wf));
        }
        return CompletableFuture.completedFuture(modelList);

    }

    @Async
    public CompletableFuture<WorkflowModel> getWorkflow(String workflowId) throws LicenseNotActiveException, WorkflowNotFoundException {
        LicenseProperties.checkLicenseIsActive();
        try {
            JEWorkflow wf = workflowRepository.findById(workflowId).get();
            return CompletableFuture.completedFuture(JEWorkflow.mapJEWorkflowToModel(wf));
        } catch (Exception e) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }


    }

    @Async
    public void deleteAll(String projectId) {
        workflowRepository.deleteByJobEngineProjectID(projectId);

    }

    public ConcurrentHashMap<String, JEWorkflow> getAllJEWorkflows(String projectId) throws LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        List<JEWorkflow> workflows = workflowRepository.findByJobEngineProjectID(projectId);
        ConcurrentHashMap<String, JEWorkflow> map = new ConcurrentHashMap<String, JEWorkflow>();
        for (JEWorkflow workflow : workflows) {
            map.put(workflow.getJobEngineElementID(), workflow);
        }
        return map;
    }

    @Async
    public CompletableFuture<List<OperationStatusDetails>> runWorkflows(String projectId, List<String> ids) throws LicenseNotActiveException, ProjectNotFoundException {

        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        List<OperationStatusDetails> results = new ArrayList<>();
        if(ids == null) {
            for (JEWorkflow wf : project.getWorkflows().values()) {
                try {
                    results.add(runWorkflow(projectId, wf.getJobEngineElementID()).get());
                }
                catch (Exception e) {
                    results.add(OperationStatusDetails.getResultDetails(wf.getJobEngineElementID(), false, THREAD_INTERRUPTED_WHILE_EXECUTING,
                            wf.getJobEngineElementName()));
                }
            }
        }

        else {
            for(String id: ids) {
                try {
                    results.add(runWorkflow(projectId, id).get());
                }
                catch (Exception e) {
                    results.add(OperationStatusDetails.getResultDetails(id, false, THREAD_INTERRUPTED_WHILE_EXECUTING,
                            project.getWorkflowByIdOrName(id).getJobEngineElementName()));
                }
            }
        }
        return CompletableFuture.completedFuture(results);
    }

    @Async
    public CompletableFuture<List<OperationStatusDetails>> stopWorkflows(String projectId, List<String> ids) throws LicenseNotActiveException, ProjectNotFoundException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        List<OperationStatusDetails> results = new ArrayList<>();
        if(ids == null) {
            for (JEWorkflow wf : project.getWorkflows().values()) {
                try {
                    results.add(stopWorkflow(projectId, wf.getJobEngineElementID()).get());
                }
                catch (Exception e) {
                    results.add(OperationStatusDetails.getResultDetails(wf.getJobEngineElementID(), false, THREAD_INTERRUPTED_WHILE_EXECUTING,
                            wf.getJobEngineElementName()));
                }
            }
        }

        else {
            for(String id: ids) {
                try {
                    results.add(stopWorkflow(projectId, id).get());
                }
                catch (Exception e) {

                    results.add(OperationStatusDetails.getResultDetails(id, false, THREAD_INTERRUPTED_WHILE_EXECUTING,
                            project.getWorkflowByIdOrName(id).getJobEngineElementName()));
                }
            }
        }
        return CompletableFuture.completedFuture(results);
    }

}
