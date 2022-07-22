package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ProjectStopException extends JEException {
    public ProjectStopException(String message) {
        super(ResponseCodes.PROJECT_STOP_FAILED, message);
    }
}
