package io.je.ruleengine.listener;

import io.je.utilities.beans.JEBlockMessage;
import io.je.utilities.beans.JEMessage;
import io.je.utilities.ruleutils.IdManager;
import io.je.utilities.runtimeobject.JEObject;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.*;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.RuleContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Work in progress for the object viewer
 **/
public class AgendaEventListener extends DefaultAgendaEventListener {

    private String projectId;
    //private  HashMap<String,RuleMatch> ruleMatches = new HashMap<String, RuleMatch>();

    public AgendaEventListener(String projectId) {
        super();
        this.projectId = projectId;
    }


    @Override
    public void matchCreated(MatchCreatedEvent event) {

        System.err.println("Instant.now().getNano() : " + Instant.now().getNano());
        System.err.println("getSessionClock getCurrentTime : " + event.getKieRuntime().getSessionClock().getCurrentTime());

        System.err.println("MatchCreatedEvent getKieRuntime : " + event.getKieRuntime().getWorkItemManager().toString());

        if (event.getKieRuntime().getObjects() != null) {
            for (Object object : event.getKieRuntime().getObjects()) {
                System.err.println("MatchCreatedEvent getKieRuntime object : " + object);
            }
        }
        /*
        if (event.getKieRuntime().getProcessInstances() != null) {
            for (ProcessInstance processInstance : event.getKieRuntime().getProcessInstances()) {
                System.err.println("getKieRuntime Id : " + processInstance.getId());
                System.err.println("getKieRuntime process Id : " + processInstance.getProcessId());
                System.err.println("getKieRuntime process Name : " + processInstance.getProcessName());
                System.err.println("getKieRuntime process getParentProcessInstanceId : " + processInstance.getParentProcessInstanceId());
                System.err.println("getKieRuntime process getState : " + processInstance.getState());
                System.err.println("getKieRuntime process toString : " + processInstance.toString());
            }
        }
        */
        System.err.println("getEntryPointId : " + event.getKieRuntime().getEntryPointId());
        System.err.println("getSessionConfiguration : " + event.getKieRuntime().getSessionConfiguration().toString());
        System.err.println("getFactHandles : " + event.getKieRuntime().getFactHandles());
        for (FactHandle factHandle : event.getKieRuntime().getFactHandles()) {
            System.err.println("factHandle : " + factHandle.toExternalForm());
        }
        System.err.println("getCalendars : " + event.getKieRuntime().getCalendars());
        for (String channelKey : event.getKieRuntime().getChannels().keySet()) {
            System.err.println("channel key : " + channelKey);
            System.err.println("channel value : " + event.getKieRuntime().getChannels().get(channelKey));
        }
        System.err.println("getEnvironment : " + event.getKieRuntime().getEnvironment());
        System.err.println("getGlobals : " + event.getKieRuntime().getGlobals().getGlobalKeys());
        for (String globalKey : event.getKieRuntime().getGlobals().getGlobalKeys()) {
            System.err.println("global key : " + globalKey);
            System.err.println("global value : " + event.getKieRuntime().getGlobals().get(globalKey));
        }
        for (Process process : event.getKieRuntime().getKieBase().getProcesses()) {
            System.err.println("process Id : " + process.getId());
            System.err.println("process Name : " + process.getName());
            System.err.println("process Package name: " + process.getPackageName());
            System.err.println("process type : " + process.getType());
            System.err.println("process version : " + process.getVersion());
        }

        System.err.println("MatchCreatedEvent : " + event.getMatch().getRule());

        for (String declarationId : event.getMatch().getDeclarationIds()) {
            System.err.println("MatchCreatedEvent declarationId : " + declarationId);
            System.err.println("MatchCreatedEvent declarationValue : " + event.getMatch().getDeclarationValue(declarationId));
        }

        for (Object object : event.getMatch().getObjects()) {
            System.err.println("MatchCreatedEvent object : " + object);
        }

        for (FactHandle factHandle : event.getMatch().getFactHandles()) {
            System.err.println("MatchCreatedEvent factHandle : " + factHandle.toExternalForm());
        }


        RuleContext ruleContext = new RuleContext() {
            @Override
            public Rule getRule() {
                return event.getMatch().getRule();
            }

            @Override
            public Match getMatch() {
                return event.getMatch();
            }

            @Override
            public FactHandle insertLogical(Object object) {
                return null;
            }

            @Override
            public FactHandle insertLogical(Object object, Object value) {
                return null;
            }

            @Override
            public void blockMatch(Match match) {

            }

            @Override
            public void unblockAllMatches(Match match) {

            }

            @Override
            public void cancelMatch(Match match) {
                System.err.println("Event Cancelled in MatchCreatedEvent");
            }

            @Override
            public KieRuntime getKieRuntime() {
                return null;
            }

            @Override
            public KieRuntime getKnowledgeRuntime() {
                return null;
            }
        };

        ruleContext.cancelMatch(event.getMatch());

        System.err.println("MatchCreatedEvent getKieRuntime getKieBase getProcesses AFTER CANCEL MATCH : ");

        for (Process process : event.getKieRuntime().getKieBase().getProcesses()) {
            System.err.println("process Id : " + process.getId());
            System.err.println("process Name : " + process.getName());
            System.err.println("process Package name: " + process.getPackageName());
            System.err.println("process type : " + process.getType());
            System.err.println("process version : " + process.getVersion());
        }

    }

