package io.je.project.beans.project.receivedrequest.request;

import io.je.project.beans.project.receivedrequest.ProjectActionEnum;
import lombok.Data;

import java.util.Map;

@Data
public class ProjectRequestObject {
    ProjectActionEnum type;
    String projectId;
    Map<String, String> dicOfValues;


}