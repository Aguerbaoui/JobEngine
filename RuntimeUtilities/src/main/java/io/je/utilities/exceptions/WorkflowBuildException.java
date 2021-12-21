package io.je.utilities.exceptions;

import static io.je.utilities.constants.ResponseCodes.WORKFLOW_BUILD_ERROR;

public class WorkflowBuildException extends JEException{
    public WorkflowBuildException( String message) {
        super(WORKFLOW_BUILD_ERROR, message);
    }
}