    @Override
    public void matchCancelled(MatchCancelledEvent event) {
        System.err.println("Instant.now().getNano() : " + Instant.now().getNano());
        System.err.println("getSessionClock getCurrentTime : " + event.getKieRuntime().getSessionClock().getCurrentTime());

        System.err.println("MatchCancelledEvent : " + event.getKieRuntime().toString());

        System.err.println("MatchCancelledEvent : " + event.getMatch().getRule());

        for (String declarationId : event.getMatch().getDeclarationIds()) {
            System.err.println("MatchCancelledEvent declarationId : " + declarationId);
            System.err.println("MatchCancelledEvent declarationValue : " + event.getMatch().getDeclarationValue(declarationId));
        }

        for (Object object : event.getMatch().getObjects()) {
            System.err.println("MatchCancelledEvent object : " + object);
        }

        for (FactHandle factHandle : event.getMatch().getFactHandles()) {
            System.err.println("MatchCancelledEvent factHandle : " + factHandle.toExternalForm());
        }
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {

        System.err.println("Instant.now().getNano() : " + Instant.now().getNano());
        System.err.println("getSessionClock getCurrentTime : " + event.getKieRuntime().getSessionClock().getCurrentTime());

        System.err.println("BeforeMatchFiredEvent : " + event.getKieRuntime().toString());

        System.err.println("BeforeMatchFiredEvent : " + event.getMatch().getRule());

        for (String declarationId : event.getMatch().getDeclarationIds()) {
            System.err.println("BeforeMatchFiredEvent declarationId : " + declarationId);
            System.err.println("BeforeMatchFiredEvent declarationValue : " + event.getMatch().getDeclarationValue(declarationId));
        }

        for (Object object : event.getMatch().getObjects()) {
            System.err.println("BeforeMatchFiredEvent object : " + object);
        }

        for (FactHandle factHandle : event.getMatch().getFactHandles()) {
            System.err.println("BeforeMatchFiredEvent factHandle : " + factHandle.toExternalForm());
        }

        System.err.println("BeforeMatchFiredEvent getKieRuntime getKieBase PROCESS : ");
        for (Process process : event.getKieRuntime().getKieBase().getProcesses()) {
            System.err.println("process Id : " + process.getId());
            System.err.println("process Name : " + process.getName());
            System.err.println("process Package name: " + process.getPackageName());
            System.err.println("process type : " + process.getType());
            System.err.println("process version : " + process.getVersion());
        }

        RuleContext ruleContext = new RuleContext() {
            @Override
            public Rule getRule() {
                return event.getMatch().getRule();
            }

            @Override
            public Match getMatch() {
                return event.getMatch();
            }

            @Override
            public FactHandle insertLogical(Object object) {
                return null;
            }

            @Override
            public FactHandle insertLogical(Object object, Object value) {
                return null;
            }

            @Override
            public void blockMatch(Match match) {

            }

            @Override
            public void unblockAllMatches(Match match) {

            }

            @Override
            public void cancelMatch(Match match) {
                System.err.println("Event Cancelled in BeforeMatchFiredEvent");
            }

            @Override
            public KieRuntime getKieRuntime() {
                return null;
            }

            @Override
            public KieRuntime getKnowledgeRuntime() {
                return null;
            }
        };

        ruleContext.cancelMatch(event.getMatch());

        System.err.println("BeforeMatchFiredEvent getKieRuntime getKieBase getProcesses AFTER CANCEL MATCH : ");

        for (Process process : event.getKieRuntime().getKieBase().getProcesses()) {
            System.err.println("process Id : " + process.getId());
            System.err.println("process Name : " + process.getName());
            System.err.println("process Package name: " + process.getPackageName());
            System.err.println("process type : " + process.getType());
            System.err.println("process version : " + process.getVersion());
        }

    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        System.err.println("Instant.now().getNano() : " + Instant.now().getNano());
        System.err.println("getSessionClock getCurrentTime : " + event.getKieRuntime().getSessionClock().getCurrentTime());

        System.err.println("AfterMatchFiredEvent : " + event.getKieRuntime().toString());

        System.err.println("AfterMatchFiredEvent : " + event.getMatch().getRule());

        for (String declarationId : event.getMatch().getDeclarationIds()) {
            System.err.println("AfterMatchFiredEvent declarationId : " + declarationId);
            System.err.println("AfterMatchFiredEvent declarationValue : " + event.getMatch().getDeclarationValue(declarationId));
        }

        for (Object object : event.getMatch().getObjects()) {
            System.err.println("AfterMatchFiredEvent object : " + object);
        }

        for (FactHandle factHandle : event.getMatch().getFactHandles()) {
            System.err.println("AfterMatchFiredEvent factHandle : " + factHandle.toExternalForm());
        }

        JEMessage ruleMessage = new JEMessage();
        ruleMessage.setType("RuleExecutionMessage");
        String ruleId = IdManager.retrieveIdFromSubRuleName(event.getMatch()
                .getRule()
                .getName());
        ruleMessage.setExecutionTime(LocalDateTime.now()
                .toString());
        List<String> instances = new ArrayList<>();
        for (Object instance : event.getMatch()
                .getObjects()) {
            if (instance instanceof JEObject) {
                JEObject jeInstance = (JEObject) instance;
                //TODO switch to name maybe?
                instances.add(jeInstance.getJobEngineElementID());
            }

        }
        ruleMessage.setInstanceNames(instances);

        //get declared variables
        for (String declaredVariableName : event.getMatch()
                .getDeclarationIds()) {
    		/*if(event.getMatch().getDeclarationValue(declaredVariableName) instanceof JEObject)
    		{
    			
    		}*/
            ruleMessage.addBlockMessage(new JEBlockMessage(declaredVariableName, event.getMatch()
                    .getDeclarationValue(declaredVariableName)
                    .toString()));
        }

        //JELogger.debug("Rule["+ IdManager.retrieveNameFromSubRuleName(event.getMatch().getRule().getName())+"] was fired",LogCategory.NOT_ASSIGNED , ruleId, LogSubModule.RULE, ruleId);
        //Executioner.informRuleBlock( projectId,  ruleId, "Rule was fired",LocalDateTime.now().toString(), "APP");

    	
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

        RuleContext ruleContext = new RuleContext() {
            @Override
            public Rule getRule() {
                return event.getMatch().getRule();
            }

            @Override
            public Match getMatch() {
                return event.getMatch();
            }

            @Override
            public FactHandle insertLogical(Object object) {
                return null;
            }

            @Override
            public FactHandle insertLogical(Object object, Object value) {
                return null;
            }

            @Override
            public void blockMatch(Match match) {

            }

            @Override
            public void unblockAllMatches(Match match) {

            }

            @Override
            public void cancelMatch(Match match) {
                System.err.println("Event Cancelled in AfterMatchFiredEvent");
            }

            @Override
            public KieRuntime getKieRuntime() {
                return null;
            }

            @Override
            public KieRuntime getKnowledgeRuntime() {
                return null;
            }
        };

        ruleContext.cancelMatch(event.getMatch());

        System.err.println("AfterMatchFiredEvent getKieRuntime getKieBase getProcesses AFTER CANCEL MATCH : ");

        for (Process process : event.getKieRuntime().getKieBase().getProcesses()) {
            System.err.println("process Id : " + process.getId());
            System.err.println("process Name : " + process.getName());
            System.err.println("process Package name: " + process.getPackageName());
            System.err.println("process type : " + process.getType());
            System.err.println("process version : " + process.getVersion());
        }


        System.err.println("Instant.now().getNano() : " + Instant.now().getNano());
        System.err.println("getSessionClock getCurrentTime : " + event.getKieRuntime().getSessionClock().getCurrentTime());

        System.err.println("AfterMatchFiredEvent : " + event.getKieRuntime().toString());

        System.err.println("AfterMatchFiredEvent : " + event.getMatch().getRule());

        for (String declarationId : event.getMatch().getDeclarationIds()) {
            System.err.println("AfterMatchFiredEvent declarationId : " + declarationId);
            System.err.println("AfterMatchFiredEvent declarationValue : " + event.getMatch().getDeclarationValue(declarationId));
        }

        for (Object object : event.getMatch().getObjects()) {
            System.err.println("AfterMatchFiredEvent object : " + object);
        }

        for (FactHandle factHandle : event.getMatch().getFactHandles()) {
            System.err.println("AfterMatchFiredEvent factHandle : " + factHandle.toExternalForm());
        }

    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        System.err.println("AgendaGroupPoppedEvent : " + event.getKieRuntime().getSessionConfiguration().toString());
    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        System.err.println("AgendaGroupPushedEvent : " + event.getKieRuntime().getSessionConfiguration().toString());
    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        System.err.println("RuleFlowGroupActivatedEvent : " + event.getKieRuntime().getSessionConfiguration().toString());
    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        System.err.println("RuleFlowGroupActivatedEvent : " + event.getKieRuntime().getSessionConfiguration().toString());
    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        System.err.println("RuleFlowGroupDeactivatedEvent : " + event.getKieRuntime().getSessionConfiguration().toString());
    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        System.err.println("RuleFlowGroupDeactivatedEvent : " + event.getKieRuntime().getSessionConfiguration().toString());
    }


    public void executionReached(String executionId) {
        int i = 0;
    }

}
