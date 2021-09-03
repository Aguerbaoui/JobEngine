package io.je.rulebuilder.components;

import io.je.utilities.files.JEFileUtils;

public class ScriptedRule extends  JERule {

	String script ;

	

	public ScriptedRule(String jobEngineProjectID , String jobEngineElementID, String script, String ruleName) {
		super(jobEngineElementID, jobEngineProjectID,ruleName);
		this.script = script;
	}

	



	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}



	public String generateDRL(String buildPath) {
		
		String fileName="";
		try{
			 fileName = buildPath +"\\" + jobEngineElementID +".drl";
	        JEFileUtils.copyStringToFile(script, fileName, "UTF-8");

		}catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
		
		
	}











	
	
}
