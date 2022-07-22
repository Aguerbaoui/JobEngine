package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class WorkflowNotFoundException extends JEException {

    public WorkflowNotFoundException(String message) {
        super(ResponseCodes.WORKFLOW_NOT_FOUND, message);

    }

}
