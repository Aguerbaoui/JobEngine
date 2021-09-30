package io.je.utilities.beans;

public class JEResponse {

    private String message;
    private int code;
    
    

    private JEResponse() {
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


    public JEResponse(int code, String message) {
        this.message = message;
        this.code = code;
    }
}
