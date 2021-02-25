package io.je.project.controllers;

import java.util.Collection;

import io.je.project.exception.JEExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.EventModel;
import io.je.utilities.network.JEResponse;

/*
 * Project Rest Controller
 * */
@RestController
@RequestMapping(value = "/event")
@CrossOrigin(maxAge = 3600)
public class EventController {

	@Autowired
	ProjectService projectService;

	@Autowired
	EventService eventService;

	/*
	 * Retrieve all events in a project
	 */
	@GetMapping(value = "{projectId}/getAllEvents")
	@ResponseBody
	public ResponseEntity<?> getAllEvents(@PathVariable("projectId") String projectId) {
		Collection<?> events = null;
		try {
			events = eventService.getAllEvents(projectId);
			if (events.isEmpty()) {
				return ResponseEntity.noContent().build();

			}
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return new ResponseEntity<Object>(events, HttpStatus.OK);

	}

	/*
	 * Retrieve all events in a project
	 */
	@GetMapping(value = "{projectId}/getEvent/{eventId}")
	@ResponseBody
	public ResponseEntity<?> getEvent(@PathVariable("projectId") String projectId,
			@PathVariable("eventId") String eventId) {
		JEEvent event = null;
		JELogger.info(getClass(), " getting event [ id="+eventId+"]");

		try {
			event = eventService.getEvent(projectId, eventId);
			if (event == null) {
				return ResponseEntity.noContent().build();

			}
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return new ResponseEntity<Object>(new EventModel(event), HttpStatus.OK);

	}

	/*
	 * add event
	 */
	@PostMapping(value = "/{projectId}/addEvent", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addEvent(@PathVariable("projectId") String projectId, @RequestBody EventModel eventModel) {

		try {
			JELogger.info(getClass(), " adding event [ id="+eventModel.getEventId()+"]");
			eventService.addEvent(projectId, eventModel);
			projectService.saveProject(projectId);
			JELogger.info(getClass(), ResponseMessages.EVENT_ADDED);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		JELogger.info(getClass(), "  event [ id="+eventModel.getEventId()+"] added successfully");
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.EVENT_ADDED));
	}
	
	

	/*
	 * update event
	 */
	@PostMapping(value = "/{projectId}/updateEvent", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateEvent(@PathVariable("projectId") String projectId, @RequestBody EventModel eventModel) {

		try {
			JELogger.info(getClass(), " updating event [ id="+eventModel.getEventId()+"]");
			eventService.updateEvent(projectId, eventModel);
			projectService.saveProject(projectId);
			JELogger.info(getClass(), ResponseMessages.EVENT_ADDED);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		JELogger.info(getClass(), "  event [ id="+eventModel.getEventId()+"] updated successfully");
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.EVENT_UPDATED));
	}


	/*
	 * delete event
	 */
	@DeleteMapping(value = "{projectId}/deleteEvent/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteEvent(@PathVariable("projectId") String projectId,
			@PathVariable("eventId") String eventId) {

		try {
			JELogger.info(getClass(), " deleting event [ id="+eventId+"]");
			eventService.deleteEvent(projectId, eventId);
			projectService.saveProject(projectId);

		} catch (Exception e) {
			JELogger.info(getClass(), "error deleting event");
			return JEExceptionHandler.handleException(e);
			
		}
		JELogger.info(getClass(), "  event [ id="+eventId+"] deleted successfully");
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.EVENT_DELETED));
	}
}
