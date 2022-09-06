package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class CommandFailedException extends JEException {

    public CommandFailedException(String message) {
        super(ResponseCodes.COMMAND_EXECUTION_FAILED, message);
    }
}
