package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class AddWorkflowBlockException extends JEException {

    public AddWorkflowBlockException(String message) {
        super(ResponseCodes.ADD_WORKFLOW_BLOCK, message);

    }


}
