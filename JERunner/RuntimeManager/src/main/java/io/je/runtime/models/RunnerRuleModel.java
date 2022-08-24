package io.je.runtime.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.ruleengine.enumerations.RuleFormat;
import io.je.utilities.mapping.JERunnerRuleMapping;

import java.util.Set;


/*
 * Rule Model
 */
public class RunnerRuleModel {

    //The project this rule belongs to
    @JsonProperty(JERunnerRuleMapping.PROJECT_ID)
    String projectId;


    @JsonProperty(JERunnerRuleMapping.PROJECT_NAME)
    String projectName;

    //rule identifier
    @JsonProperty(JERunnerRuleMapping.RULE_NAME)
    String ruleName;

    //rule identifier
    @JsonProperty(JERunnerRuleMapping.RULE_ID)
    String ruleId;

    //path where the rule file was created
    @JsonProperty(JERunnerRuleMapping.RULE_PATH)
    String rulePath;

    //Rule format
    @JsonProperty(JERunnerRuleMapping.RULE_FORMAT)
    RuleFormat format;

    @JsonProperty(JERunnerRuleMapping.RULE_TOPICS)
    Set<String> topics;

    /*
     * getters and setters
     */

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRulePath() {
        return rulePath;
    }

    public void setRulePath(String rulePath) {
        this.rulePath = rulePath;
    }

    public RuleFormat getFormat() {
        return format;
    }

    public void setFormat(RuleFormat format) {
        this.format = format;
    }


    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


}
