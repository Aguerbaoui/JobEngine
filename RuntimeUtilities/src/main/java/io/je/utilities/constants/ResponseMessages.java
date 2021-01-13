package io.je.utilities.constants;

public class ResponseMessages {

	/*
	 * private constructor to hide the public one
	 */
	private ResponseMessages() {
	}
	
	
	//
	public static final String ConfigUpdated = "Configuration Updated successfully.";

	public static final String EXECUTING_PROJECT = "Executing project";

	public static final String STOPPING_PROJECT = "Stopped project";
	/*
	 * Rules response messages
	 */

	// rule addition

	public static final String RULE_BUILD_ERROR = "Error while building a rule";

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

	//****************************************WORKFLOW**************************************************
	public static final String CREATED_PROJECT_SUCCESSFULLY = "Created project successfully";

	public static final String BUILT_EVERYTHING_SUCCESSFULLY = "Built everything successfully";

	public static final String BUILT_EVERYTHING_SUCCESSFULLY1 = "Built everything successfully";

	public static final String ADDED_WORKFLOW_SUCCESSFULLY = "Added workflow successfully";

	public static final String WORKFLOW_BUILT_SUCCESSFULLY = "Workflow built successfully";

	public static final String EXECUTING_WORKFLOW = "Executing workflow";

	public static final String WORKFLOW_DEPLOYED = "Workflow deployed to engine";

}
