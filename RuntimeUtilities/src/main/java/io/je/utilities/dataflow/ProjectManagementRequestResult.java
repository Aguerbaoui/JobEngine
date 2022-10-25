package io.je.utilities.dataflow;


import io.je.utilities.models.ProjectModel;

import java.util.List;

public class ProjectManagementRequestResult {
    public List<ProjectModel> projects;
    public boolean isOk=false;
    public String strError;

    public ProjectManagementRequestResult() {

    }
}