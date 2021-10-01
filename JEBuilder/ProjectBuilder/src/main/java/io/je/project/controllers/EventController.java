package io.je.project.controllers;

import java.util.Collection;

import io.je.project.exception.JEExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.je.project.services.EventService;
import io.je.project.services.ProjectService;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.models.EventModel;
import io.je.utilities.beans.JEResponse;

/*
 * Project Rest Controller
 * */
@RestController
@RequestMapping(value = "/event")
@CrossOrigin(maxAge = 3600)
public class EventController {



	@Autowired
	EventService eventService;
	
	@Autowired
	ProjectService projectService;

	/*
	 * Retrieve all events in a project
	 */
	@GetMapping(value = "{projectId}/getAllEvents")
	@ResponseBody
	public ResponseEntity<?> getAllEvents(@PathVariable("projectId") String projectId) {
		Collection<?> events = null;
		try {
			projectService.getProject(projectId);

			events = eventService.getAllEvents(projectId);
			/*if (events.isEmpty()) {
				return ResponseEntity.noContent().build();

			}*/
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(events);

	}

	/*
	 * Retrieve an event from a project
	 */
	@GetMapping(value = "{projectId}/getEvent/{eventId}")
	@ResponseBody
	public ResponseEntity<?> getEvent(@PathVariable("projectId") String projectId,
			@PathVariable("eventId") String eventId) {
		EventModel event = null;

		try {
			projectService.getProject(projectId);
			event = eventService.getEvent(projectId, eventId);
			if (event == null) {
				return ResponseEntity.noContent().build();

			}
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(event);

	}

	/*
	 * add event
	 */
	@PostMapping(value = "/{projectId}/addEvent", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addEvent(@PathVariable("projectId") String projectId, @RequestBody EventModel eventModel) {

		try {
			projectService.getProject(projectId);

			eventService.addEvent(projectId, eventModel);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.EVENT_ADDED));
	}
	
	
	

	
	

	/*
	 * update event
	 */
	@PostMapping(value = "/{projectId}/updateEvent", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateEvent(@PathVariable("projectId") String projectId, @RequestBody EventModel eventModel) {

		try {
			projectService.getProject(projectId);

			eventService.updateEvent(projectId, eventModel);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.EVENT_UPDATED));
	}


	/*
	 * delete event
	 */
	@DeleteMapping(value = "{projectId}/deleteEvent/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteEvent(@PathVariable("projectId") String projectId,
			@PathVariable("eventId") String eventId) {

		try {
			projectService.getProject(projectId);
			eventService.deleteEvent(projectId, eventId);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
			
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.EVENT_DELETED));
	}
	
	
	
	
	/*
	 * to be deleted 
	 */
	
	
	
	/*
	 * trigger event
	 */
	/*@GetMapping(value = "/triggerEvent/{projectId}/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> triggerEvent(@PathVariable("projectId") String projectId, @PathVariable("eventId") String eventId) {

		try {

			eventService.triggerEvent(projectId, eventId);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.EVENT_ADDED));
	}*/

	
	
}
