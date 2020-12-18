package io.je.rulebuilder.components.blocks;

import java.util.HashMap;
import java.util.Map;


/*
 * Inventory for all Operands
 */
public class OperandInventory {
	
	
	//list of all operands
	static Map<String,Operand> operands = new HashMap<>();
	
	/*
	 * add operand
	 */
	public static void addOperand(Operand op)
	{
		operands.put(op.getJobEngineElementID(), op);
	}
	
	
	public static Operand getOperand(String operandId)
	{
		return operands.get(operandId);
	}
	

}
