package io.je.classbuilder.models;

import java.util.List;

public class ClassModel {
	
	String _id;
	String workspaceId;
	String name;
	List<FieldModel> attributes;
	List<String> baseTypes;
	List<MethodModel> methods;
	boolean isClass;
	boolean isInterface;
	boolean isStruct;
	boolean isEnum;
	String classVisibility;
	List<String> dependentEntities;
	String inheritanceSemantics;
	List<String> imports;
	
	
	
	public String getInheritanceSemantics() {
		return inheritanceSemantics;
	}
	public void setInheritanceSemantics(String inheritanceSemantics) {
		this.inheritanceSemantics = inheritanceSemantics;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	
	
	
	public String getWorkspaceId() {
		return workspaceId;
	}
	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<FieldModel> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<FieldModel> attributes) {
		this.attributes = attributes;
	}
	public List<String> getBaseTypes() {
		return baseTypes;
	}
	public void setBaseTypes(List<String> baseTypes) {
		this.baseTypes = baseTypes;
	}
	public boolean  getIsClass() {
		return isClass;
	}
	public void setClass(boolean isClass) {
		this.isClass = isClass;
	}
	public boolean getIsInterface() {
		return isInterface;
	}
	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}
	public boolean getIsStruct() {
		return isStruct;
	}
	public void setStruct(boolean isStruct) {
		this.isStruct = isStruct;
	}
	public boolean  getIsEnum() {
		return isEnum;
	}
	public void setIsEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}
	public String getClassVisibility() {
		return classVisibility;
	}
	public void setClassVisibility(String classVisibility) {
		this.classVisibility = classVisibility;
	}
	public List<String> getDependentEntities() {
		return dependentEntities;
	}
	public void setDependentEntities(List<String> dependentEntities) {
		this.dependentEntities = dependentEntities;
	}
	public List<String> getImports() {
		return imports;
	}
	public void setImports(List<String> imports) {
		this.imports = imports;
	}
	
	
	public List<MethodModel> getMethods() {
		return methods;
	}
	public void setMethods(List<MethodModel> methods) {
		this.methods = methods;
	}
	@Override
	public String toString() {
		return "ClassModel [_id=" + _id + ", name=" + name + ", attributes=" + attributes + ", baseTypes=" + baseTypes
				+ ", isClass=" + isClass + ", isInterface=" + isInterface + ", isStruct=" + isStruct + ", isEnum="
				+ isEnum + ", classVisibility=" + classVisibility + ", dependentEntities=" + dependentEntities
				+ ", imports=" + imports + "]";
	}

	
}
