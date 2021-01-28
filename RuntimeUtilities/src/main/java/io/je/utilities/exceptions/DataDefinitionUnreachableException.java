package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class DataDefinitionUnreachableException extends JEException {

	public DataDefinitionUnreachableException( String message) {
		super(ResponseCodes.DATA_DEF_UNREACHABLE,message);

	}

}
