package io.je.project.services;

import blocks.WorkflowBlock;
import blocks.basic.*;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.MessageCatchEvent;
import builder.WorkflowBuilder;
import io.je.project.beans.JEProject;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
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
            throw new ProjectNotFoundException(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound);
        }
        project.addWorkflow(wf);
    }
    /*
    * Remove a workflow from a project
    * */
    public void removeWorkflow(String projectId, String workflowId) throws ProjectNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound);
        }
        project.removeWorkflow(workflowId);
    }
    /*
    * Add a workflow block to a workflow
    * */
    public void addWorkflowBlock(WorkflowBlock block) throws  ProjectNotFoundException, WorkflowNotFoundException {
        JEProject project = ProjectService.getProjectById(block.getJobEngineProjectID());
        if(project == null) {
            throw new ProjectNotFoundException(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound);
        }
        else if (!project.workflowExists(block.getWorkflowId())) {
            throw new WorkflowNotFoundException(APIConstants.WORKFLOW_NOT_FOUND, Errors.workflowNotFound);
        }
        project.addBlockToWorkflow(block);
    }

    /*
    * Delete a workflow block
    * */
    public void deleteWorkflowBlock(String projectId, String workflowId, String blockId) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound);
        }else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(APIConstants.WORKFLOW_NOT_FOUND, Errors.workflowNotFound);
        }

        project.deleteWorkflowBlock(workflowId, blockId);
    }

    /*
    * Delete a Sequence flow
    * */
    public void deleteSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(APIConstants.WORKFLOW_NOT_FOUND, Errors.workflowNotFound);
        }

        project.deleteWorkflowSequenceFlow(workflowId, sourceRef, targetRef);
    }

    /*
    * Add a Sequence flow
    * */
    public void addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef, String condition) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(APIConstants.WORKFLOW_NOT_FOUND, Errors.workflowNotFound);
        }

        project.addWorkflowSequenceFlow(workflowId, sourceRef, targetRef, condition);
    }

    /*
    * Build a workflow
    * */
    public void buildWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(APIConstants.WORKFLOW_NOT_FOUND, Errors.workflowNotFound);
        }

        WorkflowBuilder.buildWorkflow(project.getWorkflowById(workflowId));
    }

    /*
     * Build all workflow
     * */
    public void buildWorkflows(String projectId) throws ProjectNotFoundException, IOException {
        JEProject project = ProjectService.getProjectById(projectId);
        if(project == null) {
            throw new ProjectNotFoundException(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound);
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
            throw new ProjectNotFoundException(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound);
        } else if (!project.workflowExists(workflowId)) {
            throw new WorkflowNotFoundException(APIConstants.WORKFLOW_NOT_FOUND, Errors.workflowNotFound);
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
            throw new ProjectNotFoundException(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound);
        }
        for(JEWorkflow wf: project.getWorkflows().values()) {
            WorkflowBuilder.runWorkflow(wf.getWorkflowName().trim());
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
