package io.je.utilities.network;

public class Response {

    private String message;

    private int code;

    public Response(int code, String message) {
        this.message = message;
        this.code = code;
    }
}
