package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ProjectNotFoundException extends JEException {

    public ProjectNotFoundException(String message) {
        super(ResponseCodes.PROJECT_NOT_FOUND, message);


    }

}
