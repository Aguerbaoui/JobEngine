package io.je.rulebuilder.config;


/*
 * mapping for block parameters 
 */
public class BlockAttributesMapping {
	
	private BlockAttributesMapping()
	{
		
	}
	
	
	//common attributes	
	public static final String PROJECTID = "project_id";
	public static final String RULEID = "rule_id";
	public static final String BLOCKID = "block_id";
	public static final String BLOCKTYPE = "block_type";
	public static final String TIMEPERSISTENCEON = "time_persistence_on";
	public static final String TIMEPERSISTENCEVALUE = "";
	public static final String TIMEPERSISTENCEUNIT = "";
	
	public static final String OPERATIONID = "operation_id";

	
	
	//block types
	public static final String COMPARISONBLOCK="comparison_block";
	public static final String ARITHMETICBLOCK="arithmetic_block";
	public static final String GATEWAYBLOCK = "gateway_block";
	public static final String EXECUTIONBLOCK = "execution_block";
	
	
	//operands
	public static final String FIRSTOPERAND = "first_operand";
	public static final String SECONDOPERAND = "second_operand";
	public static final String OPERANDTYPE = "type";
	public static final String OPERANDVALUE = "value";
	public static final String OPERANDCLASSNAME = "class_name";
	

	
	/*
	 * operator Ids
	 */
	
	//COMPARISON OPERATORS
	public static final String GREATERTHAN=">";

	
	//GATEWAY OPERATORS
	public static final String AND ="and";

	
	//ARITHMETIC OPERATOR

	
	
	

}
