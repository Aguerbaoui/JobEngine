package blocks.basic;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.string.JEStringUtils;

public class ScriptBlock extends WorkflowBlock {

    String script;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        if (JEStringUtils.isEmpty(script)) return;
        if(script.equalsIgnoreCase(APIConstants.DEFAULT)) this.script = null;
        else
            this.script = script;
    }
}
