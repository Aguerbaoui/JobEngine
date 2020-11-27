package io.je.utilities.response;

import java.util.ArrayList;
import java.util.List;

public class Response {
	private String Message;
	private ResponseStatus state;
	private List<String> errors;
	public Response()
	{
		errors= new ArrayList<String>();
		
	}
	
	

	public String getMessage() {
		return Message;
	}



	public void setMessage(String message) {
		Message = message;
	}



	public ResponseStatus getState() {
		return state;
	}

	public void setState(ResponseStatus state) {
		this.state = state;
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public boolean addErrorMsg(String msg)
	{
		try {
			errors.add(msg);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	

}

