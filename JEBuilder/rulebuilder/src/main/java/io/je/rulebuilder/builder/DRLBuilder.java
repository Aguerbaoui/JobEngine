package io.je.rulebuilder.builder;

import io.je.rulebuilder.components.RuleTemplate;
import io.je.rulebuilder.config.RuleBuilderConfig;
import org.drools.template.ObjectDataCompiler;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/*
 * class responsible for drl generation
 */
public class DRLBuilder {

    private DRLBuilder() {

    }

    public static void generateDRL(RuleTemplate rule) {

        /*
         * TODO: not sure if objectDataCompiler is thread safe so creating a new instance everytime
         */
        ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();
        Map<String, String> data = new HashMap<>();
        data.put("name", rule.getRuleName());
        data.put("salience", rule.getSalience());
        data.put("duration", rule.getDuration());
        data.put("condition", rule.getCondition());
        data.put("actions", rule.getConsequence());
        String ruleContent = "k";
        try {
            ruleContent = objectDataCompiler.compile(Arrays.asList(data), new FileInputStream(RuleBuilderConfig.ruleTemplatePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            File myObj = new File(RuleBuilderConfig.drlGenerationPath + rule.getRuleName() + ".drl");
            if (!myObj.createNewFile()) {
                //error
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter(RuleBuilderConfig.drlGenerationPath + rule.getRuleName() + ".drl");
            myWriter.write(ruleContent);
            myWriter.close();
            System.out.println("Successfully generated drl");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } finally {
            if (myWriter != null) {
                try {
                    myWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
