package io.je.utilities.exceptions;

import static io.je.utilities.constants.ResponseCodes.VARIABLE_NOT_FOUND;

public class VariableNotFoundException extends JEException {

    public VariableNotFoundException(String message) {
        super(VARIABLE_NOT_FOUND, message);
    }
}
