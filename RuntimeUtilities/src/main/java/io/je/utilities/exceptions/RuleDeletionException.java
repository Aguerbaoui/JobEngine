package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class RuleDeletionException extends JEException {

	public RuleDeletionException( String message) {
		super(ResponseCodes.RULE_DELETION_ERROR,message);

	}

}
