package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ProjectRunException extends JEException {

    public ProjectRunException(String message) {
        super(ResponseCodes.PROJECT_RUN_FAILED, message);


    }

}
