package io.je.project.models;

public class ProjectModel {

    private String projectId;

    private String projectName;
    
    private String configurationPath;

    public String getProjectId() {
        return projectId;
    }

    
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }



	public String getConfigurationPath() {
		return configurationPath;
	}



	public void setConfigurationPath(String configurationPath) {
		this.configurationPath = configurationPath;
	}
    
    
}
