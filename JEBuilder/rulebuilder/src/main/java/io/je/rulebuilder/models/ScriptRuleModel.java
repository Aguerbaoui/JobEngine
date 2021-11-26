package io.je.rulebuilder.models;

import com.fasterxml.jackson.annotation.JsonProperty;

//import io.je.utilities.runtimeobject.ClassDefinition;

import io.je.rulebuilder.config.AttributesMapping;

public class ScriptRuleModel {

	@JsonProperty(AttributesMapping.RULEID)
	String ruleId;

	@JsonProperty(AttributesMapping.RULENAME)
	String ruleName;

    @JsonProperty(AttributesMapping.SCRIPT)
	String script;
    
    @JsonProperty(AttributesMapping.DESC)
   	String description;
    
   // @JsonProperty(AttributesMapping.CLASSES)
	//List<ClassDefinition> classes;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*public List<ClassDefinition> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassDefinition> classes) {
		this.classes = classes;
	}
    */
    
    

}
