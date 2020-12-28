package io.je.project.beans;

import blocks.WorkflowBlock;
import io.je.rulebuilder.components.JERule;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.InvalidSequenceFlowException;
import io.je.utilities.exceptions.WorkflowBlockNotFound;
import models.JEWorkflow;

import java.util.HashMap;

public class JEProject {

    private String projectId;

    private String projectName;

    private HashMap<String, JERule> rules;

    private HashMap<String, JEWorkflow> workflows;

    private boolean running = false;

    public JEProject(String projectId, String projectName) {
        rules = new HashMap<String, JERule>();
        workflows = new HashMap<String, JEWorkflow>();
        this.projectId = projectId;
        this.projectName = projectName;

    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public HashMap<String, JERule> getRules() {
        return rules;
    }

    public void setRules(HashMap<String, JERule> rules) {
        this.rules = rules;
    }

    public HashMap<String, JEWorkflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(HashMap<String, JEWorkflow> workflows) {
        this.workflows = workflows;
    }

    public void addWorkflow(JEWorkflow wf) {
        this.workflows.put(wf.getJobEngineElementID(), wf);
    }

    public void addRule(JERule rule) {
        this.rules.put(rule.getJobEngineElementID(), rule);
    }

    public void removeWorkflow(String id) {
        JEWorkflow wf = workflows.get(id);
        workflows.remove(id);
        wf = null;
    }
    public boolean workflowExists(String workflowId) {
        return workflows.containsKey(workflowId);
    }

    public void addBlockToWorkflow(WorkflowBlock block) {
        workflows.get(block.getWorkflowId()).addBlock(block);
    }

    public void deleteWorkflowBlock(String workflowId, String blockId) throws InvalidSequenceFlowException, WorkflowBlockNotFound {
        workflows.get(workflowId).deleteWorkflowBlock(blockId);
    }

    public void deleteWorkflowSequenceFlow(String workflowId, String sourceRef, String targetRef) throws InvalidSequenceFlowException {
        workflows.get(workflowId).deleteSequenceFlow(sourceRef, targetRef);
    }

    public void addWorkflowSequenceFlow(String workflowId, String sourceRef, String targetRef, String condition) throws WorkflowBlockNotFound {
        if(! workflows.get(workflowId).blockExists(sourceRef) || !workflows.get(workflowId).blockExists(targetRef)) {
            throw new WorkflowBlockNotFound(APIConstants.WORKFLOW_BLOCK_NOT_FOUND, Errors.workflowBlockNotFound);
        }
        workflows.get(workflowId).addBlockFlow(sourceRef, targetRef, condition);
    }

    public JEWorkflow getWorkflowById(String workflowId) {
        return workflows.get(workflowId);
    }
}
