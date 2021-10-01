package blocks.basic;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import utils.string.StringUtilities;

public class ScriptBlock extends WorkflowBlock {

    String script;

    int timeout;

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
}
