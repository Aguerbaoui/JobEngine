package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.string.JEStringUtils;

public class SignalCatchEvent extends WorkflowBlock {

    private String signalRef;

    public String getSignalRef() {
        return signalRef;
    }

    public void setSignalRef(String signalRef) {
        if (JEStringUtils.isEmpty(signalRef)) return;
        if(signalRef.equalsIgnoreCase(APIConstants.DEFAULT)) this.signalRef = null;
        else
            this.signalRef = signalRef;
    }
}
