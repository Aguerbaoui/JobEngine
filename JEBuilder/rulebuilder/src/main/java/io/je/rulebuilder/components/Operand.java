package io.je.rulebuilder.components;

import io.je.rulebuilder.components.enumerations.DataType;
import io.je.utilities.runtimeobject.JEObject;

public class Operand extends JEObject{
	
	
	
	DataType DataType;
	
	String ClassName;
	
	String value;

	public DataType getDataType() {
		return DataType;
	}

	public void setDataType(DataType dataType) {
		DataType = dataType;
	}

	public String getClassName() {
		return ClassName;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}



}
