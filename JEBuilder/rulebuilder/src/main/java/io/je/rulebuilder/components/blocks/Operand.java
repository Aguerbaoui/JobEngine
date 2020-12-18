package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.enumerations.OperandDataType;
import io.je.rulebuilder.models.OperandModel;
import io.je.utilities.runtimeobject.JEObject;

public class Operand extends JEObject {
	
	String value;
    OperandDataType operandDataType;
    String className;
     
    
    public Operand (OperandModel operandModel)
    {
    	super(operandModel.getProjectId(), operandModel.getOperandId());
		this.value = operandModel.getValue();
		this.operandDataType = operandModel.getDataType();
		this.className = operandModel.getClassName();
    }
    

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public OperandDataType getOperandDataType() {
		return operandDataType;
	}
	public void setOperandDataType(OperandDataType operandDataType) {
		this.operandDataType = operandDataType;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
    
    
    
    
    
    

	
	

}
