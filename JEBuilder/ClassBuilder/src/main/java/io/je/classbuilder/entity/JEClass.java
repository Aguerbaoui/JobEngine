package io.je.classbuilder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="JEClass")
public class JEClass  {
	
	@Id
	String classId; // class id defined in data model
	String className;
	
	/*
	 * path where .java file is saved
	 */
	String classPath;
	
	/*
	 * class type : class, interface or enum
	 */
	ClassType classType;
	
	private JEClass() {
	}



	public JEClass(String classId, String className, String classPath, ClassType classType) {
		this.className = className;
		this.classPath = classPath;
		this.classId = classId;
		this.classType = classType;
		
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

	


	

}
