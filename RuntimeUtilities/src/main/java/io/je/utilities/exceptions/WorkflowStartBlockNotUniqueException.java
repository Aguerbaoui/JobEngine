package io.je.utilities.exceptions;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;

public class WorkflowStartBlockNotUniqueException extends JEException {

    public WorkflowStartBlockNotUniqueException() {

        super(ResponseCodes.WORKFLOW_START_BLOCK_NOT_UNIQUE, JEMessages.WORKFLOW_START_BLOCK_NOT_UNIQUE);

    }

}
