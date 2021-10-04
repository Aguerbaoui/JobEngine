package io.je.utilities.exceptions;

import static io.je.utilities.constants.JEMessages.DELETE_WORKFLOW_FAILED;
import static io.je.utilities.constants.ResponseCodes.WORKFLOW_DELETION_ERROR;

public class WorkflowException extends JEException{
    public WorkflowException(String message) {
        super(WORKFLOW_DELETION_ERROR, message);
    }
}
