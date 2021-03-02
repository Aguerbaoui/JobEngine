package io.je.project.beans;

import blocks.WorkflowBlock;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.components.blocks.Block;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.RuleBuilderErrors;
import io.je.utilities.exceptions.*;
import models.JEWorkflow;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Document(collection="JEProject")
public class JEProject {

    /*
    * Project ID
    * */
    @Id
    private String projectId;



    /*
    * Configuration path
    * */
    private String configurationPath;

    /*
    * Rules in a project
    * */
    private ConcurrentHashMap<String, JERule> rules;

    /*
    * workflows in a project
    * */
    private ConcurrentHashMap<String, JEWorkflow> workflows;
    
    
    /*
     * Events in a project
     * */
     private ConcurrentHashMap<String, JEEvent> events;
     
     
     private boolean isRunning=false;
     
     private boolean isBuilt=false;
     

    /*
    * project Status
    * */
     
    
    

    /*
    * Constructor
    * */
    public JEProject(String projectId, String configurationPath) {
        rules = new ConcurrentHashMap<>();
        workflows = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
        this.projectId = projectId;
        this.configurationPath = configurationPath;
        isBuilt = false;

    }

    
	/******************************************************** PROJECT **********************************************************************/

    
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




	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}


	public void setBuilt(boolean isBuilt) {
		this.isBuilt = isBuilt;
	}


	public String getConfigurationPath() {
		return configurationPath;
	}

	public void setConfigurationPath(String configurationPath) {
		this.configurationPath = configurationPath;
	}

	public boolean isBuilt() {
	/*	for(JERule rule : this.getRules().values())
		{
			if(!rule.isBuilt())
			{
				isBuilt=false;
				break;

			}
		}

		for(JEWorkflow workflow: workflows.values()) {
		    if(!workflow.getStatus().equals(JEWorkflow.BUILT)) {
				isBuilt=false;
		        break;
            }
        }
	*/	
		return isBuilt;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
 
	/******************************************************** RULES **********************************************************************/


    /*
    * Get project rules
    * */
    public ConcurrentHashMap<String, JERule> getRules() {
		return rules;
	}

	/*
	* Set project rules
	* */
	public void setRules(ConcurrentHashMap<String, JERule> rules) {
		isBuilt=false;	
		this.rules = rules;
	}

	
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
	        isBuilt=false;

	    }
	    
	    /*
	     * update rule to project
	     */
	    public void updateRule(JERule rule) throws RuleNotFoundException {
	    	if(!rules.containsKey(rule.getJobEngineElementID()))
	    			{
	    				throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
	    			}
	        rules.put(rule.getJobEngineElementID(), rule);
			rule.setJeObjectLastUpdate( LocalDateTime.now());
			isBuilt=false;


	    }

	    /*
	    * Add a block to a rule
	    * */
	    public void addBlockToRule(Block block) throws AddRuleBlockException 
	    	
	    {	
	    	((UserDefinedRule) rules.get(block.getJobEngineElementID())).addBlock(block);
	    	isBuilt=false;

	    }
	    

	    /*
	     * update Block
	     */
		public void updateRuleBlock(Block block) throws AddRuleBlockException {
	    	((UserDefinedRule) rules.get(block.getJobEngineElementID())).updateBlock(block);
	    	isBuilt=false;

			
		}
		
		/*
		 * delete Block
		 */

		public void deleteRuleBlock(String ruleId, String blockId) throws RuleBlockNotFoundException {
			((UserDefinedRule) rules.get(ruleId)).deleteBlock(blockId);
			rules.get(ruleId).setJeObjectLastUpdate(  LocalDateTime.now());
			isBuilt=false;

			
		}

		/*
		 * delete rule
		 */
		public void deleteRule(String ruleId) throws RuleNotFoundException {
			if(!rules.containsKey(ruleId))
			{
				throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
			}
			//TODO: delete file
			rules.remove(ruleId);
			isBuilt=false;

			
		}

	
	/******************************************************** Workflows **********************************************************************/

	
	/*
	* Get all workflows
	* */
	public ConcurrentHashMap<String, JEWorkflow> getWorkflows() {
        return workflows;
    }

    /*
    * Set all workflows
    * */
    public void setWorkflows(ConcurrentHashMap<String, JEWorkflow> workflows) {
    	isBuilt=false;
        this.workflows = workflows;
    }

    /*
    * Add a workflow
    * */
    public void addWorkflow(JEWorkflow wf) {
        this.workflows.put(wf.getJobEngineElementID(), wf);
        isBuilt=false;

    }

    /*
     * Remove a workflow
     */
    public void removeWorkflow(String id) {
        JEWorkflow wf = workflows.get(id);
        workflows.remove(id);
        wf = null;
        isBuilt=false;

    }

    /*
    * Checks if a workflow exists
    * */
    public boolean workflowExists(String workflowId) {
        if(workflows.containsKey(workflowId)) {
            workflows.get(workflowId).setJeObjectLastUpdate(LocalDateTime.now());
            return true;
        }
        return false;
    }

    /*
    * Add a block to a workflow
    * */
    public void addBlockToWorkflow(WorkflowBlock block) {
        workflows.get(block.getWorkflowId()).addBlock(block);
		isBuilt=false;

    }

    /*
    * Delete a workflow block
    * */
    public void deleteWorkflowBlock(String workflowId, String blockId) throws InvalidSequenceFlowException, WorkflowBlockNotFound {
        workflows.get(workflowId).deleteWorkflowBlock(blockId);
		isBuilt=false;

    }

    /*
    * Delete a workflow sequence flow
    * */
    public void deleteWorkflowSequenceFlow(String workflowId, String sourceRef, String targetRef) throws InvalidSequenceFlowException {
        workflows.get(workflowId).deleteSequenceFlow(sourceRef, targetRef);
		isBuilt=false;

    }

    /*
    * Add a workflow sequence flow
    * */
    public void addWorkflowSequenceFlow(String workflowId, String sourceRef, String targetRef, String condition) throws WorkflowBlockNotFound {
        if(! workflows.get(workflowId).blockExists(sourceRef) || !workflows.get(workflowId).blockExists(targetRef)) {
            throw new WorkflowBlockNotFound( Errors.WORKFLOW_BLOCK_NOT_FOUND);
        }
        workflows.get(workflowId).addBlockFlow(sourceRef, targetRef, condition);
		isBuilt=false;

    }

    /*
    * Get a workflow id
    * */
    public JEWorkflow getWorkflowById(String workflowId) {
        return workflows.get(workflowId);
    }
    


  







	

	/******************************************************** EVENTS **********************************************************************/
	
	
	public boolean eventExists(String eventId)
	{
		return events.containsKey(eventId);
	}
	public void addEvent(JEEvent event)
	{
		events.put(event.getJobEngineElementID(), event);
	}
	
	public JEEvent getEvent(String eventId) throws EventException
	{
		if(!eventExists(eventId))
		{
			throw new EventException(Errors.EVENT_NOT_FOUND);
		}
		return events.get(eventId);
	}


	public ConcurrentHashMap<String, JEEvent> getEvents() {
		return events;
	}


	public void setEvents(ConcurrentHashMap<String, JEEvent> events) {
		this.events = events;
	}





}
