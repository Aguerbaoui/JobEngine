package io.je.project.beans;

import blocks.WorkflowBlock;
import io.je.classbuilder.builder.ClassBuilder;
import io.je.classbuilder.entity.JEClass;
import io.je.classbuilder.models.ClassModel;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.RuleBuilderErrors;
import io.je.utilities.exceptions.AddRuleBlockException;
import io.je.utilities.exceptions.ClassFormatInvalidException;
import io.je.utilities.exceptions.InvalidSequenceFlowException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleBlockNotFoundException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.WorkflowBlockNotFound;
import models.JEWorkflow;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Document(collection="JEProject")
public class JEProject {

    /*
    * Project ID
    * */
    @Id
    private String projectId;

    /*
    * Project name
    * */
    private String projectName;

    /*
    * Configuration path
    * */
    private String configurationPath;

    /*
    * Rules in a project
    * */
    private HashMap<String, UserDefinedRule> rules;

    /*
    * workflows in a project
    * */
    private HashMap<String, JEWorkflow> workflows;

    /*
    * List of classes used in the project
    * */
    private HashMap<String, JEClass> classes;

    /*
    * Is the project running
    * */
    private boolean running = false;

    /*
    * Constructor
    * */
    public JEProject(String projectId, String projectName, String configurationPath) {
        rules = new HashMap<>();
        workflows = new HashMap<>();
        classes = new HashMap<>();
        this.projectId = projectId;
        this.projectName = projectName;
        this.configurationPath = configurationPath;

    }

    /*
    * Get project Id
    * */
    public String getProjectId() {
        return projectId;
    }

    /*
    * Set project id
    * */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /*
    * Get project name
    * */
    public String getProjectName() {
        return projectName;
    }

    /*
    * Set project name
    * */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /*
    * Get project rules
    * */
    public HashMap<String, UserDefinedRule> getRules() {
		return rules;
	}

	/*
	* Set project rules
	* */
	public void setRules(HashMap<String, UserDefinedRule> rules) {
		this.rules = rules;
	}

	/*
	* Get all workflows
	* */
	public HashMap<String, JEWorkflow> getWorkflows() {
        return workflows;
    }

    /*
    * Set all workflows
    * */
    public void setWorkflows(HashMap<String, JEWorkflow> workflows) {
        this.workflows = workflows;
    }

    /*
    * Add a workflow
    * */
    public void addWorkflow(JEWorkflow wf) {
        this.workflows.put(wf.getJobEngineElementID(), wf);
    }

    /*
     * Remove a workflow
     */
    public void removeWorkflow(String id) {
        JEWorkflow wf = workflows.get(id);
        workflows.remove(id);
        wf = null;
    }

    /*
    * Checks if a workflow exists
    * */
    public boolean workflowExists(String workflowId) {
        return workflows.containsKey(workflowId);
    }

    /*
    * Add a block to a workflow
    * */
    public void addBlockToWorkflow(WorkflowBlock block) {
        workflows.get(block.getWorkflowId()).addBlock(block);
    }

    /*
    * Delete a workflow block
    * */
    public void deleteWorkflowBlock(String workflowId, String blockId) throws InvalidSequenceFlowException, WorkflowBlockNotFound {
        workflows.get(workflowId).deleteWorkflowBlock(blockId);
    }

    /*
    * Delete a workflow sequence flow
    * */
    public void deleteWorkflowSequenceFlow(String workflowId, String sourceRef, String targetRef) throws InvalidSequenceFlowException {
        workflows.get(workflowId).deleteSequenceFlow(sourceRef, targetRef);
    }

    /*
    * Add a workflow sequence flow
    * */
    public void addWorkflowSequenceFlow(String workflowId, String sourceRef, String targetRef, String condition) throws WorkflowBlockNotFound {
        if(! workflows.get(workflowId).blockExists(sourceRef) || !workflows.get(workflowId).blockExists(targetRef)) {
            throw new WorkflowBlockNotFound( Errors.workflowBlockNotFound);
        }
        workflows.get(workflowId).addBlockFlow(sourceRef, targetRef, condition);
    }

    /*
    * Get a workflow id
    * */
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
    				throw new RuleAlreadyExistsException(RuleBuilderErrors.RuleAlreadyExists);
    			}
        this.rules.put(rule.getJobEngineElementID(), rule);
    }

    /*
    * Add a block to a rule
    * */
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
	 
	
	/*
     * Class Management 
     */
    
	/*
	 * add class
	 */
	public void addClass(ClassModel classModel) throws ClassFormatInvalidException
	{
		ClassBuilder.buildClass(classModel, configurationPath);
		classes.put(classModel.get_id(), new JEClass(classModel.get_id(),projectId,classModel.getName()));
		
	}

}
