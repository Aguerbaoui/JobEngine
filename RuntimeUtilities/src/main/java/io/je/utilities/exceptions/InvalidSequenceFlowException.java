package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class InvalidSequenceFlowException extends JEException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidSequenceFlowException(String message) {
        super(ResponseCodes.INVALID_SEQUENCE_FLOW, message);

    }

}
