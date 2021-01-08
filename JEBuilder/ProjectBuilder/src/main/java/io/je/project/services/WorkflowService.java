package io.je.project.services;

import blocks.WorkflowBlock;
import blocks.basic.*;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.MessageCatchEvent;
import builder.WorkflowBuilder;
import io.je.project.beans.JEProject;
import io.je.project.models.WorkflowBlockModel;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import models.JEWorkflow;
import org.springframework.stereotype.Service;

import java.io.IOException;
/*
 * Service class to handle business logic for workflows
 * */

@Service
public class WorkflowService {

    /*
    * Add a workflow to a project
    * */
    public void addWorkflow(JEWorkflow wf)  throws ProjectNotFoundException {
        //TODO JPA save
        JEProject project = ProjectService.getProjectById(wf.getJobEngineProjectID());
        if(project == null) {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        }
        project.addWorkflow(wf);
    }
    /*
    * Remove a workflow from a project
    * */
    public void removeWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, WorkflowNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
        }

        if(!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException( Errors.workflowNotFound);
        }
        project.removeWorkflow(workflowId);
    }
    /*
    * Add a workflow block to a workflow
    * */
    public void addWorkflowBlock(WorkflowBlockModel block) throws ProjectNotFoundException, WorkflowNotFoundException, InvalidSequenceFlowException, WorkflowBlockNotFound {
        JEProject project = ProjectService.getProjectById(block.getProjectId());
        if(project == null) {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        }
        else if (!project.workflowExists(block.getWorkflowId())) {
            throw new WorkflowNotFoundException( Errors.workflowNotFound);
        }
        if (block.getType().equalsIgnoreCase(WorkflowConstants.startType)) {
            StartBlock b = new StartBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.endType)) {
            EndBlock b = new EndBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.eventgatewayType)) {
            EventGatewayBlock b = new EventGatewayBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.messageintermediatecatcheventType)) {
            MessageCatchEvent b = new MessageCatchEvent();
            b.setName(block.getAttributes().get("name"));
            b.setMessageRef(block.getAttributes().get("messageRef"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.exclusivegatewayType)) {
            ExclusiveGatewayBlock b = new ExclusiveGatewayBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.scripttaskType)) {
            ScriptBlock b = new ScriptBlock();
            b.setName(block.getAttributes().get("name"));
            b.setScript(block.getAttributes().get("script"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.parallelgatewayType)) {
            ParallelGatewayBlock b = new ParallelGatewayBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.dbservicetaskType)) {
            DBWriteBlock b = new DBWriteBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.mailservicetaskType)) {
            MailBlock b = new MailBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            project.addBlockToWorkflow(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.seqFlowType)) {
            addSequenceFlow(block.getProjectId(), block.getWorkflowId(),
                    block.getAttributes().get("sourceRef"), block.getAttributes().get("targetRef"),
                    block.getAttributes().get("condition"));
        }
    }

    /*
    * Delete a workflow block
    * */
    public void deleteWorkflowBlock(String projectId, String workflowId, String blockId) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        }else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException( Errors.workflowNotFound);
        }

        project.deleteWorkflowBlock(workflowId, blockId);
    }

    /*
    * Delete a Sequence flow
    * */
    public void deleteSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException( Errors.workflowNotFound);
        }

        project.deleteWorkflowSequenceFlow(workflowId, sourceRef, targetRef);
    }

    /*
    * Add a Sequence flow
    * */
    public void addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef, String condition) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException( Errors.workflowNotFound);
        }

        project.addWorkflowSequenceFlow(workflowId, sourceRef, targetRef, condition);
    }

    /*
    * Build a workflow
    * */
    public void buildWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException( Errors.workflowNotFound);
        }

        WorkflowBuilder.buildWorkflow(project.getWorkflowById(workflowId));
    }

    /*
     * Build all workflow
     * */
    public void buildWorkflows(String projectId) throws ProjectNotFoundException, IOException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        }

        for(JEWorkflow wf: project.getWorkflows().values()) {
            WorkflowBuilder.buildWorkflow(wf);
        }

    }

    /*
    * Run a workflow
    * */
    public void runWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException( Errors.workflowNotFound);
        }

        //set statuses wesh
        WorkflowBuilder.runWorkflow(workflowId);
    }

    /*
     * Run all workflows
     * */
    public void runWorkflows(String projectId) throws  ProjectNotFoundException, IOException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        }
        for(JEWorkflow wf: project.getWorkflows().values()) {
            WorkflowBuilder.runWorkflow(wf.getWorkflowName().trim());
        }
    }

    public void updateWorkflowBlock(WorkflowBlockModel block) throws WorkflowBlockNotFound, WorkflowNotFoundException, ProjectNotFoundException {
        JEProject project = ProjectService.getProjectById(block.getProjectId());
        if(project == null) {
            throw new ProjectNotFoundException( Errors.projectNotFound);
        }
        if(! project.workflowExists(block.getWorkflowId())) {
            throw new WorkflowNotFoundException( Errors.workflowNotFound);
        }

        if(!project.getWorkflowById(block.getWorkflowId()).blockExists(block.getId())) {
            throw new WorkflowBlockNotFound( Errors.workflowBlockNotFound);
        }

        if (block.getType().equalsIgnoreCase(WorkflowConstants.startType)) {
            StartBlock b = new StartBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            updateStartBlock(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.endType)) {
            EndBlock b = new EndBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            updateEndBlock(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.eventgatewayType)) {
            EventGatewayBlock b = new EventGatewayBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            updateEventGateway(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.messageintermediatecatcheventType)) {
            MessageCatchEvent b = new MessageCatchEvent();
            b.setName(block.getAttributes().get("name"));
            b.setMessageRef(block.getAttributes().get("messageRef"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            updateMessageCatchEvent(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.exclusivegatewayType)) {
            ExclusiveGatewayBlock b = new ExclusiveGatewayBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            updateExclusiveGateway(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.scripttaskType)) {
            ScriptBlock b = new ScriptBlock();
            b.setName(block.getAttributes().get("name"));
            b.setScript(block.getAttributes().get("script"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            updateScript(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.parallelgatewayType)) {
            ParallelGatewayBlock b = new ParallelGatewayBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            updateParallelGateway(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.dbservicetaskType)) {
            DBWriteBlock b = new DBWriteBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            updateDbTask(b);
        } else if (block.getType().equalsIgnoreCase(WorkflowConstants.mailservicetaskType)) {
            MailBlock b = new MailBlock();
            b.setName(block.getAttributes().get("name"));
            b.setJobEngineProjectID(block.getProjectId());
            b.setWorkflowId(block.getWorkflowId());
            b.setJobEngineElementID(block.getId());
            updateMailTask(b);
        }

    }
    public void updateStartBlock(StartBlock b) {
    }

    public void updateEndBlock(EndBlock b) {
    }

    public void updateEventGateway(EventGatewayBlock b) {
    }

    public void updateMessageCatchEvent(MessageCatchEvent b) {
    }

    public void updateExclusiveGateway(ExclusiveGatewayBlock b) {
    }

    public void updateScript(ScriptBlock b) {
    }

    public void updateParallelGateway(ParallelGatewayBlock b) {
    }

    public void updateDbTask(DBWriteBlock b) {
    }

    public void updateMailTask(MailBlock b) {
    }


}
