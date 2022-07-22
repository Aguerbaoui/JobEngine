package io.je.utilities.exceptions;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;

public class WorkflowEndBlockNotUniqueException extends JEException {

    public WorkflowEndBlockNotUniqueException() {

        super(ResponseCodes.WORKFLOW_END_BLOCK_NOT_UNIQUE, JEMessages.WORKFLOW_END_BLOCK_NOT_UNIQUE);

    }

}
