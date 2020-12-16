package io.je.utilities.exceptions;

public class ProjectAlreadyRunningException extends JEException {

    /**
     *
     */
    private static final long serialVersionUID = 583912528765665701L;

    public ProjectAlreadyRunningException(String code, String message) {
        super(code, message);
    }

}
