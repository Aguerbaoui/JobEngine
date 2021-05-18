package io.je.project.services;

import java.io.IOException;
import java.util.ArrayList;
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
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ConfigException;
import io.je.utilities.exceptions.EventException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.mapping.EventModelMapping;
import io.je.utilities.models.EventModel;

import static io.je.utilities.constants.JEMessages.UPDATING_EVENT;

@Service
public class EventService {

	/*
	 * Retrieve list of all events that exist in a project.
	 */
	
	public Collection<EventModel> getAllEvents(String projectId) throws ProjectNotFoundException {
		JELogger.trace(  "[project id = " + projectId + "] " + JEMessages.LOADING_EVENTS);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}
		ArrayList<EventModel> eventModels = new ArrayList<EventModel>();
		for(JEEvent event: project.getEvents().values())
		{
			eventModels.add(new EventModel(event));
		}

		JELogger.trace(" Found " + eventModels.size() + " events");
		return eventModels;
	}

	/*
	 * retrieve event from project by id
	 */
	
	public JEEvent getEvent(String projectId, String eventId) throws EventException, ProjectNotFoundException {
		JELogger.info(getClass(), JEMessages.LOADING_EVENTS +" [ id="+eventId+"] in project id =  " + projectId);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}
		return project.getEvent(eventId);
	}

	/*
	 * add new event
	 */
	public void addEvent(String projectId, EventModel eventModel) throws ProjectNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException, EventException, ConfigException {
    	ConfigurationService.checkConfig();
		JELogger.info(getClass(),  JEMessages.ADDING_EVENT+ "[ id="+eventModel.getEventId()+"] in project id = " + projectId);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}
		
		if(project.getEvents().contains(eventModel.getEventId()))
		{
			throw new EventException(JEMessages.EVENT_ALREADY_EXISTS);
		}

		if(!JEStringUtils.isStringOnlyAlphabet(eventModel.getName())) {
			throw new EventException(JEMessages.NOT_ALPHABETICAL);
		}
		JEEvent event = new JEEvent(eventModel.getEventId(), projectId, eventModel.getName(), EventType.GENERIC_EVENT);
		event.setDescription(eventModel.getDescription());
		registerEvent(event);
	}
	
	/*
	 * update new event
	 */
	public void updateEvent(String projectId, EventModel eventModel) throws ProjectNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException, EventException, ConfigException {
    	ConfigurationService.checkConfig();
		JELogger.info(getClass(), UPDATING_EVENT + " [ id="+eventModel.getEventId()+"] in project id = " + projectId);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}
		
		if(!project.getEvents().containsKey(eventModel.getEventId()))
		{
			throw new EventException(JEMessages.EVENT_NOT_FOUND);
		}

		if(!JEStringUtils.isStringOnlyAlphabet(eventModel.getName())) {
			throw new EventException(JEMessages.NOT_ALPHABETICAL);
		}
		JEEvent event = new JEEvent(eventModel.getEventId(), projectId, eventModel.getName(), EventType.GENERIC_EVENT);
		event.setDescription(eventModel.getDescription());
		registerEvent(event);
	}
	
	/*
	 * register event in runner
	 */
	
	public void registerEvent( JEEvent event ) throws ProjectNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException, ConfigException {
    	ConfigurationService.checkConfig();
		JEProject project = ProjectService.getProjectById(event.getJobEngineProjectID());
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}

		//TODO: add test on response
		HashMap<String,String> eventMap = new HashMap<String, String>();
		eventMap.put(EventModelMapping.PROJECTID, event.getJobEngineProjectID());
		eventMap.put(EventModelMapping.EVENTNAME, event.getName());
		eventMap.put(EventModelMapping.EVENTID, event.getJobEngineElementID());
		eventMap.put(EventModelMapping.EVENTTYPE, event.getType().toString());
		JELogger.trace(JEMessages.REGISTERING_EVENT);
		JERunnerAPIHandler.addEvent(eventMap);
		project.addEvent(event);
	

		
	}

	public void updateEventType(String projectId, String eventId, String eventType) throws ProjectNotFoundException, EventException, ConfigException {
    	ConfigurationService.checkConfig();
		JELogger.trace(JEMessages.UPDATING_EVENT_TYPE + eventType + " for event id = " + eventId + " in project id = " + projectId);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND); //cdc47cf6-28e9-ff1d-996f-b6b1732771a2 -> {JEEvent@10436}
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
			throw new EventException(JEMessages.EVENT_NOT_FOUND);
		}

		EventType t = EventType.valueOf(eventType);
		try {
			JELogger.trace(" " + JEMessages.UPDATING_EVENT_TYPE_IN_RUNNER);
			JERunnerAPIHandler.updateEventType(projectId, eventId, eventType);
			event.setType(t);
		} catch (JERunnerErrorException | InterruptedException | ExecutionException | IOException e) {
			JELogger.error(EventService.class, JEMessages.UPDATING_EVENT_TYPE_FAILED);
		}
	}
	
	/*
	 * delete event
	 */
	
	public void deleteEvent(String projectId, String eventId) throws EventException, ProjectNotFoundException, InterruptedException, JERunnerErrorException, ExecutionException, IOException, ConfigException {
    	ConfigurationService.checkConfig();
		JELogger.info(getClass(), JEMessages.DELETING_EVENT+"[ id="+eventId+"] in project id = " + projectId);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
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
			throw new EventException(JEMessages.EVENT_NOT_FOUND);
		}
		JELogger.info(getClass(), JEMessages.DELETING_EVENT_FROM_RUNNER);
		JERunnerAPIHandler.deleteEvent(projectId, eventId);
		project.getEvents().remove(event.getJobEngineElementID());
	}

	public void triggerEvent(String projectId, String eventId) {
		try {
			JERunnerAPIHandler.triggerEvent(eventId, projectId);

		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public void stopEvent(String projectId, String eventId) {
		try {
			JERunnerAPIHandler.stopEvent(eventId, projectId);

		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}
