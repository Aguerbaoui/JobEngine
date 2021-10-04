package io.je.utilities.exceptions;

import static io.je.utilities.constants.ResponseCodes.VARIABLE_ERROR;

public class VariableException extends JEException{
    public VariableException( String message) {
        super(VARIABLE_ERROR, message);
    }
}
