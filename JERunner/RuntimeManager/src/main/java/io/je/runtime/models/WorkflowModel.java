package io.je.runtime.models;

import java.util.ArrayList;

public class WorkflowModel {

	public String projectId;
	
	public String key;
	
	public String path;
	
	public ArrayList<EventModel> events;
	
	public ArrayList<TaskModel> tasks;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ArrayList<EventModel> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<EventModel> events) {
		this.events = events;
	}

	public ArrayList<TaskModel> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<TaskModel> tasks) {
		this.tasks = tasks;
	}

	public WorkflowModel(String projectId, String key, String path, ArrayList<EventModel> events,
			ArrayList<TaskModel> tasks) {
		super();
		this.projectId = projectId;
		this.key = key;
		this.path = path;
		this.events = events;
		this.tasks = tasks;
	}

	public WorkflowModel() {
		super();
	}
	
}
