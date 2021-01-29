package io.je.project.services;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.EventException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.mapping.EventModelMapping;
import io.je.utilities.models.EventModel;

@Service
public class EventService {

	/*
	 * Retrieve list of all events that exist in a project.
	 */
	public Collection<JEEvent> getAllEvents(String projectId) throws ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		}
		return project.getEvents().values();
	}

	/*
	 * retrieve event from project by id
	 */
	public JEEvent getEvent(String projectId, String eventId) throws EventException, ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		}
		return project.getEvent(eventId);
	}

	/*
	 * add new event
	 */
	public void addEvent(String projectId, EventModel eventModel) throws ProjectNotFoundException, JERunnerErrorException, IOException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		}
		JEEvent event = new JEEvent(eventModel.getEventId(), projectId, eventModel.getName(), eventModel.getType());
		//TODO: add test on response
		HashMap<String,String> eventMap = new HashMap<String, String>();
		eventMap.put(EventModelMapping.PROJECTID, eventModel.getProjectId());
		eventMap.put(EventModelMapping.EVENTNAME, eventModel.getName());
		eventMap.put(EventModelMapping.EVENTTYPE, eventModel.getType().toString());
		eventMap.put(EventModelMapping.EVENTID, eventModel.getEventId());
		
		JERunnerAPIHandler.addEvent(eventMap);
		project.getEvents().put(eventModel.getEventId(), event);
		
	}
	
	
	/*
	 * delete event
	 */
	public JEEvent deleteEvent(String projectId, String eventId) throws EventException, ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		}
		return project.getEvents().remove(eventId);
	}
}
