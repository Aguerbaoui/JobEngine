package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class EventException extends JEException {


    public EventException(String message) {
        super(ResponseCodes.EVENT_EXCEPTION, message);
    }

}
