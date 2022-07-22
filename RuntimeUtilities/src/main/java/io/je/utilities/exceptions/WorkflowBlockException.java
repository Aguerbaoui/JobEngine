package io.je.utilities.exceptions;

import static io.je.utilities.constants.ResponseCodes.WORKFLOW_BLOCK_ERROR;

public class WorkflowBlockException extends JEException {
    public WorkflowBlockException(String message) {
        super(WORKFLOW_BLOCK_ERROR, message);
    }
}
