package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class WorkflowBlockNotFoundException extends JEException {

    public WorkflowBlockNotFoundException(String message) {
        super(ResponseCodes.WORKFLOW_BLOCK_NOT_FOUND, message);

    }

}
