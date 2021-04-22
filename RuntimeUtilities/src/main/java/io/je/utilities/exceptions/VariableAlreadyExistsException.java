package io.je.utilities.exceptions;

import static io.je.utilities.constants.ResponseCodes.VARIABLE_EXISTS;

public class VariableAlreadyExistsException  extends JEException{
    public VariableAlreadyExistsException(String message) {
        super(VARIABLE_EXISTS, message);
    }
}
