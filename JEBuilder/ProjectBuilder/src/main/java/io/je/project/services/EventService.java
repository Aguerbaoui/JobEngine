package io.je.project.services;

import java.util.Collection;

import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.EventException;
import io.je.utilities.exceptions.ProjectNotFoundException;
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
	public void addEvent(String projectId, EventModel eventModel) throws ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		}
		JEEvent event = new JEEvent(eventModel.getEventId(), projectId, eventModel.getName(), eventModel.getType());
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
