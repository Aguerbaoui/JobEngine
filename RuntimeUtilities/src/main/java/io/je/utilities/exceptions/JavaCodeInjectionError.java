package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class JavaCodeInjectionError extends JEException{

    public JavaCodeInjectionError(String message) {
        super(ResponseCodes.CODE_INJECTION_ERROR,message);
    }
}
