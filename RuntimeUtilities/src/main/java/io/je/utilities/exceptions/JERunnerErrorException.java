package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class JERunnerErrorException extends JEException {

    public JERunnerErrorException(String message) {
        super(ResponseCodes.JERUNNER_ERROR, message);

    }

}
