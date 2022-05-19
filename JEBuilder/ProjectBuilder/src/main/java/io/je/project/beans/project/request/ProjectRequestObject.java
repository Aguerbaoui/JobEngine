package io.je.project.beans.project.request;

import lombok.Data;

import java.util.Map;

@Data
public class ProjectRequestObject {
    ProjectActionEnum type;
    String projectId;
    Map<String, String> dicOfValues;

    public ProjectRequestObject() {
        super();
        // TODO Auto-generated constructor stub
    }


}