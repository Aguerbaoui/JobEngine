package io.je.utilities.models;

import io.je.utilities.beans.JEVariable;

public class VariableModel {

    private String id;

    private String type; 

    private String projectId;

    private String name;

    private String value;
    
    private String initialValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
    
    
    public String getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	public VariableModel() {}
    public VariableModel(String projectId, String id, String name, String type, String value, String initialValue) {
        this.projectId = projectId;
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.initialValue = initialValue;
    }

    public VariableModel(String projectId,  String name, String type, String value) {
        this.projectId = projectId;
        this.id = name;
        this.name = name;
        this.type = type;
        this.value = value;
    }

	public VariableModel(JEVariable variable) {
		 this.projectId = variable.getJobEngineProjectID();
	        this.id = variable.getJobEngineElementID();
	        this.name = variable.getName();
	        this.type = variable.getType().toString();
	        this.initialValue = String.valueOf(variable.getInitialValue());
	        this.value = null;
	}
}
