package io.je.rulebuilder.components;

import io.je.utilities.files.JEFileUtils;
import io.je.utilities.logger.JELogger;

public class ScriptedRule extends  JERule {

	String script ;

	
	

	public ScriptedRule(String jobEngineElementID, String jobEngineProjectID, String script) {
		super(jobEngineElementID, jobEngineProjectID);
		this.script = script;
	}

	



	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}



	public String generateDRL(String buildPath) {
		String fileName = buildPath +"\\" + jobEngineElementID +".drl";
        JELogger.info(getClass(), script);
        JEFileUtils.copyStringToFile(script, fileName, "UTF-8");
		return fileName;
		
		
	}


	
	
}
