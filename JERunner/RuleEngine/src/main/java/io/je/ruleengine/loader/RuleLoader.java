package io.je.ruleengine.loader;

import io.je.ruleengine.models.Rule;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.JEFileNotFoundException;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * load rule content from file path
 */
public class RuleLoader {


    public static void loadRuleContent(Rule rule) throws JEFileNotFoundException {
        try {
            JELogger.debug("Rule path = " + rule.getPath(), LogCategory.RUNTIME,
                    rule.getJobEngineProjectID(), LogSubModule.RULE, rule.getJobEngineElementID());
            rule.setContent(new String(Files.readAllBytes(Paths.get(rule.getPath()))));
        } catch (IOException e) {
            e.printStackTrace();
            throw new JEFileNotFoundException(JEMessages.RULE_FILE_NOT_FOUND + rule.getPath());
        }
    }

    public static boolean writeRule(String rule, String filename) throws IOException {
        try (FileWriter fileWriter = new FileWriter(new File(filename));
             BufferedWriter writer = new BufferedWriter(fileWriter)) {
            writer.write(rule);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
