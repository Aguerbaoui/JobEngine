package io.je.ruleengine.listener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.event.rule.*;

import io.je.ruleengine.models.RuleMatch;
import io.je.utilities.beans.JEBlockMessage;
import io.je.utilities.beans.JEMessage;
import io.je.utilities.execution.Executioner;
import io.je.utilities.ruleutils.RuleIdManager;
import io.je.utilities.runtimeobject.JEObject;

public class RuleListener extends DefaultAgendaEventListener {
	
	private String projectId;
	//private  HashMap<String,RuleMatch> ruleMatches = new HashMap<String, RuleMatch>();
	
	
	
	

    @Override
    public void matchCreated(MatchCreatedEvent event) {
        // TODO Auto-generated method stub

    }

    public RuleListener(String projectId) {
		super();
		this.projectId = projectId;
	}

	@Override
    public void matchCancelled(MatchCancelledEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
 
    	JEMessage ruleMessage = new JEMessage();
    	ruleMessage.setType("RuleExecutionMessage");
    	String ruleId=RuleIdManager.retrieveIdFromSubRuleName(event.getMatch().getRule().getName());
    	ruleMessage.setExecutionTime(LocalDateTime.now().toString());
    	List<String> instances = new ArrayList<>();
    	for(Object instance : event.getMatch().getObjects())
    	{
    		JEObject jeInstance = (JEObject) instance;
    		//TODO switch to name maybe?
    		instances.add(jeInstance.getJobEngineElementID());
    		
    	}
    	ruleMessage.setInstanceNames(instances);

    	//get declared variables 
    	for(String declaredVariableName : event.getMatch().getDeclarationIds())
    	{
    		ruleMessage.addBlockMessage(new JEBlockMessage(declaredVariableName,event.getMatch().getDeclarationValue(declaredVariableName).toString()));
    	}
    	
    	Executioner.informRuleBlock( projectId,ruleId, ruleMessage);
    	
    	
    	/*String ruleId=RuleIdManager.retrieveIdFromSubRuleName(event.getMatch().getRule().getName());
    	RuleMatch match = ruleMatches.get(ruleId);
    	if(match==null)
    	{
    		match = new RuleMatch(ruleId,projectId);
    	}
    	
    	//instances involved in match
    	List<JEObject> instances = new ArrayList<JEObject>();
    	for(Object instance : event.getMatch().getObjects())
    	{
    		JEObject jeInstance = (JEObject) instance;
    		instances.add(jeInstance);
    	}
    	//get declared variables 
    	Map<String,Object> declaredVariables = new HashMap<String,Object>();
    	for(String declaredVariableName : event.getMatch().getDeclarationIds())
    	{
    		declaredVariables.put(declaredVariableName,event.getMatch().getDeclarationValue(declaredVariableName));
    	}
    	match.setDeclaredVariables(declaredVariables);
    	match.setInstancesMatched(instances);
    	ruleMatches.put(ruleId, match);
    	//send match to monitoring
    	*/
    	
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        // TODO Auto-generated method stub

    }
    
    
    public void executionReached(String executionId) {
    	
    }

}
