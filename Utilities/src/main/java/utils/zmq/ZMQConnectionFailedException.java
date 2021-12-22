package utils.zmq;

public class ZMQConnectionFailedException extends Exception {
	 private static final long serialVersionUID = 1L;
	    private int code;

	    public ZMQConnectionFailedException(int code, String message) {
	        super(message);
	        this.setCode(code);
	    }

	    public ZMQConnectionFailedException(int code, String message, Throwable cause) {
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
