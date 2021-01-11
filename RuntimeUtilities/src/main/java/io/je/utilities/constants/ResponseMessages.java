package io.je.utilities.constants;

public class ResponseMessages {

	/*
	 * private constructor to hide the public one
	 */
	private ResponseMessages() {
	}
	
	
	//
	public static final String ConfigUpdated = "Configuration Updated successfully.";


	/*
	 * Rules response messages
	 */

	// rule addition

	public static final String RuleAdditionSucceeded = "Rule added successfully.";

	public static final String RuleAdditionFailed = "Failed to add rule.";
	
	// rule update 
	
	public static final String RuleUpdateSucceeded = "Rule updated successfully";
	
	public static final String RuleUpdateFailed ="";
	
	//rule deletion
	
	public static final String RuleDeletionSucceeded = "Rule deleted successfully";

	public static final String WorkflowDeletionSucceeded = "Workflow deleted successfully";
	
	public static final String RuleDeletionFailed ="";
	
	
	//rule build 
	
	public static final String RuleBuiltSuccessfully = "Rule was built successfully";
	
	//class addition
	public static final String classAddedSuccessully ="Class was added successfully";

}
