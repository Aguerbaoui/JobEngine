package io.je.classbuilder.entity;

import io.je.utilities.runtimeobject.JEObject;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="JEClass")
public class JEClass  {
	String classId;
	String className;
	String classPath;

	

	
	private JEClass() {
	}



	public JEClass(String classId, String className, String classPath) {
		this.className = className;
		this.classPath = classPath;
		this.classId = classId;
		
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

	


	

}
