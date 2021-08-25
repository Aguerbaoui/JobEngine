package io.je.project.services;

import blocks.basic.*;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.InclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.*;
import builder.WorkflowBuilder;
import io.je.classbuilder.models.ClassDefinition;
import io.je.classbuilder.models.MethodModel;
import io.je.project.beans.JEProject;
import io.je.project.models.WorkflowBlockModel;
import io.je.project.repository.WorkflowRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.Timers;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.models.EventType;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.string.JEStringUtils;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static io.je.utilities.constants.WorkflowConstants.*;

@Service
public class WorkflowService {

    public static final String NAME = "name";
    public static final String DURATION = "duration";
    public static final String ENDDATE = "enddate";
    public static final String TIMECYCLE = "timecycle";
    public static final String TIMEDATE = "timedate";
    public static final String EVENT_ID = "eventId";
    public static final String SOURCE_REF = "sourceRef";
    public static final String TARGET_REF = "targetRef";
    public static final String CONDITION = "condition";
    public static final String BODY = "body";
    public static final String IMPORTS = "imports";


    @Autowired
     WorkflowRepository workflowRepository;
    
    @Autowired
    EventService eventService;

    @Autowired
    ClassService classService;
    /*
     * Add a workflow to a project
     * */
    public void addWorkflow(io.je.utilities.models.WorkflowModel m) throws ProjectNotFoundException, ConfigException {
    	
        JEProject project = ProjectService.getProjectById(m.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JEWorkflow wf = new JEWorkflow();
        wf.setJobEngineElementID(m.getKey());
        wf.setJobEngineProjectID(m.getProjectId());
        wf.setWorkflowName(m.getName());
        wf.setDescription(m.getDescription());
        wf.setJeObjectLastUpdate(LocalDateTime.now());
        wf.setJeObjectCreationDate(LocalDateTime.now());
        if(m.isOnProjectBoot()) {
            JEWorkflow startupWorkflow = project.getStartupWorkflow();
            if(startupWorkflow != null) {
                startupWorkflow.setOnProjectBoot(false);
                workflowRepository.save(startupWorkflow);
            }
            wf.setOnProjectBoot(true);
        }

        JELogger.debug( "[projectId ="+m.getProjectId()+" ][workflowId = " +
                        wf.getJobEngineElementID()+"]"+JEMessages.ADDING_WF ,
                LogCategory.DESIGN_MODE, m.getProjectId(), LogSubModule.WORKFLOW, m.getKey());
        project.addWorkflow(wf);
        workflowRepository.save(wf);
    }

    /*
     * Remove a workflow from a project
     * */

    public void removeWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, WorkflowNotFoundException, ConfigException, InterruptedException, JERunnerErrorException, ExecutionException {
    	

        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }

        JELogger.debug( "[projectId ="+projectId+" ][workflowId = " + workflowId+"]"+JEMessages.REMOVING_WF ,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.WORKFLOW, workflowId);
        String wfName = project.getWorkflowByIdOrName(workflowId).getWorkflowName().trim();
        //delete workflow block names
        Enumeration<String> blockIds = project.getWorkflowByIdOrName(workflowId).getAllBlocks().keys();
        while (blockIds.hasMoreElements()) {
            project.removeBlockName(blockIds.nextElement());
        }
        project.removeWorkflow(workflowId);
        JERunnerAPIHandler.deleteWorkflow(projectId, wfName);
        workflowRepository.deleteById(workflowId);
    }

