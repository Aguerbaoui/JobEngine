package io.je.rulebuilder.components;


import utils.files.FileUtilities;

public class ScriptedRule extends JERule {

    String script;


    public ScriptedRule(String jobEngineProjectID, String jobEngineElementID, String script, String ruleName, String projectName) {
        super(jobEngineElementID, jobEngineProjectID, ruleName, projectName);
        this.script = script;
    }


    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }


    public String generateDRL(String buildPath) {

        String fileName = "";
        try {
            fileName = buildPath + "\\" + jobEngineElementID + ".drl";
            FileUtilities.copyStringToFile(script, fileName, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;


    }


}
