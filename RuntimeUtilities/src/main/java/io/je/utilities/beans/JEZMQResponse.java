package io.je.utilities.beans;

public class JEZMQResponse {
	 
	ZMQResponseType response;
	String responseObject;
	String errorMessage;
	
	
	public JEZMQResponse()
	{
		
	}
	
	public JEZMQResponse(ZMQResponseType response, String errorMessage) {
		super();
		this.response = response;
		this.errorMessage = errorMessage;
	}
	public JEZMQResponse(ZMQResponseType response) {
		super();
		this.response = response;
	}
	public ZMQResponseType getResponse() {
		return response;
	}
	public void setResponse(ZMQResponseType response) {
		this.response = response;
	}
	public String getResponseObject() {
		return responseObject;
	}
	public void setResponseObject(String responseObject) {
		this.responseObject = responseObject;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	

	
	
}
