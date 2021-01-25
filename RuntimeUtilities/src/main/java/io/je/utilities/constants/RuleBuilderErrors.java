package io.je.utilities.constants;

public class RuleBuilderErrors {
	
	//rules
	
	public static final String RuleIdentifierIsEmpty = "Rule identifier can't be empty";
	public static final String ProjectIdentifierIsEmpty = "Project identifier can't be empty";
	public static final String NoExecutionBlock = "Rule must have at least one execution block";
	public static final String RuleNotFound = "No rule with this ID was found";
	public static final String RuleAlreadyExists = "a rule with this Id already exists. ";


	
	//rule blocks
	public static final String AddRuleBlockFailed = "Failed to Add Block";
	public static final String BlockAlreadyExists = "A block with this id already exists.";
	public static final String BlockNotFound = "No Block with this id was found";
	public static final String BlockIdentifierIsEmpty = "A block must have an Id";
	public static final String BlockRuleIdentifierIsEmpty = "A block must have a rule Id";
	public static final String BlockProjectIdentifierIsEmpty = "A block must have a project Id";
	public static final String BlockOperationIdEmpty ="A block operation id can not be empty";
	public static final String BlockOperationIdUnknown ="Block operation id is unknown.";
	public static final String BlockNameIsEmpty = "Block name can't be empty";
	public static final String RuleBuildFailed = "Failed to build rule";
	public static final String FailedToUpdateBlock = "Failed to update block";



}
