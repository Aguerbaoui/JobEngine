package io.je.utilities.exceptions;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;

public class WorkflowStartBlockNotDefinedException extends JEException {

    public WorkflowStartBlockNotDefinedException() {

        super(ResponseCodes.WORKFLOW_START_BLOCK_NOT_DEFINED, JEMessages.WORKFLOW_START_BLOCK_NOT_DEFINED);

    }

}
