package io.je.project.services;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import io.je.utilities.logger.JELogger;
import io.je.utilities.models.EventType;
import io.je.utilities.string.JEStringUtils;
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
	public void addEvent(String projectId, EventModel eventModel) throws ProjectNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException, EventException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.PROJECT_NOT_FOUND);
		}

		if(!JEStringUtils.isStringOnlyAlphabet(eventModel.getName())) {
			throw new EventException(Errors.NOT_ALPHABETICAL);
		}
		JEEvent event = new JEEvent(eventModel.getEventId(), projectId, eventModel.getName(), EventType.GENERIC_EVENT);
		registerEvent(event);
	}
	
	/*
	 * register event in runner
	 */
	
	public void registerEvent( JEEvent event ) throws ProjectNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		JEProject project = ProjectService.getProjectById(event.getJobEngineProjectID());
		if (project == null) {
			throw new ProjectNotFoundException( Errors.PROJECT_NOT_FOUND);
		}
		

		//TODO: add test on response
		HashMap<String,String> eventMap = new HashMap<String, String>();
		eventMap.put(EventModelMapping.PROJECTID, event.getJobEngineProjectID());
		eventMap.put(EventModelMapping.EVENTNAME, event.getName());
		eventMap.put(EventModelMapping.EVENTID, event.getJobEngineElementID());
		eventMap.put(EventModelMapping.EVENTTYPE, event.getType().toString());
		JERunnerAPIHandler.addEvent(eventMap);
		project.getEvents().put(event.getJobEngineElementID(), event);
	

		
	}

	public void updateEventType(String projectId, String eventId, String eventType) throws ProjectNotFoundException, EventException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.PROJECT_NOT_FOUND); //cdc47cf6-28e9-ff1d-996f-b6b1732771a2 -> {JEEvent@10436}
		}

		JEEvent event = project.getEvents().get(eventId);
		if(!project.getEvents().containsKey(eventId)) {
			for(JEEvent ev: project.getEvents().values()) {
				if(ev.getName().equalsIgnoreCase(eventId)) {
					event = ev;
					break;
				}
			}
		}

		if(event == null)  {
			throw new EventException(Errors.EVENT_NOT_FOUND);
		}

		EventType t = EventType.valueOf(eventType);
		try {
			JERunnerAPIHandler.updateEventType(projectId, eventId, eventType);
			event.setType(t);
		} catch (JERunnerErrorException | InterruptedException | ExecutionException | IOException e) {
			JELogger.error(EventService.class, "Failed to set event type in runner");
		}
	}
	
	/*
	 * delete event
	 */
	
	public void deleteEvent(String projectId, String eventId) throws EventException, ProjectNotFoundException, InterruptedException, JERunnerErrorException, ExecutionException, IOException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.PROJECT_NOT_FOUND);
		}

		JEEvent event = project.getEvents().get(eventId);
		if(event == null) {
			for(JEEvent ev: project.getEvents().values()) {
				if(ev.getName().equalsIgnoreCase(eventId)) {
					event = ev;
					break;
				}
			}
		}

		if(event == null)  {
			throw new EventException(Errors.EVENT_NOT_FOUND);
		}

		JERunnerAPIHandler.deleteEvent(projectId, eventId);
		project.getEvents().remove(eventId);
	}
}
