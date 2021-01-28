package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class RuleFormatNotValidException extends JEException {

    public RuleFormatNotValidException( String message) {
		super(ResponseCodes.RULE_NOT_VALID,message);

    }

}
