package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ProjectStatusException extends JEException {
    public ProjectStatusException(String message) {
        super(ResponseCodes.PROJECT_ALREADY_STOPPED, message);
    }
}
