package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class InstanceCreationFailedException extends JEException {


    public InstanceCreationFailedException(String message) {
        super(ResponseCodes.INSTANCE_NOT_CREATED, message);
    }

}
