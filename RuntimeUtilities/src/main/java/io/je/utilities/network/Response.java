package io.je.utilities.network;

public class Response {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;

    public Response(String code, String message) {
        this.message = message;
        this.code = code;
    }
}
