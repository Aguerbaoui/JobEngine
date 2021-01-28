package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class RuleAlreadyExistsException extends JEException {

    /**
     *
     */
    private static final long serialVersionUID = 583912528765665701L;

    public RuleAlreadyExistsException( String message) {
		super(ResponseCodes.RULE_EXISTS,message);
    }

}
