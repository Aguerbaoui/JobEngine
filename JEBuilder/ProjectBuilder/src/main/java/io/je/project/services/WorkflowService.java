package io.je.project.services;

import blocks.WorkflowBlock;
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
import io.je.project.repository.LibraryRepository;
import io.je.project.repository.WorkflowRepository;
import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JELib;
import io.je.utilities.beans.Status;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.Timers;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.EventType;
import io.je.utilities.models.LibModel;
import io.je.utilities.models.WorkflowBlockModel;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.ruleutils.IdManager;
import io.je.utilities.ruleutils.OperationStatusDetails;
import io.siothconfig.SIOTHConfigUtility;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.network.AuthScheme;
import utils.string.StringUtilities;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static io.je.utilities.constants.ClassBuilderConfig.SCRIPTS_PACKAGE;
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

    @Autowired
    LibraryRepository libraryRepository;

    @Autowired
    @Lazy
    ProjectService projectService;

    /*
     * Add a workflow to a project
     */
    // @Async
    public void addWorkflow(WorkflowModel m)
            throws LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = ProjectService.getProjectById(m.getProjectId());
        JEWorkflow wf = mapWorkflowModelToJEWorkflow(m);
        wf.setJeObjectLastUpdate(Instant.now());
        wf.setJeObjectCreationDate(Instant.now());
        wf.setStatus(Status.NOT_BUILT);
        wf.setJobEngineProjectName(project.getProjectName());
        setProjectBootWorkflow(wf, project.getStartupWorkflow());
        JELogger.debug("[project=" + project.getProjectName() + " ][workflow = " +
                        wf.getJobEngineElementName() + "]" + JEMessages.ADDING_WF,
                LogCategory.DESIGN_MODE, m.getProjectId(), LogSubModule.WORKFLOW, m.getId());
        project.addWorkflow(wf);
        workflowRepository.save(wf);
    }

    //Set the project boot workflow
    public void setProjectBootWorkflow(JEWorkflow workflowToCheck, JEWorkflow currentStartupWorkflow) {
        if (workflowToCheck.isOnProjectBoot()) {
            if (currentStartupWorkflow != null) {
                currentStartupWorkflow.setOnProjectBoot(false);
                workflowRepository.save(currentStartupWorkflow);
            }
        }
    }
    /*
     * Remove a workflow from a project
     */

    public void removeWorkflow(String projectId, String workflowId)
            throws WorkflowNotFoundException, LicenseNotActiveException, WorkflowException {

        //check if license is valid
        LicenseProperties.checkLicenseIsActive();

        //get project
        JEProject project = ProjectService.getProjectById(projectId);

        //check if workflow exists
        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }

        //delete workflow from runner
        JEWorkflow wf = project.getWorkflowByIdOrName(workflowId);
        String wfName = wf.getJobEngineElementName().trim();
        JELogger.debug(
                "[project=" + project.getProjectName() + " ][workflow = " + wfName + "]" + JEMessages.REMOVING_WF,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.WORKFLOW, workflowId);
        try {
            JERunnerAPIHandler.deleteWorkflow(projectId, wfName);
        } catch (JERunnerErrorException e) {
            throw new WorkflowException(JEMessages.DELETE_WORKFLOW_FAILED);
        }

        // delete workflow block names
        Enumeration<String> blockIds = project.getWorkflowByIdOrName(workflowId).getAllBlocks().keys();
        while (blockIds.hasMoreElements()) {
            project.removeBlockName(blockIds.nextElement());
        }
        //delete script task blocks data (files/classes)
        for (WorkflowBlock b : wf.getAllBlocks().values()) {
            if (b instanceof ScriptBlock) {
                String scriptName = IdManager.getScriptTaskId(wf.getJobEngineElementName(),
                        b.getJobEngineElementName());
                wf.cleanUpScriptTaskBlock((ScriptBlock) b);
                classService.removeClass(scriptName);
            }
        }

        //delete workflow from db
        workflowRepository.deleteById(workflowId);

        //delete bpmn file from drive
        try {
            FileUtilities.deleteFileFromPath(wf.getBpmnPath());
        } catch (Exception e) {
            JELogger.error(JEMessages.FAILED_TO_DELETE_FILES, LogCategory.DESIGN_MODE, projectId, LogSubModule.WORKFLOW,
                    wf.getJobEngineElementID());
        }
        JELogger.info(JEMessages.WORKFLOW_DELETED_SUCCESSFULLY, LogCategory.DESIGN_MODE, projectId, LogSubModule.WORKFLOW,
                wf.getJobEngineElementID());

        //delete workflow from memory
        project.removeWorkflow(workflowId);
    }

    /*
     * Add a workflow block to a workflow
     */
    public String addWorkflowBlock(WorkflowBlockModel block)
            throws WorkflowNotFoundException,
            WorkflowBlockNotFound, EventException, LicenseNotActiveException, WorkflowBlockException {

        //check if license is valid
        LicenseProperties.checkLicenseIsActive();

        //get project
        JEProject project = ProjectService.getProjectById(block.getProjectId());
        JEWorkflow wf = project.getWorkflowByIdOrName(block.getWorkflowId());
        //check if workflow exists
        if (!project.workflowExists(block.getWorkflowId())) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }

        JELogger.debug("[project=" + project.getProjectName() + " ][workflow = " +
                        project.getWorkflowByIdOrName(block.getWorkflowId()).getJobEngineElementName() + "]"
                        + JEMessages.ADDING_WF_BLOCK + "" +
                        " id = " + block.getId(),
                LogCategory.DESIGN_MODE, block.getProjectId(),
                LogSubModule.WORKFLOW, block.getWorkflowId());

        //set status to not built
        project.getWorkflowByIdOrName(block.getWorkflowId()).setStatus(Status.NOT_BUILT);

        //generate block name
        String generatedBlockName = "";
        if (!block.getType().equalsIgnoreCase(SEQ_FLOW_TYPE)) {
            generatedBlockName = project.generateUniqueBlockName((String) block.getAttributes().get(NAME));
            block.getAttributes().put(NAME, generatedBlockName);
            project.addBlockName(block.getId(), generatedBlockName);
        }

        switch (block.getType().toLowerCase()) {
            case WorkflowConstants.START_TYPE: {
                project.addBlockToWorkflow(getStartBlock(project, wf, block, false));
                break;
            }
            case WorkflowConstants.END_TYPE: {
                project.addBlockToWorkflow(getEndBlock(project, wf,block, false));
                break;
            }
            case WorkflowConstants.EVENTGATEWAY_TYPE: {
                project.addBlockToWorkflow(getEventGatewayBlock(project, wf,block, false));
                break;
            }
            case WorkflowConstants.MESSAGEINTERMEDIATECATCHEVENT_TYPE: {
                project.addBlockToWorkflow(getMessageEvent(project, wf,block, false));
                break;
            }
            case WorkflowConstants.SIGNALINTERMEDIATECATCHEVENT_TYPE: {
                project.addBlockToWorkflow(getSignalEvent(project, wf,block, false, false));
                break;
            }
            case WorkflowConstants.SIGNAL_THROW_EVENT_TYPE: {
                project.addBlockToWorkflow(getSignalEvent(project, wf,block, true, false));
                break;
            }
            case WorkflowConstants.EXCLUSIVEGATEWAY_TYPE: {
                project.addBlockToWorkflow(getExclusiveGatewayBlock(project, wf,block, false));
                break;
            }
            case WorkflowConstants.SCRIPTTASK_TYPE: {
                try {
                    project.addBlockToWorkflow(getScriptBlock(project, wf,block, false));
                } catch (ClassLoadException | AddClassException | IOException | InterruptedException ignore) {
                }
                break;
            }
            case WorkflowConstants.CALLACTIVITYTASK_TYPE: {
                project.addBlockToWorkflow(getSubProcessBlock(project, wf,block, false));
                break;
            }
            case WorkflowConstants.PARALLELGATEWAY_TYPE: {
                project.addBlockToWorkflow(getParallelGatewayBlock(project, wf,block, false));
                break;
            }
            case WorkflowConstants.INCLUSIVEGATEWAY_TYPE: {
                project.addBlockToWorkflow(getInclusiveGatewayBlock(project, wf,block, false));
                break;
            }
            case WorkflowConstants.DATETIMEREVENT: {
                project.addBlockToWorkflow(getTimerEvent(project, wf,block, Timers.DATE_TIME, false));
                break;
            }
            case WorkflowConstants.CYCLETIMEREVENT: {
                project.addBlockToWorkflow(getTimerEvent(project, wf,block, Timers.CYCLIC, false));
                break;
            }
            case WorkflowConstants.DURATIONTIMEREVENT: {
                project.addBlockToWorkflow(getTimerEvent(project, wf,block, Timers.DELAY, false));
                break;
            }
            case WorkflowConstants.DBREADSERVICETASK_TYPE:
            case WorkflowConstants.DBWRITESERVICETASK_TYPE:
            case WorkflowConstants.DBEDITSERVICETASK_TYPE: {
                project.addBlockToWorkflow(getDatabaseBlock(project, wf,block, false));
                break;
            }
            case WorkflowConstants.MAILSERVICETASK_TYPE: {
                project.addBlockToWorkflow(getMailBlock(project, wf,block, false));
                break;
            }
            case WorkflowConstants.WEBSERVICETASK_TYPE: {
                project.addBlockToWorkflow(getWebApiBlock(project, wf,block, false));
                break;
            }
            case WorkflowConstants.INFORMSERVICETASK_TYPE: {
                project.addBlockToWorkflow(getInformBlock(project, wf,block, false));
                break;
            }
            case WorkflowConstants.BOUNDARYEVENT_TYPE: {
                project.addBlockToWorkflow(getErrorBoundaryEvent(project, wf,block, false));
                break;
            }
            case WorkflowConstants.SEQ_FLOW_TYPE: {
                addSequenceFlow(block.getProjectId(), block.getWorkflowId(),
                        (String) block.getAttributes().get(SOURCE_REF), (String) block.getAttributes().get(TARGET_REF),
                        (String) block.getAttributes().get(CONDITION));
                break;
            }
            default: {
                throw new WorkflowBlockNotFound(JEMessages.WORKFLOW_BLOCK_NOT_FOUND);
            }


        }
        workflowRepository.save(project.getWorkflowByIdOrName(block.getWorkflowId()));
        return generatedBlockName;
    }

    //get start block
    public StartBlock getStartBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update) throws EventException, LicenseNotActiveException {
        StartBlock startBlock = null;
        if (!update) {
            startBlock = new StartBlock();
            startBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            startBlock.setJobEngineProjectID(block.getProjectId());
            startBlock.setWorkflowId(block.getWorkflowId());
            startBlock.setJobEngineElementID(block.getId());
        }
        else {
            startBlock = (StartBlock) wf.getAllBlocks().get(block.getId());
            startBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            startBlock.setEventId(null);
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {
                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.START_WORKFLOW.toString());
                startBlock.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
            if (WorkflowConstants.DATETIMEREVENT.equalsIgnoreCase((String) block.getAttributes().get(WorkflowConstants.EVENT_TYPE))) {
                startBlock.setTimerEvent(getEmbeddedTimerEvent(block, Timers.DATE_TIME));
            }
            if (WorkflowConstants.DURATIONTIMEREVENT.equalsIgnoreCase((String) block.getAttributes().get(WorkflowConstants.EVENT_TYPE))) {
                startBlock.setTimerEvent(getEmbeddedTimerEvent(block, Timers.DELAY));
            }
            if (WorkflowConstants.CYCLETIMEREVENT.equalsIgnoreCase((String) block.getAttributes().get(WorkflowConstants.EVENT_TYPE))) {
                startBlock.setTimerEvent(getEmbeddedTimerEvent(block, Timers.CYCLIC));
            }
        }
        return startBlock;
    }

    //get end block
    public EndBlock getEndBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update)  {
        EndBlock endBlock = null;
        if (!update) {
            endBlock = new EndBlock();
            endBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            endBlock.setJobEngineProjectID(block.getProjectId());
            endBlock.setWorkflowId(block.getWorkflowId());
            endBlock.setJobEngineElementID(block.getId());
        }
        else {
            endBlock = (EndBlock) wf.getAllBlocks().get(block.getId());
            endBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            endBlock.setEventId(null);
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {
                endBlock.setEventId((String) block.getAttributes().get(EVENT_ID));
            }
        }
        return endBlock;
    }

    //get event based gateway block
    public EventGatewayBlock getEventGatewayBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update){
        EventGatewayBlock eventGatewayBlock = null;
        if (!update) {
            eventGatewayBlock = new EventGatewayBlock();
            eventGatewayBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            eventGatewayBlock.setJobEngineProjectID(block.getProjectId());
            eventGatewayBlock.setWorkflowId(block.getWorkflowId());
            eventGatewayBlock.setJobEngineElementID(block.getId());
        }
        else {
            eventGatewayBlock = (EventGatewayBlock) wf.getAllBlocks().get(block.getId());
            eventGatewayBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
        }
        return eventGatewayBlock;
    }

    //get message catch event
    public MessageEvent getMessageEvent(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update) throws EventException, LicenseNotActiveException {
        MessageEvent messageEvent = null;
        if (!update) {
            messageEvent = new MessageEvent();
            messageEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
            messageEvent.setJobEngineProjectID(block.getProjectId());
            messageEvent.setWorkflowId(block.getWorkflowId());
            messageEvent.setJobEngineElementID(block.getId());
        }
        else {
            messageEvent = (MessageEvent) wf.getAllBlocks().get(block.getId());
            messageEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {
                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.MESSAGE_EVENT.toString());
                messageEvent.setEventId((String) block.getAttributes().get(EVENT_ID));
            } else {
                messageEvent.setEventId(null);
            }
        }
        messageEvent.setThrowMessage(false);
        return messageEvent;
    }

    //get signal event ( throw and catch)
    public SignalEvent getSignalEvent(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean throwSignal, boolean update) throws EventException, LicenseNotActiveException {
        SignalEvent signalEvent = null;
        if (!update) {
            signalEvent = new SignalEvent();
            signalEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
            signalEvent.setJobEngineProjectID(block.getProjectId());
            signalEvent.setWorkflowId(block.getWorkflowId());
            signalEvent.setJobEngineElementID(block.getId());
        }
        else {
            signalEvent = (SignalEvent) wf.getAllBlocks().get(block.getId());
            signalEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
            if (!StringUtilities.isEmpty((String) block.getAttributes().get(EVENT_ID))) {
                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID), EventType.SIGNAL_EVENT.toString());
                signalEvent.setEventId((String) block.getAttributes().get(EVENT_ID));
            } else {
                signalEvent.setEventId(null);
            }
        }
        signalEvent.setThrowSignal(throwSignal);
        return signalEvent;
    }

    //get exclusive gateway
    public ExclusiveGatewayBlock getExclusiveGatewayBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update){
        ExclusiveGatewayBlock exclusiveGatewayBlock = null;
        if (!update) {
            exclusiveGatewayBlock = new ExclusiveGatewayBlock();
            exclusiveGatewayBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            exclusiveGatewayBlock.setJobEngineProjectID(block.getProjectId());
            exclusiveGatewayBlock.setWorkflowId(block.getWorkflowId());
            exclusiveGatewayBlock.setJobEngineElementID(block.getId());
        }
        else {
            exclusiveGatewayBlock = (ExclusiveGatewayBlock) wf.getAllBlocks().get(block.getId());
            exclusiveGatewayBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
        }
        return exclusiveGatewayBlock;
    }

    //get script block
    public ScriptBlock getScriptBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update) throws ClassLoadException, AddClassException, IOException, InterruptedException {
        ScriptBlock scriptBlock = null;
        if (!update) {
            scriptBlock = new ScriptBlock();
            scriptBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            scriptBlock.setJobEngineProjectID(block.getProjectId());
            scriptBlock.setWorkflowId(block.getWorkflowId());
            scriptBlock.setJobEngineElementID(block.getId());
        }
        else {
            scriptBlock = (ScriptBlock) wf.getAllBlocks().get(block.getId());
            String name = IdManager.getScriptTaskId(wf.getJobEngineElementName(), scriptBlock.getJobEngineElementName());
            if (!scriptBlock.getJobEngineElementName().equals((String) block.getAttributes().get(NAME))) {
                scriptBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
                classService.removeClass(name);
                name = IdManager.getScriptTaskId(wf.getJobEngineElementName(), scriptBlock.getJobEngineElementName());
            }
            ArrayList<String> imports = (ArrayList<String>) block.getAttributes().get(IMPORTS);
            scriptBlock.setScript((String) block.getAttributes().get(SCRIPT));
            scriptBlock.setTimeout((Integer) block.getAttributes().get(TIMEOUT));
            scriptBlock.setScriptPath(name);
            ClassDefinition c = classService.getScriptTaskClassModel(scriptBlock.getScript());
            c.setClassId(name);
            // update to an existent class, no longer temporary class
            c.setImports(imports);
            c.setName(name);
            // True to send directly to JERunner
            try {
                classService.compileCode(c, SCRIPTS_PACKAGE);
            } catch (Exception e) {
                wf.cleanUpScriptTaskBlock(scriptBlock);
                throw e;
            }
        }
        return scriptBlock;
    }

    //get subprocess block
    public SubProcessBlock getSubProcessBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update) throws WorkflowBlockException {
        SubProcessBlock subProcessBlock = null;
        if (!update) {
            subProcessBlock = new SubProcessBlock();
            subProcessBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            subProcessBlock.setJobEngineProjectID(block.getProjectId());
            subProcessBlock.setWorkflowId(block.getWorkflowId());
            subProcessBlock.setJobEngineElementID(block.getId());
        }
        else {
            if (project.isWorkflowEnabled((String) block.getAttributes().get(SUBWORKFLOWID))) {
                subProcessBlock = (SubProcessBlock) wf.getAllBlocks().get(block.getId());
                subProcessBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
                subProcessBlock.setJobEngineProjectID(block.getProjectId());
                subProcessBlock.setWorkflowId(block.getWorkflowId());
                subProcessBlock.setSubWorkflowId(project.getWorkflowByIdOrName((String) block.getAttributes().get(SUBWORKFLOWID))
                        .getJobEngineElementName());
                subProcessBlock.setJobEngineElementID(block.getId());
            } else {
                wf.setHasErrors(true);
                workflowRepository.save(wf);
                JELogger.debug(JEMessages.ERROR_WHILE_REFERENCING_A_DISABLED_WORKFLOW +
                                project.getWorkflowByIdOrName((String) block.getAttributes().get(SUBWORKFLOWID))
                                        .getJobEngineElementName(),
                        LogCategory.DESIGN_MODE, wf.getJobEngineProjectID(),
                        LogSubModule.WORKFLOW, wf.getJobEngineElementID());
                throw new WorkflowBlockException(JEMessages.ERROR_WHILE_REFERENCING_A_DISABLED_WORKFLOW
                        + project.getWorkflowByIdOrName((String) block.getAttributes().get(SUBWORKFLOWID))
                        .getJobEngineElementName());
            }
        }
        return subProcessBlock;
    }

    //get parallel gateway
    public ParallelGatewayBlock getParallelGatewayBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update){
        ParallelGatewayBlock parallelGatewayBlock = null;
        if (!update) {
            parallelGatewayBlock = new ParallelGatewayBlock();
            parallelGatewayBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            parallelGatewayBlock.setJobEngineProjectID(block.getProjectId());
            parallelGatewayBlock.setWorkflowId(block.getWorkflowId());
            parallelGatewayBlock.setJobEngineElementID(block.getId());
        }
        else {
            parallelGatewayBlock  = (ParallelGatewayBlock) wf.getAllBlocks().get(block.getId());
            parallelGatewayBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
        }
        return parallelGatewayBlock;
    }

    //get inclusive gateway
    public InclusiveGatewayBlock getInclusiveGatewayBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update) throws EventException, LicenseNotActiveException {
        InclusiveGatewayBlock inclusiveGatewayBlock = null;
        if (!update) {
            inclusiveGatewayBlock = new InclusiveGatewayBlock();
            inclusiveGatewayBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            inclusiveGatewayBlock.setJobEngineProjectID(block.getProjectId());
            inclusiveGatewayBlock.setWorkflowId(block.getWorkflowId());
            inclusiveGatewayBlock.setJobEngineElementID(block.getId());
        }
        else {
            inclusiveGatewayBlock  = (InclusiveGatewayBlock) wf.getAllBlocks().get(block.getId());
            inclusiveGatewayBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
        }
        return inclusiveGatewayBlock;
    }

    //get timer event
    public TimerEvent getTimerEvent(JEProject project, JEWorkflow wf, WorkflowBlockModel block, Timers timerType, boolean update) throws EventException, LicenseNotActiveException {
        TimerEvent timerEvent = null;
        if (!update) {
            timerEvent = new TimerEvent();
            timerEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
            timerEvent.setJobEngineProjectID(block.getProjectId());
            timerEvent.setWorkflowId(block.getWorkflowId());
            timerEvent.setJobEngineElementID(block.getId());
            timerEvent.setTimer(timerType);
        }
        else {
            timerEvent = (TimerEvent) wf.getAllBlocks().get(block.getId());
            timerEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
            setTimerEventSpecsFromModel(timerEvent, block);
        }

        return timerEvent;
    }

    public void setTimerEventSpecsFromModel(TimerEvent timerEvent, WorkflowBlockModel block) {
        if(timerEvent != null) {
            switch (timerEvent.getTimer()) {
                case DATE_TIME: {
                    timerEvent.setTimeDate((String) block.getAttributes().get(ENDDATE));
                    timerEvent.setTimeDuration(null);
                    timerEvent.setEndDate(null);
                    timerEvent.setTimeCycle(null);
                    timerEvent.setOccurrences(-1);
                }

                case DELAY: {
                    timerEvent.setTimeDuration((String) block.getAttributes().get(TIMECYCLE));
                    timerEvent.setTimeDate((String) block.getAttributes().get(ENDDATE));
                    timerEvent.setTimeCycle(null);
                    timerEvent.setEndDate(null);
                    timerEvent.setOccurrences(-1);
                }

                case CYCLIC: {
                    if (block.getAttributes().get(NBOCCURENCES) != null) {
                        timerEvent.setOccurrences((Integer) block.getAttributes().get(NBOCCURENCES));
                    }
                    timerEvent.setTimeCycle((String) block.getAttributes().get(TIMECYCLE));
                    timerEvent.setEndDate((String) block.getAttributes().get(ENDDATE));
                    timerEvent.setTimeDuration(null);
                }
            }
        }
    }
    public TimerEvent getEmbeddedTimerEvent(WorkflowBlockModel block, Timers timerType) {
        TimerEvent timerEvent = new TimerEvent();
        timerEvent.setTimer(timerType);
        setTimerEventSpecsFromModel(timerEvent, block);
        return timerEvent;
    }
    //get workflow block
    public WorkflowBlock getWorkflowBlock(WorkflowBlockModel block) {
        WorkflowBlock workflowBlock = null;
        workflowBlock = new WorkflowBlock();
        workflowBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
        workflowBlock.setJobEngineProjectID(block.getProjectId());
        workflowBlock.setWorkflowId(block.getWorkflowId());
        workflowBlock.setJobEngineElementID(block.getId());

        return workflowBlock;
    }

    //get db block
    public WorkflowBlock getDatabaseBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update) {
        DBReadBlock dbReadBlock = null;
        if (!update) {
            dbReadBlock = new DBReadBlock();
            dbReadBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            dbReadBlock.setJobEngineProjectID(block.getProjectId());
            dbReadBlock.setWorkflowId(block.getWorkflowId());
            dbReadBlock.setJobEngineElementID(block.getId());
        }
        else {
            dbReadBlock = (DBReadBlock) wf.getAllBlocks().get(block.getId());
            dbReadBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            //TODO given the future requirements and spec definitions this has to be refactored
            dbReadBlock.setRequest((String) block.getAttributes().get(REQUEST));
            dbReadBlock.setDatabaseId((String) block.getAttributes().get(DATABASE_ID));
        }
        return dbReadBlock;
    }

    //get mail block
    public MailBlock getMailBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update) {
        MailBlock mailBlock = null;
        if (!update) {
            mailBlock = new MailBlock();
            mailBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            mailBlock.setJobEngineProjectID(block.getProjectId());
            mailBlock.setWorkflowId(block.getWorkflowId());
            mailBlock.setJobEngineElementID(block.getId());
        }
        else {
            mailBlock = (MailBlock) wf.getAllBlocks().get(block.getId());
            mailBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            mailBlock.setiPort((Integer) block.getAttributes().get(PORT));
            mailBlock.setStrSenderAddress((String) block.getAttributes().get(SENDER_ADDRESS));
            mailBlock.setiSendTimeOut((Integer) block.getAttributes().get(SEND_TIME_OUT));
            mailBlock.setLstCCs((List<String>) block.getAttributes().get(CC_LIST));
            mailBlock.setLstBCCs((List<String>) block.getAttributes().get(BCC_LIST));
            mailBlock.setLstAttachementPaths((List<String>) block.getAttributes().get(ATTACHEMENT_URLS));
            List<String> uploadedFiles = (List<String>) block.getAttributes().get(UPLOADED_FILES_PATHS);
            if (uploadedFiles != null) {
                for (String fileName : uploadedFiles) {
                    JELib jeLib = libraryRepository.findByJobEngineElementName(fileName);
                    if (jeLib != null) {
                        mailBlock.getLstUploadedFiles().add(jeLib.getFilePath());
                    }
                }
            }
            mailBlock.setLstUploadedFiles((List<String>) block.getAttributes().get(UPLOADED_FILES_PATHS));
            mailBlock.setLstRecieverAddress((List<String>) block.getAttributes().get(RECEIVER_ADDRESS));
            mailBlock.setEmailMessage((HashMap<String, String>) block.getAttributes().get(EMAIL_MESSAGE));
            mailBlock.setStrSMTPServer((String) block.getAttributes().get(SMTP_SERVER));
            if ((boolean) block.getAttributes().get(USE_DEFAULT_CREDENTIALS)) {
                mailBlock.setbUseDefaultCredentials((boolean) block.getAttributes().get(USE_DEFAULT_CREDENTIALS));
                mailBlock.setbEnableSSL((boolean) block.getAttributes().get(ENABLE_SSL));
            } else {
                mailBlock.setStrPassword((String) block.getAttributes().get(PASSWORD));
                mailBlock.setStrUserName((String) block.getAttributes().get(USERNAME));
                mailBlock.setbEnableSSL(false);
                mailBlock.setbUseDefaultCredentials(false);
            }
        }
        return mailBlock;
    }

    //get webapi block
    public WebApiBlock getWebApiBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update) {
        WebApiBlock webApiBlock = null;
        if (!update) {
            webApiBlock = new WebApiBlock();
            webApiBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            webApiBlock.setJobEngineProjectID(block.getProjectId());
            webApiBlock.setWorkflowId(block.getWorkflowId());
            webApiBlock.setJobEngineElementID(block.getId());
        }
        else {
            webApiBlock = (WebApiBlock) wf.getAllBlocks().get(block.getId());
            webApiBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            webApiBlock.setDescription((String) block.getAttributes().get(DESCRIPTION));
            webApiBlock.setMethod((String) block.getAttributes().get(METHOD));
            webApiBlock.setUrl((String) block.getAttributes().get(URL));
            if (block.getAttributes().containsKey(BODY)) {
                webApiBlock.setBody((String) block.getAttributes().get(BODY));
                webApiBlock.setInputs(null);
            } else {
                webApiBlock.setBody(null);
                webApiBlock.setInputs((HashMap<String, ArrayList<Object>>) block.getAttributes().get(INPUTS));
            }
            webApiBlock.setOutputs((HashMap<String, String>) block.getAttributes().get(OUTPUTS));
            webApiBlock.setAuthScheme(AuthScheme.valueOf((String) block.getAttributes().get(AUTH_SCHEME)));
            webApiBlock.setAuthentication((HashMap<String, String>) block.getAttributes().get(AUTHENTICATION));
        }
        return webApiBlock;
    }

    //get inform block
    public InformBlock getInformBlock(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update) {
        InformBlock informBlock = null;
        if (!update) {
            informBlock = new InformBlock();
            informBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            informBlock.setJobEngineProjectID(block.getProjectId());
            informBlock.setWorkflowId(block.getWorkflowId());
            informBlock.setJobEngineElementID(block.getId());
        }
        else {
            informBlock = (InformBlock) wf.getAllBlocks().get(block.getId());
            informBlock.setJobEngineElementName((String) block.getAttributes().get(NAME));
            informBlock.setJobEngineProjectID(block.getProjectId());
            informBlock.setWorkflowId(block.getWorkflowId());
            informBlock.setJobEngineElementID(block.getId());
            informBlock.setMessage((String) block.getAttributes().get(MESSAGE));
        }
        return informBlock;
    }

    //get error boundary event
    public ErrorBoundaryEvent getErrorBoundaryEvent(JEProject project, JEWorkflow wf, WorkflowBlockModel block, boolean update) {
        ErrorBoundaryEvent errorBoundaryEvent = null;
        if (!update) {
            errorBoundaryEvent = new ErrorBoundaryEvent();
            errorBoundaryEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
            errorBoundaryEvent.setJobEngineProjectID(block.getProjectId());
            errorBoundaryEvent.setWorkflowId(block.getWorkflowId());
            errorBoundaryEvent.setJobEngineElementID(block.getId());
        }
        else {
            errorBoundaryEvent = (ErrorBoundaryEvent) wf.getAllBlocks().get(block.getId());
            errorBoundaryEvent.setJobEngineElementName((String) block.getAttributes().get(NAME));
            errorBoundaryEvent.setDescription((String) block.getAttributes().get(DESCRIPTION));
            errorBoundaryEvent.setJobEngineProjectID(block.getProjectId());
            errorBoundaryEvent.setWorkflowId(block.getWorkflowId());
            errorBoundaryEvent.setJobEngineElementID(block.getId());
        }
        return errorBoundaryEvent;
    }

    /*
     * Delete a workflow block
     */
    public void deleteWorkflowBlock(String projectId, String workflowId, String blockId)
            throws  WorkflowNotFoundException, WorkflowBlockNotFound,
            InvalidSequenceFlowException, LicenseNotActiveException {

        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug(
                "[project=" + project.getProjectName() + " ][workflow = "
                        + project.getWorkflowByIdOrName(workflowId).getJobEngineElementName() + "]" + "[blockId = "
                        + blockId + "]" +
                        JEMessages.DELETING_WF_BLOCK,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        project.deleteWorkflowBlock(workflowId, blockId);
        workflowRepository.deleteById(workflowId);
    }

    /*
     * Delete a Sequence flow
     */
    public void deleteSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef)
            throws ProjectNotFoundException, WorkflowNotFoundException,
            InvalidSequenceFlowException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug(
                "[project=" + project.getProjectName() + " ][workflow = "
                        + project.getWorkflowByIdOrName(workflowId).getJobEngineElementName() + "]"
                        + JEMessages.DELETING_SEQUENCE_FLOW + sourceRef + " to  " + targetRef + " in workflow id = "
                        + workflowId,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        project.deleteWorkflowSequenceFlow(workflowId, sourceRef, targetRef);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));

    }

    /*
     * Add a Sequence flow
     */
    @Async
    public void addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef,
                                String condition) throws WorkflowNotFoundException, WorkflowBlockNotFound, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug("[project=" + project.getProjectName() + " ][workflow = "
                        + project.getWorkflowByIdOrName(workflowId).getJobEngineElementName() + "]" +
                        JEMessages.ADDING_SEQUENCE_FLOW + sourceRef + " to  " + targetRef + " in workflow id = " + workflowId,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        project.addWorkflowSequenceFlow(workflowId, sourceRef, targetRef, condition);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));

    }

    /*
     * Build a workflow
     */
    @Async
    public CompletableFuture<OperationStatusDetails> buildWorkflow(String projectId, String workflowId)
            throws LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        OperationStatusDetails result = new OperationStatusDetails(workflowId);
        if (!project.workflowExists(workflowId)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_NOT_FOUND);
            return CompletableFuture.completedFuture(result);
        }

        JEWorkflow workflow = project.getWorkflowByIdOrName(workflowId);
        result.setItemName(workflow.getJobEngineElementName());
        result.setItemName(workflow.getJobEngineElementName());
        result.setItemId(workflowId);
        if (!workflow.isEnabled()) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_IS_DISABLED);
            return CompletableFuture.completedFuture(result);
        }
        JELogger.info(
                "[project=" + project.getProjectName() + " ][workflow = " + workflow.getJobEngineElementName() + "]"
                        + JEMessages.BUILDING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);

        if (project.workflowHasError(workflow) || !WorkflowBuilder.buildWorkflow(workflow)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_BUILD_ERROR + " " + workflow.getJobEngineElementName());
            // throw new WorkflowBuildException(JEMessages.WORKFLOW_BUILD_ERROR + " " +
            // workflow.getJobEngineElementName());
        }
        workflowRepository.save(workflow);
        return CompletableFuture.completedFuture(result);
    }

    /*
     * Build all workflow
     */
    @Async
    public CompletableFuture<List<OperationStatusDetails>> buildWorkflows(String projectId, List<String> ids)
            throws  LicenseNotActiveException {
        //check if license is valid
        LicenseProperties.checkLicenseIsActive();
        JEProject project = ProjectService.getProjectById(projectId);
        JELogger.debug("[project=" + project.getProjectName() + " ]" + JEMessages.BUILDING_WFS,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, null);

        List<OperationStatusDetails> results = new ArrayList<>();
        if (ids == null) {
            for (JEWorkflow wf : project.getWorkflows().values()) {
                try {
                    results.add(buildWorkflow(projectId, wf.getJobEngineElementID()).get());
                } catch (Exception e) {
                    results.add(OperationStatusDetails.getResultDetails(wf.getJobEngineElementID(), false,
                            THREAD_INTERRUPTED_WHILE_EXECUTING,
                            wf.getJobEngineElementName()));
                }
            }
        } else {
            for (String id : ids) {
                try {
                    results.add(buildWorkflow(projectId, id).get());
                } catch (Exception e) {
                    results.add(OperationStatusDetails.getResultDetails(id, false, THREAD_INTERRUPTED_WHILE_EXECUTING,
                            project.getWorkflowByIdOrName(id).getJobEngineElementName()));
                }
            }
        }

        return CompletableFuture.completedFuture(results);

    }

    /*
     * Run a workflow
     */
    @Async
    public CompletableFuture<OperationStatusDetails> runWorkflow(String projectId, String workflowId)
            throws  LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        OperationStatusDetails result = new OperationStatusDetails(workflowId);
        if (!project.workflowExists(workflowId)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_NOT_FOUND);

            return CompletableFuture.completedFuture(result);
        }

        JEWorkflow wf = project.getWorkflowByIdOrName(workflowId);
        result.setItemName(wf.getJobEngineElementName());

        if (wf.getStatus().equals(Status.NOT_BUILT)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_NEEDS_BUILD);
            return CompletableFuture.completedFuture(result);
        }

        if (wf.getStatus().equals(Status.RUNNING)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_ALREADY_RUNNING);
            return CompletableFuture.completedFuture(result);
        }

        JELogger.info("[project=" + project.getProjectName() + " ][workflow= " +
                        wf.getJobEngineElementName() + "]" + JEMessages.RUNNING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        try {
            WorkflowBuilder.runWorkflow(projectId,
                    project.getWorkflowByIdOrName(workflowId).getJobEngineElementName().trim());
            // wf.setStatus(Status.RUNNING);
            result.setItemName(wf.getJobEngineElementName());
            result.setItemId(workflowId);
        } catch (WorkflowRunException e) {
            result.setOperationSucceeded(false);
            result.setOperationError(e.getMessage());
        }

        return CompletableFuture.completedFuture(result);
    }

    /*
     *
     * Update workflow block
     */
    public void updateWorkflowBlock(WorkflowBlockModel block)
            throws WorkflowBlockNotFound, WorkflowNotFoundException, EventException,
            WorkflowBlockException, ClassLoadException, AddClassException, LicenseNotActiveException, IOException, InterruptedException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(block.getProjectId());
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
            }
            project.removeBlockName(block.getId());
            project.addBlockName(block.getId(), (String) block.getAttributes().get(NAME));

        }

        JELogger.debug(JEMessages.UPDATING_A_WORKFLOW_BLOCK_WITH_ID + " = " +
                        block.getId() + " in workflow with id = " + block.getWorkflowId(),
                LogCategory.DESIGN_MODE, block.getProjectId(),
                LogSubModule.WORKFLOW, block.getWorkflowId());

        switch (block.getType().toLowerCase()) {
            case WorkflowConstants.START_TYPE: {
                project.addBlockToWorkflow(getStartBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.END_TYPE: {
                project.addBlockToWorkflow(getEndBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.EVENTGATEWAY_TYPE: {
                project.addBlockToWorkflow(getEventGatewayBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.MESSAGEINTERMEDIATECATCHEVENT_TYPE: {
                project.addBlockToWorkflow(getMessageEvent(project, wf,block, true));
                break;
            }
            case WorkflowConstants.SIGNALINTERMEDIATECATCHEVENT_TYPE: {
                project.addBlockToWorkflow(getSignalEvent(project, wf,block, false, true));
                break;
            }
            case WorkflowConstants.SIGNAL_THROW_EVENT_TYPE: {
                project.addBlockToWorkflow(getSignalEvent(project, wf,block, true, true));
                break;
            }
            case WorkflowConstants.EXCLUSIVEGATEWAY_TYPE: {
                project.addBlockToWorkflow(getExclusiveGatewayBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.SCRIPTTASK_TYPE: {
                if (StringUtilities.isEmpty((String) block.getAttributes().get(SCRIPT))) {
                    throw new WorkflowBlockException(JEMessages.EMPTY_SCRIPT);
                }
                project.addBlockToWorkflow(getScriptBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.CALLACTIVITYTASK_TYPE: {
                project.addBlockToWorkflow(getSubProcessBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.PARALLELGATEWAY_TYPE: {
                project.addBlockToWorkflow(getParallelGatewayBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.INCLUSIVEGATEWAY_TYPE: {
                project.addBlockToWorkflow(getInclusiveGatewayBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.DATETIMEREVENT: {
                project.addBlockToWorkflow(getTimerEvent(project, wf,block, Timers.DATE_TIME, true));
                break;
            }
            case WorkflowConstants.CYCLETIMEREVENT: {
                project.addBlockToWorkflow(getTimerEvent(project, wf,block, Timers.CYCLIC, true));
                break;
            }
            case WorkflowConstants.DURATIONTIMEREVENT: {
                project.addBlockToWorkflow(getTimerEvent(project, wf,block, Timers.DELAY, true));
                break;
            }
            case WorkflowConstants.DBREADSERVICETASK_TYPE:
            case WorkflowConstants.DBWRITESERVICETASK_TYPE:
            case WorkflowConstants.DBEDITSERVICETASK_TYPE: {
                project.addBlockToWorkflow(getDatabaseBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.MAILSERVICETASK_TYPE: {
                project.addBlockToWorkflow(getMailBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.WEBSERVICETASK_TYPE: {
                project.addBlockToWorkflow(getWebApiBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.INFORMSERVICETASK_TYPE: {
                project.addBlockToWorkflow(getInformBlock(project, wf,block, true));
                break;
            }
            case WorkflowConstants.BOUNDARYEVENT_TYPE: {
                project.addBlockToWorkflow(getErrorBoundaryEvent(project, wf,block, true));
                break;
            }
            default: {
                throw new WorkflowBlockNotFound(JEMessages.WORKFLOW_BLOCK_NOT_FOUND);
            }


        }
        wf.setStatus(Status.NOT_BUILT);
        wf.setJeObjectLastUpdate(Instant.now());
        workflowRepository.save(wf);

    }

    /*
     * Add a scripted xml file to activiti engine
     */
    public void addBpmn(String projectId, String workflowId, String bpmn)
            throws  LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = ProjectService.getProjectById(projectId);
        JELogger.debug(JEMessages.ADDING_BPMN_SCRIPT,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        JEWorkflow wf = new JEWorkflow();
        wf.setJobEngineElementName(workflowId);
        wf.setJobEngineProjectID(projectId);
        wf.setJobEngineElementID(workflowId);
        wf.setJobEngineProjectName(project.getProjectName());
        wf.setBpmnPath(ConfigurationConstants.BPMN_PATH + wf.getJobEngineElementName().trim()
                + WorkflowConstants.BPMN_EXTENSION);
        wf.setIsScript(true);
        wf.setScript(bpmn);
        WorkflowBuilder.saveBpmn(wf, bpmn);
        project.addWorkflow(wf);
        workflowRepository.save(wf);

    }

    /*
     * Update workflow
     */
    public void updateWorkflow(String projectId, String workflowId, WorkflowModel m)
            throws WorkflowNotFoundException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = ProjectService.getProjectById(projectId);

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }

        JEWorkflow wf = project.getWorkflowByIdOrName(workflowId);
        JELogger.debug("[project=" + m.getProjectName() + " ][workflow = " + m.getName() + "]" + JEMessages.UPDATING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        if (m.getName() != null) {
            wf.setJobEngineElementName(m.getName());
            try {
                FileUtilities.deleteFileFromPath(wf.getBpmnPath());
            } catch (Exception e) {
                JELogger.error(JEMessages.FAILED_TO_DELETE_FILES, LogCategory.DESIGN_MODE, projectId,
                        LogSubModule.WORKFLOW, wf.getJobEngineElementID());
            }
        }

        if (m.isOnProjectBoot()) {
            JEWorkflow startupWorkflow = project.getStartupWorkflow();
            if (startupWorkflow != null) {
                startupWorkflow.setOnProjectBoot(false);
                workflowRepository.save(startupWorkflow);
            }
        }

        if (m.isEnabled() != wf.isEnabled()) {
            wf.setEnabled(m.isEnabled());
        }
        wf.setJeObjectLastUpdate(Instant.now());
        wf.setDescription(m.getDescription());
        wf.setJeObjectCreatedBy(m.getCreatedBy());
        wf.setJeObjectModifiedBy(m.getModifiedBy());
        wf.setOnProjectBoot(m.isOnProjectBoot());
        workflowRepository.save(wf);

    }

    /*
     * Update workflow status
     */
    public void updateWorkflowStatus(String projectId, String workflowId, String status)
            throws ProjectNotFoundException, WorkflowNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }

        JEWorkflow wf = project.getWorkflowByIdOrName(workflowId);
        JELogger.debug("[project=" + project.getProjectName() + " ][workflow = "
                        + project.getWorkflowByIdOrName(workflowId).getJobEngineElementName() + "]" + JEMessages.UPDATING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);

        wf.setStatus(Status.fromString(status));
        workflowRepository.save(wf);
    }

    /*
     * Set workflow ui config //TODO to be removed at some point
     */
    public void setFrontConfig(String projectId, String workflowId, String config)
            throws  WorkflowNotFoundException, LicenseNotActiveException {

        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        project.getWorkflowByIdOrName(workflowId).setFrontConfig(config);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));

    }

    /*
     * Remove workflows
     */
    public void removeWorkflows(String projectId, List<String> ids)
            throws  LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = ProjectService.getProjectById(projectId);
        JELogger.debug("[project=" + project.getProjectName() + " ]" + JEMessages.REMOVING_WFS,
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

    /*
     * Stop workflow execution
     */
    @Async
    public CompletableFuture<OperationStatusDetails> stopWorkflow(String projectId, String workflowId)
            throws  LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);
        OperationStatusDetails result = new OperationStatusDetails(workflowId);
        if (!project.workflowExists(workflowId)) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.WORKFLOW_NOT_FOUND);
            return CompletableFuture.completedFuture(result);
        }
        JEWorkflow wf = project.getWorkflowByIdOrName(workflowId);
        JELogger.info(
                "[project=" + project.getProjectName() + " ][workflow = " + wf.getJobEngineElementName() + "]"
                        + JEMessages.STOPPING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        try {
            result.setItemName(wf.getJobEngineElementName());
            result.setItemId(workflowId);
            JERunnerAPIHandler.deleteWorkflow(projectId, wf.getJobEngineElementName());
            //wf.setStatus(Status.STOPPING);

        } catch (JERunnerErrorException e) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.ERROR_STOPPING_WORKFLOW);
        }
        wf.setStatus(Status.STOPPED);
        workflowRepository.save(wf);
        return CompletableFuture.completedFuture(result);
    }

    // @Async
    /*
     * Return the list of all workflow models
     */
    public CompletableFuture<List<WorkflowModel>> getAllWorkflows(String projectId) throws LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();
        List<JEWorkflow> wfs = workflowRepository.findByJobEngineProjectID(projectId);
        List<WorkflowModel> modelList = new ArrayList<>();
        for (JEWorkflow wf : wfs) {
            modelList.add(mapJEWorkflowToModel(wf));
        }
        return CompletableFuture.completedFuture(modelList);

    }

    /*
     * Get workflow by id
     */
    @Async
    public CompletableFuture<WorkflowModel> getWorkflow(String workflowId)
            throws LicenseNotActiveException, WorkflowNotFoundException {
        LicenseProperties.checkLicenseIsActive();
        try {
            Optional<JEWorkflow> wf = workflowRepository.findById(workflowId);
            if (wf.isPresent()) {
                return CompletableFuture.completedFuture(mapJEWorkflowToModel(wf.get()));
            } else {
                throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
            }
        }
        catch (Exception e) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
    }

    /*
     * delete all workflows
     */
    @Async
    public void deleteAll(String projectId) {
        JEProject project = ProjectService.getProjectById(projectId);
        for (String id : project.getWorkflows().keySet()) {
            try {
                removeWorkflow(projectId, id);
            } catch (WorkflowNotFoundException | LicenseNotActiveException
                    | WorkflowException e) {
                JELogger.error(JEMessages.DELETE_WORKFLOW_FAILED, LogCategory.DESIGN_MODE, projectId,
                        LogSubModule.WORKFLOW, id);
            }
        }
        // workflowRepository.deleteByJobEngineProjectID(projectId);

    }

    /*
     * Return all JE workflows
     */
    public ConcurrentHashMap<String, JEWorkflow> getAllJEWorkflows(String projectId) throws LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        List<JEWorkflow> workflows = workflowRepository.findByJobEngineProjectID(projectId);
        ConcurrentHashMap<String, JEWorkflow> map = new ConcurrentHashMap<String, JEWorkflow>();
        for (JEWorkflow workflow : workflows) {
            map.put(workflow.getJobEngineElementID(), workflow);
        }
        return map;
    }

    /*
     * run multiple workflows
     */
    @Async
    public CompletableFuture<List<OperationStatusDetails>> runWorkflows(String projectId, List<String> ids)
            throws LicenseNotActiveException {

        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectService.getProjectById(projectId);

        List<OperationStatusDetails> results = new ArrayList<>();
        if (ids == null) {
            for (JEWorkflow wf : project.getWorkflows().values()) {
                try {
                    results.add(runWorkflow(projectId, wf.getJobEngineElementID()).get());
                } catch (Exception e) {
                    results.add(OperationStatusDetails.getResultDetails(wf.getJobEngineElementID(), false,
                            THREAD_INTERRUPTED_WHILE_EXECUTING,
                            wf.getJobEngineElementName()));
                }
            }
        } else {
            for (String id : ids) {
                try {
                    results.add(runWorkflow(projectId, id).get());
                } catch (Exception e) {
                    results.add(OperationStatusDetails.getResultDetails(id, false, THREAD_INTERRUPTED_WHILE_EXECUTING,
                            project.getWorkflowByIdOrName(id).getJobEngineElementName()));
                }
            }
        }
        return CompletableFuture.completedFuture(results);
    }

    /*
     * Stop workflow execution
     */
    @Async
    public CompletableFuture<List<OperationStatusDetails>> stopWorkflows(String projectId, List<String> ids)
            throws LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = ProjectService.getProjectById(projectId);

        List<OperationStatusDetails> results = new ArrayList<>();
        if (ids == null) {
            for (JEWorkflow wf : project.getWorkflows().values()) {
                try {
                    results.add(stopWorkflow(projectId, wf.getJobEngineElementID()).get());
                } catch (Exception e) {
                    results.add(OperationStatusDetails.getResultDetails(wf.getJobEngineElementID(), false,
                            THREAD_INTERRUPTED_WHILE_EXECUTING,
                            wf.getJobEngineElementName()));
                }
            }
        } else {
            for (String id : ids) {
                try {
                    results.add(stopWorkflow(projectId, id).get());
                } catch (Exception e) {

                    results.add(OperationStatusDetails.getResultDetails(id, false, THREAD_INTERRUPTED_WHILE_EXECUTING,
                            project.getWorkflowByIdOrName(id).getJobEngineElementName()));
                }
            }
        }
        return CompletableFuture.completedFuture(results);
    }

    //add email attachment
    public void addAttachment(LibModel libModel) throws LibraryException, ExecutionException, InterruptedException, IOException {
        JELib jeLib = projectService.addFile(libModel);
        if (jeLib != null) {
            libModel.setFilePath(jeLib.getFilePath());
            int responseCode = JEBuilderApiHandler.uploadFileTo(SIOTHConfigUtility.getSiothConfig().getApis().getEmailAPI().getAddress() + "GetFiles", libModel);
            if (responseCode != 204 && responseCode != 200) {
                FileUtilities.deleteFileFromPath(jeLib.getFilePath());
                throw new LibraryException(JEMessages.ERROR_IMPORTING_FILE);
            } else {
                libraryRepository.save(jeLib);
            }
        }


    }

    //add email attachments
    public void addAttachments(List<LibModel> libModels) throws LibraryException, IOException, ExecutionException, InterruptedException {
        for (LibModel libModel : libModels) {
            addAttachment(libModel);
        }
    }

    //delete email attachment
    public void deleteAttachmentByName(String libName) throws IOException {
        JELib lib = libraryRepository.findByJobEngineElementName(libName);
        if (lib != null) {
            FileUtilities.deleteFileFromPath(lib.getFilePath());
            libraryRepository.delete(lib);
        }
    }

    //clean up workflow data
    public void cleanUpHouse() {
        try {
            workflowRepository.deleteAll();
            FileUtilities.deleteDirectory(ConfigurationConstants.BPMN_PATH);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //return workflow by name
    public List<JEWorkflow> getWorkflowByName(String workflowName) {
        return workflowRepository.findByJobEngineElementName(workflowName);
    }

    //map bean to model
    public WorkflowModel mapJEWorkflowToModel(JEWorkflow wf) {
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
        model.setCreatedAt(wf.getJeObjectCreationDate().toString());
        model.setModifiedAt(wf.getJeObjectLastUpdate().toString());
        model.setFrontConfig(wf.getFrontConfig());
        model.setEnabled(wf.isEnabled());
        model.setProjectName(wf.getJobEngineProjectName());
        return model;
    }

    //map model to bean
    public JEWorkflow mapWorkflowModelToJEWorkflow(WorkflowModel workflowModel) {
        JEWorkflow jeWorkflow = new JEWorkflow();
        jeWorkflow.setEnabled(workflowModel.isEnabled());
        jeWorkflow.setJobEngineProjectName(workflowModel.getProjectName());
        jeWorkflow.setOnProjectBoot(workflowModel.isOnProjectBoot());
        jeWorkflow.setJeObjectModifiedBy(workflowModel.getModifiedBy());
        jeWorkflow.setDescription(workflowModel.getDescription());
        jeWorkflow.setJeObjectCreatedBy(workflowModel.getCreatedBy());
        jeWorkflow.setJobEngineElementID(workflowModel.getId());
        jeWorkflow.setBpmnPath(workflowModel.getPath());
        jeWorkflow.setJobEngineProjectID(workflowModel.getProjectId());
        jeWorkflow.setTriggeredByEvent(workflowModel.isTriggeredByEvent());
        jeWorkflow.setFrontConfig(workflowModel.getFrontConfig());
        jeWorkflow.setJobEngineElementName(workflowModel.getName());
        return jeWorkflow;
    }

}
