package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class RuleCompilationException extends JEException {

    /**
     *
     */
    private static final long serialVersionUID = 583912528765665701L;

    public RuleCompilationException( String message) {
		super(ResponseCodes.RULE_COMPILATION_ERROR,message);

    }

}
