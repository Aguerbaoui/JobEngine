package io.je.ruleengine.loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.je.ruleengine.models.Rule;
import io.je.utilities.logger.JELogger;

/*
 * load rule content from file path
 */
public class RuleLoader {

public RuleLoader()
{
	
}

public boolean loadRuleContent(Rule rule)
{
	try {
		JELogger.info(rule.getPath());
		rule.setContent(new String(Files.readAllBytes(Paths.get(rule.getPath()))));
		
	} catch (IOException e) {
		return false;
	}
	return true;
}

public boolean writeRule(String rule, String filename) throws IOException {
	try (FileWriter fileWriter = new FileWriter(new File(filename));
			BufferedWriter writer = new BufferedWriter(fileWriter)) {
		writer.write(rule);
		writer.flush();
		writer.close();
		return true;
	} catch (IOException e) {
		throw e;
	}
}

}
