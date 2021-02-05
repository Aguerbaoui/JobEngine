package io.je.project.services;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import io.je.utilities.models.EventType;
import jdk.jfr.Event;
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
			throw new ProjectNotFoundException( Errors.PROJECT_NOT_FOUND);
		}
		return project.getEvents().values();
	}

	/*
	 * retrieve event from project by id
	 */
	public JEEvent getEvent(String projectId, String eventId) throws EventException, ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.PROJECT_NOT_FOUND);
		}
		return project.getEvent(eventId);
	}

	/*
	 * add new event
	 */
	public void addEvent(String projectId, EventModel eventModel) throws ProjectNotFoundException, JERunnerErrorException, IOException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.PROJECT_NOT_FOUND);
		}
		JEEvent event = new JEEvent(eventModel.getEventId(), projectId, eventModel.getName(), EventType.GENERIC_EVENT);
		registerEvent(event);
		
	}
	
	/*
	 * add new event
	 */
	public void registerEvent( JEEvent event ) throws ProjectNotFoundException, JERunnerErrorException, IOException {
		JEProject project = ProjectService.getProjectById(event.getJobEngineProjectID());
		if (project == null) {
			throw new ProjectNotFoundException( Errors.PROJECT_NOT_FOUND);
		}

		//TODO: add test on response
		HashMap<String,String> eventMap = new HashMap<String, String>();
		eventMap.put(EventModelMapping.PROJECTID, event.getJobEngineProjectID());
		eventMap.put(EventModelMapping.EVENTNAME, event.getName());
		eventMap.put(EventModelMapping.EVENTID, event.getJobEngineElementID());
		JERunnerAPIHandler.addEvent(eventMap);
		project.getEvents().put(event.getJobEngineElementID(), event);
		
	}
	
	
	/*
	 * delete event
	 */
	public JEEvent deleteEvent(String projectId, String eventId) throws EventException, ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.PROJECT_NOT_FOUND);
		}
		return project.getEvents().remove(eventId);
	}
}
