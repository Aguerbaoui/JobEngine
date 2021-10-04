package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ProjectLoadException extends JEException{

    public ProjectLoadException(String message) {
        super(ResponseCodes.PROJECT_LOAD_ERROR,message);
    }
}
