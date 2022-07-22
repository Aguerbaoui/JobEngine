package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class JEFileNotFoundException extends JEException {

    private static final long serialVersionUID = 3893986508618978394L;

    public JEFileNotFoundException(String message) {
        super(ResponseCodes.FILE_NOT_FOUND, message);

    }

}
