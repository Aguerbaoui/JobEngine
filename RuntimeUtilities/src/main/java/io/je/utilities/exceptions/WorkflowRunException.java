package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class WorkflowRunException extends JEException {
    public WorkflowRunException(String message) {
        super(ResponseCodes.WORKFLOW_RUN_ERROR, message);
    }
}