    /*
     * Add a workflow block to a workflow
     * */
    public String addWorkflowBlock(WorkflowBlockModel block) throws ProjectNotFoundException, WorkflowNotFoundException, InvalidSequenceFlowException, WorkflowBlockNotFound, EventException, ConfigException, WorkflowBlockException {
    	
    	JEProject project = ProjectService.getProjectById(block.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(block.getWorkflowId())) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug( "[projectId ="+block.getProjectId()+" ][workflowId = " +
                        block.getWorkflowId()+"]"+JEMessages.ADDING_WF_BLOCK + "" +
                        " id = " + block.getId(),
                LogCategory.DESIGN_MODE, block.getProjectId(),
                LogSubModule.WORKFLOW, block.getWorkflowId());
        project.getWorkflowByIdOrName(block.getWorkflowId()).setStatus(JEWorkflow.IDLE);
        String generatedBlockName = "";
        if(!block.getType().equalsIgnoreCase(SEQ_FLOW_TYPE)) {
            generatedBlockName = project.generateUniqueBlockName((String) block.getAttributes().get(NAME));
            block.getAttributes().put(NAME, generatedBlockName);
            project.addBlockName(block.getId(), generatedBlockName);
        }


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
            b.setTimeout(Integer.parseInt((String) block.getAttributes().get(TIMEOUT)));
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
            TimerEvent b = new TimerEvent();
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeDate((String) block.getAttributes().get(TIMEDATE));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setTimer(Timers.DATE_TIME);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.CYCLETIMEREVENT)) {
            TimerEvent b = new TimerEvent();
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeCycle((String) block.getAttributes().get(TIMECYCLE));
            b.setEndDate((String) block.getAttributes().get(ENDDATE));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setTimer(Timers.CYCLIC);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DURATIONTIMEREVENT)) {
            TimerEvent b = new TimerEvent();
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeDuration((String) block.getAttributes().get(DURATION));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setTimer(Timers.DELAY);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DBREADSERVICETASK_TYPE)) {
            DBReadBlock b = new DBReadBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DBWRITESERVICETASK_TYPE)) {
            DBWriteBlock b = new DBWriteBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        }  else if (block.getType().equalsIgnoreCase(DBEDITSERVICETASK_TYPE)) {
            DBEditBlock b = new DBEditBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        }
        else if (block.getType().equalsIgnoreCase(WorkflowConstants.MAILSERVICETASK_TYPE)) {
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
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        }

        else if(block.getType().equalsIgnoreCase(WorkflowConstants.INFORMSERVICETASK_TYPE)) {
            InformBlock b = new InformBlock();
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        }

        else if(block.getType().equalsIgnoreCase(BOUNDARYEVENT_TYPE)) {
            ErrorBoundaryEvent b = new ErrorBoundaryEvent();
            b.setName((String) block.getAttributes().get(NAME));
            b.setDescription((String) block.getAttributes().get(DESCRIPTION));
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
            throw new WorkflowBlockNotFound(JEMessages.WORKFLOW_BLOCK_NOT_FOUND);
        }
        workflowRepository.save(project.getWorkflowByIdOrName(block.getWorkflowId()));
        return generatedBlockName;
    }


    /*
     * Delete a workflow block
     * */
    public void deleteWorkflowBlock(String projectId, String workflowId, String blockId) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException, ConfigException {
    	
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug( "[projectId ="+projectId+" ][workflowId = " +workflowId+"]" +"[blockId = " +blockId+"]"+
                        JEMessages.DELETING_WF_BLOCK,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        project.deleteWorkflowBlock(workflowId, blockId);
        workflowRepository.deleteById(workflowId);
    }

    /*
     * Delete a Sequence flow
     * */
    public void deleteSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException, ConfigException {
    	
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug( "[projectId ="+projectId+" ][workflowId = " +
                        workflowId+"]"+ JEMessages.DELETING_SEQUENCE_FLOW + sourceRef + " to  " + targetRef + " in workflow id = " + workflowId,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        project.deleteWorkflowSequenceFlow(workflowId, sourceRef, targetRef);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));

    }

    /*
     * Add a Sequence flow
     * */
    public void addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef, String condition) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException, ConfigException {
    	
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug( "[projectId ="+projectId+" ][workflowId = " +workflowId+"]"+
                JEMessages.ADDING_SEQUENCE_FLOW + sourceRef + " to  " + targetRef + " in workflow id = " + workflowId,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        project.addWorkflowSequenceFlow(workflowId, sourceRef, targetRef, condition);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));

    }

    /*
     * Build a workflow
     * */
    public void buildWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException, InterruptedException, ExecutionException, ConfigException, WorkflowBuildException {
    	
    	
    	
    	
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }

        JELogger.info( "[projectId ="+projectId+" ][workflowId = " +workflowId+"]"+ JEMessages.BUILDING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        JEWorkflow workflow = project.getWorkflowByIdOrName(workflowId);
        if(!WorkflowBuilder.buildWorkflow(workflow)) {
            throw new WorkflowBuildException(JEMessages.WORKFLOW_BUILD_ERROR);
        };
    }

    /*
     * Build all workflow
     * */
    @Async
    public CompletableFuture<Void> buildWorkflows(String projectId) throws ProjectNotFoundException, IOException,  InterruptedException, ExecutionException, ConfigException, WorkflowBuildException {
    	
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JELogger.debug( "[projectId ="+projectId+" ]"+ JEMessages.BUILDING_WFS,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, null);
        for (JEWorkflow wf : project.getWorkflows().values()) {
            if(!WorkflowBuilder.buildWorkflow(wf)) {
                throw new WorkflowBuildException(JEMessages.WORKFLOW_BUILD_ERROR);
            };
        }
        return CompletableFuture.completedFuture(null);

    }

    /*
     * Run a workflow
     * */
    public void runWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException, WorkflowAlreadyRunningException, InterruptedException, ExecutionException, JERunnerErrorException {
    	
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }

        if(!project.getWorkflowByIdOrName(workflowId).getStatus().equals(JEWorkflow.BUILT) || project.getWorkflowByIdOrName(workflowId).getStatus().equals(JEWorkflow.RUNNING)) {
            throw new WorkflowAlreadyRunningException(JEMessages.WORKFLOW_NEEDS_BUILD);
        }
        //set statuse
        if (!project.getWorkflowByIdOrName(workflowId).getStatus().equals(JEWorkflow.RUNNING)) {
            JELogger.info( "[projectId =" + projectId + " ][workflowId = " +
                            workflowId + "]" + JEMessages.RUNNING_WF,
                    LogCategory.DESIGN_MODE, projectId,
                    LogSubModule.WORKFLOW, workflowId);
            WorkflowBuilder.runWorkflow(projectId, project.getWorkflowByIdOrName(workflowId).getWorkflowName().trim());
            project.getWorkflowByIdOrName(workflowId).setStatus(JEWorkflow.RUNNING);
        } else {
            throw new WorkflowAlreadyRunningException(JEMessages.WORKFLOW_ALREADY_RUNNING);
        }
    }


    /*
     *
     * Update workflow block
     * */
    public void updateWorkflowBlock(WorkflowBlockModel block) throws WorkflowBlockNotFound, WorkflowNotFoundException, ProjectNotFoundException, IOException, InterruptedException, ExecutionException, EventException, ConfigException, WorkflowBlockException, ClassLoadException, JERunnerErrorException, AddClassException, DataDefinitionUnreachableException, AddRuleBlockException {
    	
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

        String oldWorkflowBlockName = project.getWorkflowByIdOrName(block.getWorkflowId()).getBlockById(block.getId()).getName();
        if(!oldWorkflowBlockName.equals((String) block.getAttributes().get(NAME) ))
        {
        	if(project.blockNameExists((String) block.getAttributes().get(NAME)))
        	{
        		throw new WorkflowBlockException(JEMessages.BLOCK_NAME_CAN_T_BE_UPDATED_BECAUSE_IT_ALREADY_EXISTS);
        	}
        	else {
        		project.removeBlockName(block.getId());
        		project.addBlockName(block.getId(), (String) block.getAttributes().get(NAME));
        	}
        }

        JELogger.debug( JEMessages.UPDATING_A_WORKFLOW_BLOCK_WITH_ID + " = " +
                        block.getId() + " in workflow with id = " + block.getWorkflowId(),
                LogCategory.DESIGN_MODE, block.getProjectId(),
                LogSubModule.WORKFLOW, block.getWorkflowId());
        project.getWorkflowByIdOrName(block.getWorkflowId()).setStatus(JEWorkflow.IDLE);
        if (block.getType().equalsIgnoreCase(WorkflowConstants.START_TYPE)) {
            StartBlock b = (StartBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setEventId(null);
            if (!JEStringUtils.isEmpty((String) block.getAttributes().get(EVENT_ID))) {
                eventService.updateEventType(block.getProjectId(), (String) block.getAttributes().get(EVENT_ID),  EventType.START_WORKFLOW.toString());
                b.setEventId((String) block.getAttributes().get(EVENT_ID));
            }

            else if(WorkflowConstants.DATETIMEREVENT.equals(block.getAttributes().get(WorkflowConstants.EVENT_TYPE))) {
                TimerEvent timerEvent = new TimerEvent();
                timerEvent.setName((String) block.getAttributes().get(NAME));
                timerEvent.setTimeDate((String) block.getAttributes().get(ENDDATE));
                timerEvent.setTimer(Timers.DATE_TIME);
                timerEvent.setTimeDuration(null);
                timerEvent.setEndDate(null);
                timerEvent.setTimeCycle(null);
                b.setTimerEvent(timerEvent);
            }

            else if(WorkflowConstants.DURATIONTIMEREVENT.equals(block.getAttributes().get(WorkflowConstants.EVENT_TYPE))) {
                TimerEvent timerEvent = new TimerEvent();
                timerEvent.setName((String) block.getAttributes().get(NAME));
                timerEvent.setTimeDuration((String) block.getAttributes().get(TIMECYCLE));
                timerEvent.setTimeDate((String) block.getAttributes().get(ENDDATE));
                timerEvent.setTimeCycle(null);
                timerEvent.setEndDate(null);
                timerEvent.setTimer(Timers.DELAY);
                b.setTimerEvent(timerEvent);
            }

            else if(WorkflowConstants.CYCLETIMEREVENT.equals(block.getAttributes().get(WorkflowConstants.EVENT_TYPE))) {
                TimerEvent timerEvent = new TimerEvent();
                timerEvent.setName((String) block.getAttributes().get(NAME));
                timerEvent.setTimeCycle((String) block.getAttributes().get(TIMECYCLE));
                timerEvent.setTimeDate((String) block.getAttributes().get(ENDDATE));
                timerEvent.setTimer(Timers.CYCLIC);
                timerEvent.setEndDate(null);
                timerEvent.setTimeDuration(null);
                b.setTimerEvent(timerEvent);
            }

            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.END_TYPE)) {
            EndBlock b = (EndBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.EVENTGATEWAY_TYPE)) {
            EventGatewayBlock b = (EventGatewayBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.MESSAGEINTERMEDIATECATCHEVENT_TYPE)) {
            MessageEvent b = (MessageEvent) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
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
            MessageEvent b = (MessageEvent) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
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
            SignalEvent b = (SignalEvent) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
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
            SignalEvent b = (SignalEvent) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
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
            ExclusiveGatewayBlock b = (ExclusiveGatewayBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.SCRIPTTASK_TYPE)) {
            if(JEStringUtils.isEmpty((String) block.getAttributes().get(SCRIPT))) {
                throw new WorkflowBlockException(JEMessages.EMPTY_SCRIPT);
            }
            ScriptBlock b = (ScriptBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            ArrayList<String> imports = (ArrayList) block.getAttributes().get(IMPORTS);
            b.setScript((String) block.getAttributes().get(SCRIPT));
            b.setTimeout((Integer) block.getAttributes().get(TIMEOUT));
            ClassDefinition c = getClassModel(b.getJobEngineElementID(), project.getWorkflowByIdOrName(block.getWorkflowId()).getWorkflowName()+b.getName(), b.getScript());
            c.setImports(imports);
            //True to send directly to JERunner
            classService.addClass(c, true,true);
            //JEClassLoader.generateScriptTaskClass(b.getName(), b.getScript());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.PARALLELGATEWAY_TYPE)) {
            ParallelGatewayBlock b = (ParallelGatewayBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.INCLUSIVEGATEWAY_TYPE)) {
            InclusiveGatewayBlock b = (InclusiveGatewayBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DATETIMEREVENT)) {
            TimerEvent b = (TimerEvent) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeDate((String) block.getAttributes().get(ENDDATE));
            b.setTimer(Timers.DATE_TIME);
            b.setTimeDuration(null);
            b.setEndDate(null);
            b.setTimeCycle(null);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.CYCLETIMEREVENT)) {
            TimerEvent b = (TimerEvent) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeCycle((String) block.getAttributes().get(TIMECYCLE));
            //b.setEndDate((String) block.getAttributes().get(ENDDATE));
            b.setTimeDate((String) block.getAttributes().get(ENDDATE));
            b.setTimer(Timers.CYCLIC);
            b.setEndDate(null);
            b.setTimeDuration(null);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DURATIONTIMEREVENT)) {
            TimerEvent b = (TimerEvent) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setTimeDuration((String) block.getAttributes().get(TIMECYCLE));
            b.setTimeDate((String) block.getAttributes().get(ENDDATE));
            b.setTimeCycle(null);
            b.setEndDate(null);
            b.setTimer(Timers.DELAY);
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.DBREADSERVICETASK_TYPE)) {
            DBReadBlock b = (DBReadBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setRequest((String) block.getAttributes().get(REQUEST));
            b.setDatabaseId((String) block.getAttributes().get(DATABASE_ID));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(DBEDITSERVICETASK_TYPE)) {
            DBEditBlock b = (DBEditBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setRequest((String) block.getAttributes().get("request"));
            b.setDatabaseId((String) block.getAttributes().get("databaseId"));
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(DBWRITESERVICETASK_TYPE)) {
            DBWriteBlock b = (DBWriteBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setRequest((String) block.getAttributes().get("request"));
            b.setDatabaseId((String) block.getAttributes().get("databaseId"));
            project.addBlockToWorkflow(b);
        }

        else if (block.getType().equalsIgnoreCase(WorkflowConstants.MAILSERVICETASK_TYPE)) {
            MailBlock b = (MailBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));

            b.setiPort((Integer) block.getAttributes().get(PORT));
            b.setStrSenderAddress((String) block.getAttributes().get(SENDER_ADDRESS));
            b.setiSendTimeOut((Integer) block.getAttributes().get(SEND_TIME_OUT));
            b.setLstRecieverAddress((List<String>) block.getAttributes().get(RECEIVER_ADDRESS));
            b.setEmailMessage((HashMap<String, String>) block.getAttributes().get(EMAIL_MESSAGE));
            b.setStrSMTPServer((String) block.getAttributes().get(SMTP_SERVER));
            if((boolean) block.getAttributes().get(USE_DEFAULT_CREDENTIALS)) {
                b.setbUseDefaultCredentials((boolean) block.getAttributes().get(USE_DEFAULT_CREDENTIALS));
                b.setbEnableSSL((boolean) block.getAttributes().get(ENABLE_SSL));
            }
            else {
                b.setStrPassword((String) block.getAttributes().get(PASSWORD));
                b.setStrUserName((String) block.getAttributes().get(USERNAME));
                b.setbEnableSSL(false);
                b.setbUseDefaultCredentials(false);
            }
            project.addBlockToWorkflow(b);
        }
        else if (block.getType().equalsIgnoreCase(WorkflowConstants.WEBSERVICETASK_TYPE)) {
            WebApiBlock b = (WebApiBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setDescription((String) block.getAttributes().get(DESCRIPTION));
            b.setMethod((String) block.getAttributes().get(METHOD));
            b.setUrl((String) block.getAttributes().get(URL));
            if(block.getAttributes().containsKey(BODY)) {
                b.setBody((String) block.getAttributes().get(BODY));
                b.setInputs(null);
            }
            else {
                b.setBody(null);
                b.setInputs((HashMap<String, ArrayList<Object>>) block.getAttributes().get(INPUTS));
            }

            b.setOutputs((HashMap<String, String>) block.getAttributes().get(OUTPUTS));
            project.addBlockToWorkflow(b);
        }

        else if(block.getType().equalsIgnoreCase(WorkflowConstants.INFORMSERVICETASK_TYPE)) {
            InformBlock b = (InformBlock) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            b.setMessage((String) block.getAttributes().get(MESSAGE));
            project.addBlockToWorkflow(b);
        }
        else if(block.getType().equalsIgnoreCase(BOUNDARYEVENT_TYPE)) {
            ErrorBoundaryEvent b = (ErrorBoundaryEvent) project.getWorkflowByIdOrName(block.getWorkflowId()).getAllBlocks().get(block.getId());
            b.setName((String) block.getAttributes().get(NAME));
            b.setDescription((String) block.getAttributes().get(DESCRIPTION));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            //b.setErrorRef((String) block.getAttributes().get(ERROR_REF));
            project.addBlockToWorkflow(b);
        }
        workflowRepository.save(project.getWorkflowByIdOrName(block.getWorkflowId()));


    }

    private ClassDefinition getClassModel(String id, String name, String script) {
        ClassDefinition c = new ClassDefinition();
        c.setClass(true);
        c.setIdClass(id);
        c.setName(name);
        c.setClassVisibility("public");
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

    public void addBpmn(String projectId, String workflowId, String bpmn) throws ProjectNotFoundException {
    	
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JELogger.debug( JEMessages.ADDING_BPMN_SCRIPT,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        JEWorkflow wf = new JEWorkflow();
        wf.setWorkflowName(workflowId);
        wf.setJobEngineProjectID(projectId);
        wf.setJobEngineElementID(workflowId);
        wf.setBpmnPath(ConfigurationConstants.BPMN_PATH + wf.getWorkflowName().trim() + WorkflowConstants.BPMN_EXTENSION);
        wf.setScript(true);
        wf.setScript(bpmn);
        WorkflowBuilder.saveBpmn(wf, bpmn);
        project.addWorkflow(wf);
        workflowRepository.save(wf);

    }

    public void updateWorkflow(String projectId, String workflowId, WorkflowModel m) throws WorkflowNotFoundException, ProjectNotFoundException, ConfigException {
    	
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }

        if (m.getName() != null) {
            JELogger.debug( "[projectId ="+projectId+" ][workflowId = " + workflowId+"]"+JEMessages.UPDATING_WF,
                    LogCategory.DESIGN_MODE, projectId,
                    LogSubModule.WORKFLOW, workflowId);
            project.getWorkflowByIdOrName(workflowId).setWorkflowName(m.getName());
        }

        if(m.isOnProjectBoot()) {
            JEWorkflow startupWorkflow = project.getStartupWorkflow();
            if(startupWorkflow != null) {
                startupWorkflow.setOnProjectBoot(false);
                workflowRepository.save(startupWorkflow);
            }

        }
        project.getWorkflowByIdOrName(workflowId).setOnProjectBoot(m.isOnProjectBoot());
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));


    }

    public void setFrontConfig(String projectId, String workflowId, String config) throws ProjectNotFoundException, WorkflowNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
        }

        if(! project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException( JEMessages.WORKFLOW_NOT_FOUND);
        }
        project.getWorkflowByIdOrName(workflowId).setFrontConfig(config);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));

    }


    public void removeWorkflows(String projectId, List<String> ids) throws ProjectNotFoundException, ConfigException {
    	
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        JELogger.debug( "[projectId ="+projectId+" ]"+ JEMessages.REMOVING_WFS,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, null);
        for(String id: ids) {
            try {
                removeWorkflow(projectId, id);
                workflowRepository.deleteById(id);
            }
            catch (Exception e) {
                JELogger.error( JEMessages.DELETE_WORKFLOW_FAILED + " id = " + id + " " + e.getMessage(),
                        LogCategory.DESIGN_MODE, projectId,
                        LogSubModule.WORKFLOW, id);
            }
        }
    }

    public void stopWorkflow(String projectId, String workflowId) throws ConfigException, ProjectNotFoundException, WorkflowNotFoundException, InterruptedException, JERunnerErrorException, ExecutionException {
        
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        JELogger.debug( "[projectId ="+projectId+" ][workflowId = " +workflowId+"]"+ JEMessages.STOPPING_WF,
                LogCategory.DESIGN_MODE, projectId,
                LogSubModule.WORKFLOW, workflowId);
        JERunnerAPIHandler.deleteWorkflow(projectId, project.getWorkflowByIdOrName(workflowId).getWorkflowName());
        project.getWorkflowByIdOrName(workflowId).setStatus(JEWorkflow.IDLE);
        workflowRepository.save(project.getWorkflowByIdOrName(workflowId));

    }
    
    public List <JEWorkflow> getAllWorkflows(String projectId)
    {
    	return  workflowRepository.findByJobEngineProjectID(projectId);

    }
    
    public JEWorkflow getWorkflow(String workflowId)
    {
    	return  workflowRepository.findById(workflowId).get();

    }

	public void deleteAll(String projectId) {
		workflowRepository.deleteByJobEngineProjectID(projectId);
		
	}
	
	   public ConcurrentHashMap<String, JEWorkflow> getAllJEWorkflows(String projectId) {
			List<JEWorkflow> workflows = workflowRepository.findByJobEngineProjectID(projectId);
			ConcurrentHashMap<String, JEWorkflow> map = new ConcurrentHashMap<String, JEWorkflow>();
			for(JEWorkflow workflow : workflows )
			{
				map.put(workflow.getJobEngineElementID(), workflow);
			}
			return map;
		}
}
