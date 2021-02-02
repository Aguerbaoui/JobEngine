package blocks.basic;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.string.JEStringUtils;

public class StartBlock extends WorkflowBlock {

    private String reference = null;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        if (JEStringUtils.isEmpty(reference)) return;
        if(reference.equalsIgnoreCase(APIConstants.DEFAULT))
            this.reference = null;
        else
            this.reference = reference;
    }
}
