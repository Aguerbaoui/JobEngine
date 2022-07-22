package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class UnknownBlockEException extends JEException {

    public UnknownBlockEException(String message) {
        super(ResponseCodes.UNKNOWN_BLOCK, message);

    }

}
