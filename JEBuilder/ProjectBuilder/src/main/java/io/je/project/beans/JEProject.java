package io.je.project.beans;

import blocks.WorkflowBlock;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.AddRuleBlockException;
import io.je.utilities.exceptions.InvalidSequenceFlowException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleBlockNotFoundException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.WorkflowBlockNotFound;
import io.je.utilities.network.Network;
import models.JEWorkflow;

import java.util.HashMap;

public class JEProject {

    private String projectId;

    private String projectName;
    
    private String configurationPath;

    private HashMap<String, UserDefinedRule> rules;

    private HashMap<String, JEWorkflow> workflows;

    private boolean running = false;

    public JEProject(String projectId, String projectName, String configurationPath) {
        rules = new HashMap<String, UserDefinedRule>();
        workflows = new HashMap<String, JEWorkflow>();
        this.projectId = projectId;
        this.projectName = projectName;
        this.configurationPath = configurationPath;

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



    public HashMap<String, UserDefinedRule> getRules() {
		return rules;
	}

	public void setRules(HashMap<String, UserDefinedRule> rules) {
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

    /*
     * Workflow Management 
     */

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
    
    /*
     * Rule Management 
     */
    
    
    
    public boolean ruleExists(String ruleId)
    {
    	return rules.containsKey(ruleId);
    }
    
    /*
     * add a new rule to project
     */
    public void addRule(UserDefinedRule rule) throws RuleAlreadyExistsException {
    	if(rules.containsKey(rule.getJobEngineElementID()))
    			{
    				throw new RuleAlreadyExistsException("", "");
    			}
        this.rules.put(rule.getJobEngineElementID(), rule);
    }
    
    public void addBlockToRule(BlockModel blockModel) throws AddRuleBlockException 
    	
    {	
    	rules.get(blockModel.getRuleId()).addBlock(blockModel);
    }
    
    /*
     * build rule : drl generation + compilation
     */
    public void buildRule(String ruleId) throws RuleBuildFailedException
    {
    	RuleBuilder.buildRule(rules.get(ruleId), configurationPath);
    }

    /*
     * update Block
     */
	public void updateRuleBlock(BlockModel blockModel) throws AddRuleBlockException {
		rules.get(blockModel.getRuleId()).updateBlock(blockModel);
		
	}
	
	/*
	 * delete Block
	 */

	public void deleteRuleBlock(String ruleId, String blockId) throws RuleBlockNotFoundException {
		rules.get(ruleId).deleteBlock(blockId);
		
	}

	/*
	 * delete rule
	 */
	public void deleteRule(String ruleId) {
		//TODO: send request to JERunner to delete Rule
		rules.remove(ruleId);
		
	}

	/*
	 * update rule attributes
	 */
	public void updateRuleAttributes(UserDefinedRule rule) {
		UserDefinedRule ruleToUpdate = rules.get(rule.getJobEngineElementID());
		ruleToUpdate.setSalience(rule.getSalience());
		ruleToUpdate.setDateEffective(rule.getDateEffective());
		ruleToUpdate.setDateExpires(rule.getDateExpires());
		ruleToUpdate.setTimer(rule.getTimer());
		ruleToUpdate.setEnabled(rule.isEnabled());
		
		
		
	}

}
