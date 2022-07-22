package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ClassLoadException extends JEException {

    private String compilationErrorMessage = null;

    public ClassLoadException(String message) {
        super(ResponseCodes.CLASS_LOAD_EXCEPTION, message);

    }

    public String getCompilationErrorMessage() {
        return compilationErrorMessage;
    }

    public void setCompilationErrorMessage(String compilationErrorMessage) {
        this.compilationErrorMessage = compilationErrorMessage;
    }

}
