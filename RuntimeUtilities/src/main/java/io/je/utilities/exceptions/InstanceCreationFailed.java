package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class InstanceCreationFailed extends JEException {

	
	public InstanceCreationFailed( String message) {
		super(ResponseCodes.INSTANCE_NOT_CREATED,message);
	}

}
