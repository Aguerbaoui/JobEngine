package io.je.ruleengine.listener;

import org.kie.api.event.rule.*;

public class RuleListener extends DefaultAgendaEventListener {

    @Override
    public void matchCreated(MatchCreatedEvent event) {
        // TODO Auto-generated method stub

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
        super.afterMatchFired(event);

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

}
