package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class AddClassException extends JEException {

    public AddClassException(String message) {
        super(ResponseCodes.ADD_CLASS_FAILED, message);

    }

}
