package io.je.ruleengine.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.je.utilities.runtimeobject.JEObject;

public class RuleMatch extends JEObject{
	
	
	
	public RuleMatch(String jobEngineElementID, String jobEngineProjectID) {
		super(jobEngineElementID, jobEngineProjectID);
		// TODO Auto-generated constructor stub
	}
	private List<JEObject> instancesMatched = new ArrayList<>();
	private Map<String,Object> declaredVariables = new HashMap();
	public List<JEObject> getInstancesMatched() {
		return instancesMatched;
	}
	public void setInstancesMatched(List<JEObject> instancesMatched) {
		this.instancesMatched = instancesMatched;
	}
	public Map<String, Object> getDeclaredVariables() {
		return declaredVariables;
	}
	public void setDeclaredVariables(Map<String, Object> declaredVariables) {
		this.declaredVariables = declaredVariables;
	}
	
	
	
	
	
	

}
