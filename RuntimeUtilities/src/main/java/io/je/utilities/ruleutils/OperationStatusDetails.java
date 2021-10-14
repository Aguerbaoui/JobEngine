package io.je.utilities.ruleutils;

public class OperationStatusDetails {
		
	String itemName;
	boolean operationSucceeded;
	String operationError;
	
	
	
	
	public OperationStatusDetails(String itemName) {
		super();
		this.itemName = itemName;
	}




	public String getItemName() {
		return itemName;
	}




	public void setItemName(String itemName) {
		this.itemName = itemName;
	}




	public boolean isOperationSucceeded() {
		return operationSucceeded;
	}




	public void setOperationSucceeded(boolean operationSucceeded) {
		this.operationSucceeded = operationSucceeded;
	}




	public String getOperationError() {
		return operationError;
	}




	public void setOperationError(String operationError) {
		this.operationError = operationError;
	}
	

	
	
	

}
