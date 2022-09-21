package io.je.project.beans.project.sentrequest.response;

import java.util.List;

public class ProjectManagementRequestResult {
	public List<ProjectModel> projects;
	public boolean isOk=false;
	public String strError;

	public ProjectManagementRequestResult() {
		
	}
}