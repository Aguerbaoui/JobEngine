package io.je.utilities.exceptions;

public class JEException extends Exception {

    private static final long serialVersionUID = 1L;
    private int code;

    public JEException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    public JEException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

