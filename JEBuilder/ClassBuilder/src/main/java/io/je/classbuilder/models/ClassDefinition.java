package io.je.classbuilder.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.utilities.beans.ClassAuthor;


public class ClassDefinition {
	
    @JsonProperty(ClassModelAttributeMapping.CLASSID)
	String classId;
    
    @JsonProperty(ClassModelAttributeMapping.WORKSPACEID)
	String workspaceId;
    
    @JsonProperty(ClassModelAttributeMapping.CLASSNAME)
	String name;
    
    @JsonProperty(ClassModelAttributeMapping.ATTRIBUTES)
	List<FieldModel> attributes;
    
    @JsonProperty(ClassModelAttributeMapping.BASETYPES)
	List<String> baseTypes;
    
    @JsonProperty(ClassModelAttributeMapping.METHODS)
	List<MethodModel> methods;
    
    @JsonProperty(ClassModelAttributeMapping.ISCLASS)
	boolean isClass;
    
    @JsonProperty(ClassModelAttributeMapping.ISINTERFACE)
	boolean isInterface;
    
    @JsonProperty(ClassModelAttributeMapping.ISTRUCT)
	boolean isStruct;
    
    @JsonProperty(ClassModelAttributeMapping.ISENUM)
	boolean isEnum;
    
    @JsonProperty(ClassModelAttributeMapping.VISIBILITY)
	String classVisibility;
    
    @JsonProperty(ClassModelAttributeMapping.DEPENDENTENTITIES)
	List<String> dependentEntities;
    
    @JsonProperty(ClassModelAttributeMapping.INHERITANCESEMANTICS)
	String inheritanceSemantics;
    
    @JsonProperty(ClassModelAttributeMapping.IMPORTS)
	List<String> imports;
	
	ClassAuthor classAuthor;
	
	public String getInheritanceSemantics() {
		return inheritanceSemantics;
	}
	public void setInheritanceSemantics(String inheritanceSemantics) {
		this.inheritanceSemantics = inheritanceSemantics;
	}
	
	
	
	
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
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

	public ClassAuthor getClassAuthor(){
		return classAuthor;
	}
	
	public void setClassAuthor(ClassAuthor classAuthor){
		this.classAuthor = classAuthor;
	}

	@Override
	public String toString() {
		return "ClassModel [idClass=" + classId + ", workspaceId=" + workspaceId + ", name=" + name + ", attributes="
				+ attributes + ", baseTypes=" + baseTypes + ", methods=" + methods + ", isClass=" + isClass
				+ ", isInterface=" + isInterface + ", isStruct=" + isStruct + ", isEnum=" + isEnum
				+ ", classVisibility=" + classVisibility + ", dependentEntities=" + dependentEntities
				+ ", inheritanceSemantics=" + inheritanceSemantics + ", imports=" + imports + "]";
	}
	

	
}
