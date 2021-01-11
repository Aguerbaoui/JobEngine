package io.je.ruleengine.loader;

import io.je.ruleengine.models.Rule;
import io.je.utilities.logger.JELogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.je.utilities.constants.RuleEngineErrors;
import io.je.utilities.exceptions.JEFileNotFoundException;

/*
 * load rule content from file path
 */
public class RuleLoader {


    public static void loadRuleContent(Rule rule) throws JEFileNotFoundException {
        try {
            JELogger.info(RuleLoader.class, rule.getPath());
            rule.setContent(new String(Files.readAllBytes(Paths.get(rule.getPath()))));
        } catch (IOException e) {
            throw new JEFileNotFoundException(RuleEngineErrors.RULE_FILE_NOT_FOUND);
        }
    }

    public static boolean writeRsule(String rule, String filename) throws IOException {
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
