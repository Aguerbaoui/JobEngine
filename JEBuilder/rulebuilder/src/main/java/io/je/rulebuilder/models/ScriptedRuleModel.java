package io.je.rulebuilder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.rulebuilder.config.AttributesMapping;

public class ScriptedRuleModel {


    @JsonProperty(AttributesMapping.RULEID)
    String ruleId;

    @JsonProperty(AttributesMapping.RULENAME)
    String ruleName;

    @JsonProperty(AttributesMapping.SCRIPT)
    String script;

}
