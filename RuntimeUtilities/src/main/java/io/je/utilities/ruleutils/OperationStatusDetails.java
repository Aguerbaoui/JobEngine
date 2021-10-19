package io.je.utilities.ruleutils;

public class OperationStatusDetails {
		
	String itemId;
	String itemName;
	boolean operationSucceeded;
	String operationError;
	
	
	
	
	public OperationStatusDetails(String itemId) {
		super();
		this.itemId = itemId;
		operationSucceeded = true;
	}




	public String getItemId() {
		return itemId;
	}




	public void setItemId(String itemId) {
		this.itemId = itemId;
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


	public static OperationStatusDetails getResultDetails(String id, boolean success, String msg, String itemName) {
		OperationStatusDetails res = new OperationStatusDetails(id);
		res.setOperationError(msg);
		res.setOperationSucceeded(success);
		res.setItemName(itemName);
		return res;
	}
	
	

}
