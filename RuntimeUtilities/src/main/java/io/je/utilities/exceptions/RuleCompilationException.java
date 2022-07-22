package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class RuleCompilationException extends JEException {


    private static final long serialVersionUID = 583912528765665701L;
    String compilationError;

    public RuleCompilationException(String message) {
        super(ResponseCodes.RULE_COMPILATION_ERROR, message);

    }

    public RuleCompilationException(String message, String compilationError) {
        super(ResponseCodes.RULE_COMPILATION_ERROR, message);
        this.compilationError = compilationError;

    }

    public String getCompilationError() {
        return compilationError;
    }

    public void setCompilationError(String compilationError) {
        this.compilationError = compilationError;
    }


}
