package io.je.classbuilder.models;

import java.util.List;

public class MethodModel {
	String methodName;
	List<FieldModel> inputs;
	String returnType;
	String methodScope;
	String methodVisibility;
	String code;
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public List<FieldModel> getInputs() {
		return inputs;
	}
	public void setInputs(List<FieldModel> inputs) {
		this.inputs = inputs;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getMethodScope() {
		return methodScope;
	}
	public void setMethodScope(String methodScope) {
		this.methodScope = methodScope;
	}
	public String getMethodVisibility() {
		return methodVisibility;
	}
	public void setMethodVisibility(String methodVisibility) {
		this.methodVisibility = methodVisibility;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	
}
