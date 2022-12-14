package io.je.rulebuilder.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.config.AttributesMapping;


/**
 * Rule Model Class
 */
public class RuleModel {


    @JsonProperty(AttributesMapping.RULEID)
    String ruleId;

    @JsonProperty(AttributesMapping.RULENAME)
    String ruleName;

    @JsonProperty(AttributesMapping.DESC)
    String description;


    @JsonProperty(AttributesMapping.SALIENCE)
    String salience;

    @JsonProperty(AttributesMapping.ENABLED)
    boolean enabled;


    @JsonProperty(AttributesMapping.DATEEFFECTIVE)
    String dateEffective;

    @JsonProperty(AttributesMapping.DATEEXPIRES)
    String dateExpires;

    @JsonProperty(AttributesMapping.TIMER)
    String timer;

    @JsonProperty(AttributesMapping.CREATEDAT)
    String createdAt;

    @JsonProperty(AttributesMapping.LASTUPDATE)
    String lastModifiedAt;

    @JsonProperty(AttributesMapping.STATUS)
    String status;


    String createdBy;
    String modifiedBy;


    //temporary 
    @JsonProperty(AttributesMapping.FRONTCONFIG)
    String ruleFrontConfig;


    public RuleModel(JERule rule) {
        super();
        this.ruleId = rule.getJobEngineElementID();
        this.ruleName = rule.getJobEngineElementName();
        this.description = rule.getDescription();
        this.status = String.valueOf(rule.isBuilt());
        this.createdAt = rule.getJeObjectCreationDate()
                .toString();
        this.lastModifiedAt = rule.getJeObjectLastUpdate()
                .toString();
        this.createdBy = rule.getJeObjectCreatedBy();
        this.modifiedBy = rule.getJeObjectModifiedBy();
        this.status = rule.getStatus()
                .toString();
        if (rule instanceof UserDefinedRule) {
            this.salience = ((UserDefinedRule) rule).getRuleParameters()
                    .getSalience();
            this.enabled = Boolean.valueOf(((UserDefinedRule) rule).getRuleParameters()
                    .getEnabled());
            this.dateEffective = ((UserDefinedRule) rule).getRuleParameters()
                    .getDateEffective();
            this.dateExpires = ((UserDefinedRule) rule).getRuleParameters()
                    .getDateExpires();
            this.timer = ((UserDefinedRule) rule).getRuleParameters()
                    .getTimer();
            this.ruleFrontConfig = ((UserDefinedRule) rule).getRuleFrontConfig();

        }

    }


    public RuleModel() {
        super();
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }


    public String getCreatedBy() {
        return createdBy;
    }


    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    public String getModifiedBy() {
        return modifiedBy;
    }


    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }


    public String getSalience() {
        return salience;
    }

    public void setSalience(String salience) {
        this.salience = salience;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDateEffective() {
        return dateEffective;
    }

    public void setDateEffective(String dateEffective) {
        this.dateEffective = dateEffective;
    }

    public String getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(String dateExpires) {
        this.dateExpires = dateExpires;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public String getLastModifiedAt() {
        return lastModifiedAt;
    }


    public void setLastModifiedAt(String lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getRuleFrontConfig() {
        return ruleFrontConfig;
    }


    public void setRuleFrontConfig(String ruleFrontConfig) {
        this.ruleFrontConfig = ruleFrontConfig;
    }


}
