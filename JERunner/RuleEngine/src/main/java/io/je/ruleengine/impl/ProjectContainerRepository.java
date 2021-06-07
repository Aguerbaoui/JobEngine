package io.je.ruleengine.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/*
 * In Memory Repository for all the rule engine projects (project container)
 */
public class ProjectContainerRepository {

    //Map of all the projectContainers of this RuleEngine.
    static Map<String, ProjectContainer> allProjects = new ConcurrentHashMap<>();



    /*
     * create new project container
     */
    private ProjectContainer newProjectContainer(String projectId) {
        ProjectContainer project = new ProjectContainer(projectId);
        allProjects.put(projectId, project);
        return project;
    }

    /*
     * get project container by ID
     */
    public ProjectContainer getProjectContainer(String projectId) {
 
        if (projectContainerExists(projectId)) {
            return allProjects.get(projectId);
        }
        return newProjectContainer(projectId);
    }

    /*
     * delete project container
     */

    public void deleteProjectContainer(String projectId) {
        if (projectContainerExists(projectId)) {
            ProjectContainer project = allProjects.get(projectId);
            if (project.getStatus() == Status.RUNNING) {
                //TODO: error management
                project.stopRuleExecution();
            }
            project = null;
            allProjects.remove(projectId);
        }
        //else project doesn't exist


    }

    /*
     * check if a project container with a specific id exists
     */
    public  boolean projectContainerExists(String projectId) {

        return allProjects.containsKey(projectId);
    }


    public static Map<String, ProjectContainer> getAllProjects() {
        return allProjects;
    }
}
