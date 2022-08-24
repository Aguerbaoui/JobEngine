package io.je.utilities.beans;

public class JEResponse {

    protected String message;
    protected int code;


    protected JEResponse() {
    }

    public JEResponse(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
