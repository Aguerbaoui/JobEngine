package io.je.project.controllers;

import java.util.Collection;

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
import io.je.utilities.exceptions.EventException;
import io.je.utilities.exceptions.ProjectNotFoundException;
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
	@GetMapping(value="{projectId}/getAllevents")
	@ResponseBody
	public ResponseEntity<?> getAllEvents(@PathVariable("projectId") String projectId) {
		Collection<?> events = null;
		try {
			 events = eventService.getAllEvents(projectId);
			 if(events.isEmpty())
			 {
					return ResponseEntity.noContent().build();

			 }
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
			JELogger.error(EventController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		}
		
		return	new ResponseEntity<Object>(events,HttpStatus.OK);
	
}
	
	
	/*
	 * Retrieve all events in a project
	 */
	@GetMapping(value="{projectId}/getEvent/{eventId}")
	@ResponseBody
	public ResponseEntity<?> getEvent(@PathVariable("projectId") String projectId,@PathVariable("eventId") String eventId)   {
		JEEvent event = null;
		try {
			 event = eventService.getEvent(projectId,eventId);
			 if(event == null)
			 {
					return ResponseEntity.noContent().build();

			 }
		} catch (ProjectNotFoundException | EventException e) {
			e.printStackTrace();
			JELogger.error(EventController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		}
		
		return	new ResponseEntity<Object>(event,HttpStatus.OK);
	
}
	
	/*
	 * add event
	 */
	@PostMapping(value = "/{projectId}/addEvent", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addEvent(@PathVariable("projectId") String projectId, @RequestBody EventModel eventModel) {
		
			try {
				eventService.addEvent(projectId,eventModel);
				projectService.saveProject(ProjectService.getProjectById(projectId));
				JELogger.info(getClass(), ResponseMessages.EVENT_ADDED);

			} catch (ProjectNotFoundException e) {
				//e.printStackTrace();
				JELogger.error(EventController.class, e.getMessage());
				return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
			}
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.EVENT_ADDED));
	}

	
	/*
	 * delete event
	 */
	@DeleteMapping(value = "{projectId}/deleteEvent/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteEvent(@PathVariable("projectId") String projectId,@PathVariable("eventId") String eventId) {
		
			try {
				eventService.deleteEvent(projectId,eventId);
				projectService.saveProject(ProjectService.getProjectById(projectId));
				JELogger.info(getClass(), ResponseMessages.EVENT_ADDED);

			} catch (ProjectNotFoundException | EventException e) {
				//e.printStackTrace();
				JELogger.error(EventController.class, e.getMessage());
				return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
			}
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.EVENT_ADDED));
	}
}
