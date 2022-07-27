package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ProjectExistsException extends JEException {
    public ProjectExistsException(String message) {
        super(ResponseCodes.PROJECT_EXISTS, message);

    }
}
