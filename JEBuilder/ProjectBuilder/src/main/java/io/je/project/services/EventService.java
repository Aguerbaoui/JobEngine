package io.je.project.services;

import static io.je.utilities.constants.JEMessages.ADDING_JAR_FILE_TO_RUNNER;
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

import io.je.utilities.models.EventType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.project.config.LicenseProperties;
import io.je.project.repository.EventRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ConfigException;
import io.je.utilities.exceptions.EventException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.LicenseNotActiveException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.log.JELogger;
import io.je.utilities.mapping.EventModelMapping;
import io.je.utilities.models.EventModel;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.string.StringUtilities;

@Service
public class EventService {
	
	@Autowired
	EventRepository eventRepository;
	
	
	
	/*
	 * Retrieve list of all events that exist in a project.
	 */
	
	public Collection<EventModel> getAllEvents(String projectId) throws LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();

		List<JEEvent> events = eventRepository.findByJobEngineProjectID(projectId);
		ArrayList<EventModel> eventModels = new ArrayList<EventModel>();
		for(JEEvent event: events)
		{
			eventModels.add(new EventModel(event));
		}
		JELogger.debug(" Found " + eventModels.size() + " events",
				LogCategory.DESIGN_MODE, projectId,
				LogSubModule.EVENT, null);
		return eventModels;
	}
	
	public ConcurrentHashMap<String, JEEvent> getAllJEEvents(String projectId) throws LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();

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
	
	public EventModel getEvent(String projectId, String eventId) throws LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();

		Optional<JEEvent> event = eventRepository.findById(eventId);
		if(event.isPresent())
		{
			return new EventModel(event.get());
		}
		return null;
	}

	/*
	 * add new event
	 */
	public void addEvent(String projectId, EventModel eventModel) throws ProjectNotFoundException,  EventException, LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();

		JELogger.debug(JEMessages.ADDING_EVENT+ "[ id="+eventModel.getName()+"] in project id = " + projectId,
				LogCategory.DESIGN_MODE, projectId,
				LogSubModule.EVENT, eventModel.getEventId());
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}
		
		if(project.getEvents().contains(eventModel.getEventId()))
		{
			throw new EventException(JEMessages.EVENT_ALREADY_EXISTS);
		}

		if(!StringUtilities.isStringOnlyAlphabet(eventModel.getName())) {
			throw new EventException(JEMessages.NOT_ALPHABETICAL);
		}
		JEEvent event = new JEEvent(eventModel.getEventId(), projectId, eventModel.getName(), EventType.GENERIC_EVENT,eventModel.getDescription(),eventModel.getTimeout(),eventModel.getTimeoutUnit(),eventModel.getCreatedBy(),eventModel.getModifiedBy());
		registerEvent(event);
		eventRepository.save(event);
		
	}
	
	
		
		
	
	
	

	/*
	 * update new event
	 */
	public void updateEvent(String projectId, EventModel eventModel) throws ProjectNotFoundException, EventException, LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();

		JELogger.debug(UPDATING_EVENT + " [ id="+eventModel.getName()+"] in project id = " + projectId,
				LogCategory.DESIGN_MODE, projectId,
				LogSubModule.EVENT, eventModel.getEventId());
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}
		
		if(!project.getEvents().containsKey(eventModel.getEventId()))
		{
			throw new EventException(JEMessages.EVENT_NOT_FOUND);
		}

		if(!StringUtilities.isStringOnlyAlphabet(eventModel.getName())) {
			throw new EventException(JEMessages.NOT_ALPHABETICAL);
		}
		JEEvent event = new JEEvent(eventModel.getEventId(), projectId, eventModel.getName(), EventType.GENERIC_EVENT,eventModel.getDescription(),eventModel.getTimeout(),eventModel.getTimeoutUnit(),eventModel.getCreatedBy(),eventModel.getModifiedBy());
		registerEvent(event);
		eventRepository.save(event);

		
	}
	
	/*
	 * register event in runner
	 */
	
	public void registerEvent( JEEvent event ) throws ProjectNotFoundException, LicenseNotActiveException, EventException {
    	LicenseProperties.checkLicenseIsActive();

		JEProject project = ProjectService.getProjectById(event.getJobEngineProjectID());
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}

		//TODO: add test on response
		HashMap<String,Object> eventMap = new HashMap<String, Object>();
		eventMap.put(EventModelMapping.PROJECTID, event.getJobEngineProjectID());
		eventMap.put(EventModelMapping.EVENTNAME, event.getJobEngineElementName());
		eventMap.put(EventModelMapping.EVENTID, event.getJobEngineElementID());
		eventMap.put(EventModelMapping.EVENTTYPE, event.getType().toString());
		eventMap.put(EventModelMapping.TIMEOUTUNIT, event.getTimeoutUnit());
		eventMap.put(EventModelMapping.TIMOUTVALUE, event.getTimeoutValue());
		JELogger.debug(JEMessages.REGISTERING_EVENT,
				LogCategory.DESIGN_MODE, event.getJobEngineProjectID(),
				LogSubModule.EVENT,event.getJobEngineElementID());
		try {
			JERunnerAPIHandler.addEvent(eventMap);
		}
		catch(JERunnerErrorException e) {
			throw new EventException(JEMessages.FAILED_TO_ADD_EVENT);
		}
		project.addEvent(event);
	

		
	}

	public void updateEventType(String projectId, String eventId, String eventType) throws ProjectNotFoundException, EventException, ConfigException, LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();


		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND); //cdc47cf6-28e9-ff1d-996f-b6b1732771a2 -> {JEEvent@10436}
		}

		JEEvent event = project.getEvents().get(eventId);

		if(!project.getEvents().containsKey(eventId)) {
			for(JEEvent ev: project.getEvents().values()) {
				if(ev.getJobEngineElementName().equalsIgnoreCase(eventId)) {
					event = ev;
					break;
				}
			}
		}

		if(event == null)  {
			throw new EventException(JEMessages.EVENT_NOT_FOUND);
		}
		JELogger.debug(JEMessages.UPDATING_EVENT_TYPE + eventType + " for event id = " + event.getJobEngineElementName() + " in project id = " + projectId,
				LogCategory.DESIGN_MODE, projectId,
				LogSubModule.EVENT,eventId);
		EventType t = EventType.valueOf(eventType);
		try {
			JELogger.debug(JEMessages.UPDATING_EVENT_TYPE_IN_RUNNER,
					LogCategory.DESIGN_MODE, projectId,
					LogSubModule.EVENT,eventId);
			JERunnerAPIHandler.updateEventType(projectId, eventId, eventType);
		} catch (Exception e) {
			throw new EventException(JEMessages.EVENT_NOT_FOUND);
		}
		event.setType(t);
		eventRepository.save(event);
	}
	
	/*
	 * delete event
	 */
	
	public void deleteEvent(String projectId, String eventId) throws EventException, ProjectNotFoundException, LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();

		JELogger.debug(JEMessages.DELETING_EVENT+"[ id="+eventId+"] in project id = " + projectId,
				LogCategory.DESIGN_MODE, projectId,
				LogSubModule.EVENT,eventId);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( JEMessages.PROJECT_NOT_FOUND);
		}

		JEEvent event = project.getEvents().get(eventId);
		if(event == null) {
			for(JEEvent ev: project.getEvents().values()) {
				if(ev.getJobEngineElementName().equalsIgnoreCase(eventId)) {
					event = ev;
					break;
				}
			}
		}

		if(event == null)  {
			throw new EventException(JEMessages.EVENT_NOT_FOUND);
		}
		JELogger.debug(JEMessages.DELETING_EVENT_FROM_RUNNER,
				LogCategory.DESIGN_MODE, projectId,
				LogSubModule.EVENT,eventId);
		try {
			JERunnerAPIHandler.deleteEvent(projectId, eventId);
		}
		catch(JERunnerErrorException e) {
			throw new EventException(JEMessages.ERROR_DELETING_EVENT);
		}
		project.getEvents().remove(event.getJobEngineElementID());
		eventRepository.deleteById(eventId);
		
	}

	/*public void triggerEvent(String projectId, String eventId) throws ConfigException, ProjectNotFoundException, EventException {
		
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

	}*/

	public void deleteAll(String projectId) throws LicenseNotActiveException {
    	LicenseProperties.checkLicenseIsActive();

		JELogger.debug(JEMessages.DELETING_EVENTS,
				LogCategory.DESIGN_MODE, projectId,
				LogSubModule.EVENT,null);
		eventRepository.deleteByJobEngineProjectID(projectId);
		
	}
}
