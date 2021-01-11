package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ClassLoadException extends JEException {

    public ClassLoadException( String message) {
		super(ResponseCodes.CLASS_LOAD_EXCEPTION,message);

    }


}
