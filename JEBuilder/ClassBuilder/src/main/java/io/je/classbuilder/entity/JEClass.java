package io.je.classbuilder.entity;

import io.je.utilities.beans.JEMethod;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Document(collection="JEClass")
public class JEClass  {
	
	@Id
	String classId; // class id defined in data model
	String workspaceId;
	String className;
	HashMap<String, JEMethod>  methods = new HashMap<>();
	/*
	 * path where .java file is saved
	 */
	String classPath;
	
	/*
	 * class type : class, interface or enum
	 */
	ClassType classType;
	
	public JEClass() {
		methods = new HashMap<>();
	}



	public JEClass(String workspaceId,String classId, String className, String classPath, ClassType classType) {
		this.workspaceId = workspaceId;
		this.className = className;
		this.classPath = classPath;
		this.classId = classId;
		this.classType = classType;
		methods = new HashMap<>();
	}

	
	
	public String getClassId() {
		return classId;
	}



	public void setClassId(String classId) {
		this.classId = classId;
	}



	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}



	public ClassType getClassType() {
		return classType;
	}



	public void setClassType(ClassType classType) {
		this.classType = classType;
	}



	public String getWorkspaceId() {
		return workspaceId;
	}



	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}


	public HashMap<String, JEMethod> getMethods() {
		return methods;
	}

	public void setMethods(HashMap<String, JEMethod> methods) {
		this.methods = methods;
	}
}
