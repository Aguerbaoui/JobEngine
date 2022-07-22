package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class WorkflowAlreadyRunningException extends JEException {

    public WorkflowAlreadyRunningException(String message) {
        super(ResponseCodes.WORKFLOW_NOT_FOUND, message);

    }

}
