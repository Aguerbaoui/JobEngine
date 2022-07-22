package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class JavaCodeInjectionErrorException extends JEException {

    public JavaCodeInjectionErrorException(String message) {
        super(ResponseCodes.CODE_INJECTION_ERROR, message);
    }
}
