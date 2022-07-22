package io.je.utilities.exceptions;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;

public class WorkflowEndBlockNotDefinedException extends JEException {

    public WorkflowEndBlockNotDefinedException() {

        super(ResponseCodes.WORKFLOW_END_BLOCK_NOT_DEFINED, JEMessages.WORKFLOW_END_BLOCK_NOT_DEFINED);

    }

}
