package io.je.utilities.exceptions;

import static io.je.utilities.constants.ResponseCodes.ERROR_IMPORTING_FILE;

public class LibraryException extends JEException {
    public LibraryException(String message) {
        super(ERROR_IMPORTING_FILE, message);
    }
}
