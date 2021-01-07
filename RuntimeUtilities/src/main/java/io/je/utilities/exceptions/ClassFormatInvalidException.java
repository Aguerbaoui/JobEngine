package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ClassFormatInvalidException extends JEException {

	public ClassFormatInvalidException( String message) {
		super(ResponseCodes.ADD_RULE_BLOCK,message);

	}

}
