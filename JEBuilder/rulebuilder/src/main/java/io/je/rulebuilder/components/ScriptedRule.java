package io.je.rulebuilder.components;

import java.util.List;

import io.je.utilities.files.JEFileUtils;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.ClassDefinition;

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
		String fileName = buildPath +"\\" + jobEngineElementID +".drl";
       // JELogger.info(getClass(), script);
        JEFileUtils.copyStringToFile(script, fileName, "UTF-8");
		return fileName;
		
		
	}











	
	
}
