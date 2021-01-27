package io.je.runtime.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.ruleengine.enumerations.RuleFormat;
import io.je.utilities.mapping.JERunnerRuleMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/*
 * Rule Model
 */
public class RuleModel {

    //The project this rule belongs to
    @JsonProperty(JERunnerRuleMapping.PROJECT_ID)
    String projectId;

    //rule identifier
    @JsonProperty(JERunnerRuleMapping.RULE_NAME)
    String ruleName;
    
  //rule identifier
    @JsonProperty(JERunnerRuleMapping.RULE_ID)
    String ruleId;

    //path where the rule file was created
    @JsonProperty(JERunnerRuleMapping.PATH)
    String rulePath;

    //Rule format
    @JsonProperty(JERunnerRuleMapping.FORMAT)
    RuleFormat format;
    
    @JsonProperty(JERunnerRuleMapping.TOPICS)
    List<String> topics;

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

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

   
	


}
