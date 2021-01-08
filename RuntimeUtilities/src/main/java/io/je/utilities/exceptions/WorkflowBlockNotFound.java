package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class WorkflowBlockNotFound extends JEException {

    public WorkflowBlockNotFound( String message) {
		super(ResponseCodes.WORKFLOW_BLOCK_NOT_FOUND,message);

    }

}
