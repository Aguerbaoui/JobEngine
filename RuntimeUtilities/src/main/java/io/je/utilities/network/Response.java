package io.je.utilities.network;

public class Response {

    private String message;

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

    private int code;

    public Response(int code, String message) {
        this.message = message;
        this.code = code;
    }
}
