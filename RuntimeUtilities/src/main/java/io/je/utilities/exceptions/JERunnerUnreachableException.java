package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class JERunnerUnreachableException extends JEException {

	public JERunnerUnreachableException( String message) {
		super(ResponseCodes.JERUNNER_UNREACHABLE,message);

	}

}
