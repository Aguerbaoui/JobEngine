package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class FileNotFoundException extends JEException {

    private static final long serialVersionUID = 3893986508618978394L;

    public FileNotFoundException( String message) {
		super(ResponseCodes.FILE_NOT_FOUND,message);

    }

}
