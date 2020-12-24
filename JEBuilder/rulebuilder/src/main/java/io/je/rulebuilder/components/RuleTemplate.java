package io.je.rulebuilder.components;

public class RuleTemplate {
    String ruleName;
    String duration;
    String salience;
    String condition;
    String consequence;


    public RuleTemplate(JERule rule) {
        super();
        this.salience = String.valueOf(rule.getSalience());
       // this.condition = rule.getCondition().getString();
        consequence = "";
        for (Consequence cons : rule.getConsequences()) {
            consequence = consequence + "\n" + cons.getExpression();
        }
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSalience() {
        return salience;
    }

    public void setSalience(String salience) {
        this.salience = salience;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getConsequence() {
        return consequence;
    }

    public void setConsequence(String consequence) {
        this.consequence = consequence;
    }


}
