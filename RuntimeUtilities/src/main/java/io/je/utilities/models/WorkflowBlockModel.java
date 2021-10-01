package io.je.utilities.models;

import java.util.Map;

public class WorkflowBlockModel {

    private String id;

    private String type;

    private Map<String, Object> attributes;

    private String projectId;

    private String workflowId;

    public WorkflowBlockModel(String id, String type, Map<String, Object> attributes) {
        super();
        this.id = id;
        this.type = type;
        this.attributes = attributes;
    }

    public WorkflowBlockModel() {
        super();
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

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }


}
