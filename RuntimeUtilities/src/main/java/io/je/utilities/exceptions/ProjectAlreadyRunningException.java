package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ProjectAlreadyRunningException extends JEException {

    /**
     *
     */
    private static final long serialVersionUID = 583912528765665701L;

    public ProjectAlreadyRunningException(String message) {
        super(ResponseCodes.PROJECT_ALREADY_RUNNING, message);

    }

}
