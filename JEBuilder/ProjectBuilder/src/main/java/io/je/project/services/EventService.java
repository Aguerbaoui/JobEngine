package io.je.project.services;

import static io.je.utilities.constants.JEMessages.UPDATING_EVENT;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import io.je.utilities.logger.JELogger;
import io.je.utilities.models.EventType;
import io.je.utilities.string.JEStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.project.repository.EventRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ConfigException;
import io.je.utilities.exceptions.EventException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.mapping.EventModelMapping;
import io.je.utilities.models.EventModel;

@Service
public class EventService {
	
	@Autowired
	EventRepository eventRepository;
	
	
	
	/*
	 * Retrieve list of all events that exist in a project.
	 */
	
	public Collection<EventModel> getAllEvents(String projectId) throws ProjectNotFoundException {
		List<JEEvent> events = eventRepository.findByJobEngineProjectID(projectId);
		ArrayList<EventModel> eventModels = new ArrayList<EventModel>();
		for(JEEvent event: events)
		{
			eventModels.add(new EventModel(event));
		}

		JELogger.trace(" Found " + eventModels.size() + " events");
		return eventModels;
	}
	
	public ConcurrentHashMap<String, JEEvent> getAllJEEvents(String projectId) throws ProjectNotFoundException {
		List<JEEvent> events = eventRepository.findByJobEngineProjectID(projectId);
		ConcurrentHashMap<String, JEEvent> map = new ConcurrentHashMap<String, JEEvent>();
		for(JEEvent event : events )
		{
			map.put(event.getJobEngineElementID(), event);
		}
		return map;
	}

	/*
	 * retrieve event from project by id
	 */
	
	public JEEvent getEvent(String projectId, String eventId) throws EventException, ProjectNotFoundException {
		Optional<JEEvent> event = eventRepository.findById(eventId);
		return event.orElse(null);
	}

	/*
	 * add new event
	 */
	public void addEvent(String projectId, EventModel eventModel) throws ProjectNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException, EventException, ConfigException {
    	
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
		JEEvent event = new JEEvent(eventModel.getEventId(), projectId, eventModel.getName(), EventType.GENERIC_EVENT,eventModel.getDescription(),eventModel.getTimeout(),eventModel.getTimeoutUnit());
		registerEvent(event);
		eventRepository.save(event);
		
	}
	
	
		
		
	
	
	

	/*
	 * update new event
	 */
	public void updateEvent(String projectId, EventModel eventModel) throws ProjectNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException, EventException, ConfigException {
    	
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
		JEEvent event = new JEEvent(eventModel.getEventId(), projectId, eventModel.getName(), EventType.GENERIC_EVENT,eventModel.getDescription(),eventModel.getTimeout(),eventModel.getTimeoutUnit());
		registerEvent(event);
		eventRepository.save(event);

		
	}
	
	/*
	 * register event in runner
	 */
	
	public void registerEvent( JEEvent event ) throws ProjectNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException, ConfigException {
    	
		JEProject project = ProjectService.getProjectById(event.getJobEngineProjectID());
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}

		//TODO: add test on response
		HashMap<String,Object> eventMap = new HashMap<String, Object>();
		eventMap.put(EventModelMapping.PROJECTID, event.getJobEngineProjectID());
		eventMap.put(EventModelMapping.EVENTNAME, event.getName());
		eventMap.put(EventModelMapping.EVENTID, event.getJobEngineElementID());
		eventMap.put(EventModelMapping.EVENTTYPE, event.getType().toString());
		eventMap.put(EventModelMapping.TIMEOUTUNIT, event.getTimeoutUnit());
		eventMap.put(EventModelMapping.TIMOUTVALUE, event.getTimeoutValue());
		JELogger.trace(JEMessages.REGISTERING_EVENT);
		JERunnerAPIHandler.addEvent(eventMap);
		project.addEvent(event);
	

		
	}

	public void updateEventType(String projectId, String eventId, String eventType) throws ProjectNotFoundException, EventException, ConfigException {
    	
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
			eventRepository.save(event);
		} catch (JERunnerErrorException | InterruptedException | ExecutionException | IOException e) {
			JELogger.error(EventService.class, JEMessages.UPDATING_EVENT_TYPE_FAILED);
		}
	}
	
	/*
	 * delete event
	 */
	
	public void deleteEvent(String projectId, String eventId) throws EventException, ProjectNotFoundException, InterruptedException, JERunnerErrorException, ExecutionException, IOException, ConfigException {
    	
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
		eventRepository.deleteById(eventId);
		
	}

	public void triggerEvent(String projectId, String eventId) throws ConfigException, ProjectNotFoundException, EventException {
		
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND); //cdc47cf6-28e9-ff1d-996f-b6b1732771a2 -> {JEEvent@10436}
		}
		if(!project.getEvents().containsKey(eventId))
		{
			throw new EventException(JEMessages.EVENT_NOT_FOUND);
		}

		JEEvent event = project.getEvents().get(eventId);
		event.setTriggered(true);
		event.setJeObjectLastUpdate(LocalDateTime.now());

	}

	public void untriggerEvent(String projectId, String eventId) throws ConfigException, ProjectNotFoundException, EventException {
		
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND); //cdc47cf6-28e9-ff1d-996f-b6b1732771a2 -> {JEEvent@10436}
		}
		if(!project.getEvents().containsKey(eventId))
		{
			throw new EventException(JEMessages.EVENT_NOT_FOUND);
		}

		JEEvent event = project.getEvents().get(eventId);
		event.setTriggered(false);
		event.setJeObjectLastUpdate(LocalDateTime.now());

	}

	public void deleteAll(String projectId) {
		eventRepository.deleteByJobEngineProjectID(projectId);
		
	}
}
