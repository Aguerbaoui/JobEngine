package io.je.ruleengine.loader;

import io.je.ruleengine.models.Rule;
import io.je.utilities.logger.JELogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
 * load rule content from file path
 */
public class RuleLoader {


    public static boolean loadRuleContent(Rule rule) throws FileNotFoundException {
        boolean returnValue = false;
        try {
            JELogger.info(RuleLoader.class, rule.getPath());
            rule.setContent(new String(Files.readAllBytes(Paths.get(rule.getPath()))));
            returnValue = true;
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
        return returnValue;
    }

    public static boolean writeRule(String rule, String filename) throws IOException {
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
