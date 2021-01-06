package io.je.classbuilder.entity;

import io.je.utilities.runtimeobject.JEObject;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("JEClass")
public class JEClass extends JEObject {
	String className;

	public JEClass(String jobEngineElementID, String jobEngineProjectID, String className) {
		super(jobEngineElementID, jobEngineProjectID);
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}




	

}
