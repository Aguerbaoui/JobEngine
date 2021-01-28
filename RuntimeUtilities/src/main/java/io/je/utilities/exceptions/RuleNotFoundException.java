package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class RuleNotFoundException extends JEException {

	public RuleNotFoundException( String message) {
		super(ResponseCodes.RULE_NOT_FOUND,message);

	}

}
