package io.je.utilities.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.je.utilities.ruleutils.OperationStatusDetails;

public class JECustomResponse extends JEResponse {

	List<OperationStatusDetails> details ;
	
	
	public JECustomResponse(int code, String message,List<OperationStatusDetails>details) {
		super(code, message);
		this.details = details;
	}


	public List<OperationStatusDetails> getDetails() {
		return details;
	}


	public void setDetails(List<OperationStatusDetails> details) {
		this.details = details;
	}


	
	

}