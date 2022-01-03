package blocks.basic;

import blocks.WorkflowBlock;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import utils.string.StringUtilities;

public class ScriptBlock extends WorkflowBlock {

    String script;

    int timeout;

    String scriptPath;

    public String getScript() {
        return script;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setScript(String script) {
        if (StringUtilities.isEmpty(script)) return;
        if(script.equalsIgnoreCase(APIConstants.DEFAULT)) this.script = null;
        else
            this.script = script;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = ConfigurationConstants.JAVA_GENERATION_PATH + "\\" + ClassBuilderConfig.CLASS_PACKAGE + "\\" + scriptPath + ".java";
    }
}
