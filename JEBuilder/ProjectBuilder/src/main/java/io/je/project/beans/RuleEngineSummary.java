package io.je.project.beans;

import java.util.ArrayList;
import java.util.List;

public class RuleEngineSummary{

	boolean isRunning = false;
	List<String> builtRules ; // id of rules that have been added to the rule engine and that will run if project is running
	
	
	
	public RuleEngineSummary() {
		super();
		builtRules = new ArrayList<>();
	}
	public boolean isRunning() {
		return isRunning;
	}
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	public List<String> getBuiltRules() {
		return builtRules;
	}
	public void setBuiltRules(List<String> builtRules) {
		this.builtRules = builtRules;
	}
	public void remove(String ruleId) {
		if(builtRules.contains(ruleId))
		{
			builtRules.remove(ruleId);
		}else
		{
			System.out.println("!!!!!!!!!!!!! logic error");
		}
		
	}
	
	public void add(String ruleId)
	{
		if(!builtRules.contains(ruleId))
		{
			builtRules.add(ruleId);
		}
	}
	
	
	
}
