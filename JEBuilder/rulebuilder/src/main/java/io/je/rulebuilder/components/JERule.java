package io.je.rulebuilder.components;

import io.je.utilities.runtimeobject.JEObject;

import java.util.ArrayList;
import java.util.List;

/*
 * rule definition in job engine
 */
public class JERule extends JEObject {

    String name;
    int salience;
    int duration;
    Condition condition;
    List<Consequence> consequences;


    public JERule(String jobEngineElementID, String jobEngineProjectID) {
        super(jobEngineElementID, jobEngineProjectID);
        consequences = new ArrayList<>();
    }

    //TODO: to be deleted
    public JERule() {
        super();
        consequences = new ArrayList<>();
    }

    public void addConsequence(Consequence cons) {
        consequences.add(cons);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalience() {
        return salience;
    }

    public void setSalience(int salience) {
        this.salience = salience;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<Consequence> getConsequences() {
        return consequences;
    }

    public void setConsequences(List<Consequence> consequences) {
        this.consequences = consequences;
    }


}
