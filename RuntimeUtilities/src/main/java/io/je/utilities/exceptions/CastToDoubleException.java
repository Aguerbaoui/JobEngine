package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class CastToDoubleException extends JEException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CastToDoubleException(String message) {
        super(ResponseCodes.CAST_DOUBLE_EXCEPTION, message);

    }

}
