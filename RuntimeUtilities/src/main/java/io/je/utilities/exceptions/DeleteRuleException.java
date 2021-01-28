package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class DeleteRuleException extends JEException {

	
	public DeleteRuleException( String message) {
		super(ResponseCodes.RULE_NOT_DELETED,message);
	}

}
