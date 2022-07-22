package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class DataListenerNotFoundException extends JEException {
    public DataListenerNotFoundException(int code, String message) {
        super(ResponseCodes.ADD_RULE_BLOCK, message);
    }
}
