package io.je.utilities.models;

import io.je.utilities.beans.JEVariable;


public class VariableModel {

    private String id;

    private String type;

    private String projectId;

    private String projectName;

    private String name;

    private Object value;

    private String description;

    private String initialValue;

    private String createdAt;

    private String lastModifiedAt;

    private String createdBy;

    private String modifiedBy;


    public VariableModel() {
    }

    public VariableModel(JEVariable variable) {
        this.projectId = variable.getJobEngineProjectID();
        this.projectName = variable.getJobEngineProjectName();
        this.id = variable.getJobEngineElementID();
        this.name = variable.getJobEngineElementName();
        this.type = variable.getType().toString();
        this.initialValue = String.valueOf(variable.getInitialValue());
        this.createdAt = variable.getJeObjectCreationDate().toString();
        this.lastModifiedAt = variable.getJeObjectLastUpdate().toString();
        this.value = variable.getValue();
        this.createdBy = variable.getJeObjectCreatedBy();
        this.modifiedBy = variable.getJeObjectModifiedBy();
        this.description = variable.getDescription();
    }

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

    public Object getValue() {
        return value != null ? value : initialValue;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(String lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
