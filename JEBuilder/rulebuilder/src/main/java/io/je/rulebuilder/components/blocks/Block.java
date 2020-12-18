package io.je.rulebuilder.components.blocks;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.utilities.runtimeobject.JEObject;

/*
 * Job Engine block
 */
public abstract class Block extends JEObject {

    @JsonProperty(AttributesMapping.RULEID)
    String ruleId;
    
    int operationId;


    public Block(String jobEngineElementID, String jobEngineProjectID, String ruleId) {
        super(jobEngineElementID, jobEngineProjectID);
        this.ruleId = ruleId;
    }


    public String getRuleId() {
        return ruleId;
    }


    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }


    /*
     * returns a string that expresses this condition in the drools rule language.
     */
    public abstract String getExpression();

}
