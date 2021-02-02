package io.je.project.beans;

import blocks.WorkflowBlock;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.RuleBuilderErrors;
import io.je.utilities.exceptions.AddRuleBlockException;
import io.je.utilities.exceptions.EventException;
import io.je.utilities.exceptions.InvalidSequenceFlowException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleBlockNotFoundException;
import io.je.utilities.exceptions.RuleNotFoundException;
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
    private HashMap<String, JERule> rules;

    /*
    * workflows in a project
    * */
    private HashMap<String, JEWorkflow> workflows;
    
    
    /*
     * Events in a project
     * */
     private HashMap<String, JEEvent> events;

    /*
    * Is the project running
    * */
    private boolean isRunning = false;
    
    /*
    * Is the project built \\TODO: set true during build, set false everytime rules/wfs get added/updated/deleted 
    * */
    private boolean isBuilt = false;

    /*
    * Constructor
    * */
    public JEProject(String projectId, String projectName, String configurationPath) {
        rules = new HashMap<>();
        workflows = new HashMap<>();
        events = new HashMap<>();
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
    public HashMap<String, JERule> getRules() {
		return rules;
	}

	/*
	* Set project rules
	* */
	public void setRules(HashMap<String, JERule> rules) {
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
    
    public JERule getRule(String ruleId)
    {
    	return rules.get(ruleId);
    }
    
    /*
     * add a new rule to project
     */
    public void addRule(JERule rule) throws RuleAlreadyExistsException {
    	if(rules.containsKey(rule.getJobEngineElementID()))
    			{
    				throw new RuleAlreadyExistsException(RuleBuilderErrors.RuleAlreadyExists);
    			}
        this.rules.put(rule.getJobEngineElementID(), rule);
    }
    
    /*
     * update rule to project
     */
    public void updateRule(JERule rule) throws RuleNotFoundException {
    	if(!rules.containsKey(rule.getJobEngineElementID()))
    			{
    				throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
    			}
        this.rules.put(rule.getJobEngineElementID(), rule);
    }

    /*
    * Add a block to a rule
    * */
    public void addBlockToRule(BlockModel blockModel) throws AddRuleBlockException 
    	
    {	
    	((UserDefinedRule) rules.get(blockModel.getRuleId())).addBlock(blockModel);
    }
    

    /*
     * update Block
     */
	public void updateRuleBlock(BlockModel blockModel) throws AddRuleBlockException {
		((UserDefinedRule) rules.get(blockModel.getRuleId())).updateBlock(blockModel);
		
	}
	
	/*
	 * delete Block
	 */

	public void deleteRuleBlock(String ruleId, String blockId) throws RuleBlockNotFoundException {
		((UserDefinedRule) rules.get(ruleId)).deleteBlock(blockId);
		
	}

	/*
	 * delete rule
	 */
	public void deleteRule(String ruleId) {
		//TODO: send request to JERunner to delete Rule 
		//TODO: delete file
		rules.remove(ruleId);
		
	}

	/*
	 * update rule attributes
	 */
	public void updateRuleAttributes(RuleModel ruleModel) {
		UserDefinedRule ruleToUpdate = (UserDefinedRule) rules.get(ruleModel.getRuleId());
		ruleToUpdate.setRuleName(ruleModel.getRuleName());
		ruleToUpdate.setDescription(ruleModel.getDescription());
		ruleToUpdate.getRuleParameters().setSalience(String.valueOf(ruleModel.getSalience()));
		ruleToUpdate.getRuleParameters().setDateEffective(ruleModel.getDateEffective());
		ruleToUpdate.getRuleParameters().setDateExpires(ruleModel.getDateExpires());
		ruleToUpdate.getRuleParameters().setEnabled(ruleModel.getEnabled());
		ruleToUpdate.getRuleParameters().setTimer(ruleModel.getTimer());
		ruleToUpdate.setBuilt(false);
		rules.put(ruleModel.getRuleId(), ruleToUpdate);

	}
	 





	public boolean isBuilt() {
		//TODO: check for unbuilt workflows
		for(JERule rule : this.getRules().values())
		{
			if(!rule.isBuilt())
			{
				isBuilt = false;
				//JELogger.info("Rule Not built : " + rule.getRuleName());
			}
		}
		
		return isBuilt;
	}

	public void setBuilt(boolean isBuilt) {
		this.isBuilt = isBuilt;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public String getConfigurationPath() {
		return configurationPath;
	}

	public void setConfigurationPath(String configurationPath) {
		this.configurationPath = configurationPath;
	}

	

	/******************************************************** EVENTS **********************************************************************/
	
	
	public boolean eventExists(String eventId)
	{
		return events.containsKey(eventId);
	}
	public void addEvent(String eventId)
	{
		
	}
	
	public JEEvent getEvent(String eventId) throws EventException
	{
		if(!eventExists(eventId))
		{
			throw new EventException(Errors.EVENT_NOT_FOUND);
		}
		return events.get(eventId);
	}
	
	public HashMap<String, JEEvent> getEvents() {
		return events;
	}

	public void setEvents(HashMap<String, JEEvent> events) {
		this.events = events;
	}

	public void updateRuleName(String ruleId, String ruleName) {
		rules.get(ruleId).setRuleName(ruleName);
		rules.get(ruleId).setBuilt(false);
		
	}
	
}
