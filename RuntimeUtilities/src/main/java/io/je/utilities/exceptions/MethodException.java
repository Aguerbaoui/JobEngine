package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class MethodException extends JEException{
    public MethodException( String message) {
        super(ResponseCodes.METHOD_EXCEPTION, message);
    }
}
