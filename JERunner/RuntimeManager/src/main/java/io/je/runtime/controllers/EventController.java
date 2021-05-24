package io.je.runtime.controllers;

import io.je.project.exception.JEExceptionHandler;
import io.je.utilities.logger.JELogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.models.EventModel;
import io.je.utilities.network.JEResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
 * Rule Controller Class
 */

@RestController
@RequestMapping(value = "/event")
@CrossOrigin(maxAge = 3600)
public class EventController {

	@Autowired
	RuntimeDispatcher runtimeDispatcher ;

	/*
	 * add a new event
	 */
	@PostMapping(value = "/addEvent", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addEvent(@RequestBody EventModel eventModel) {

		runtimeDispatcher.addEvent(eventModel);
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_ADDED_SUCCESSFULLY));
	}
	

    /*
     * trigger event
     * */
    @GetMapping(value = "/triggerEvent/{projectId}/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> triggerEvent(@PathVariable String projectId, @PathVariable String eventId) {
		try {
			runtimeDispatcher.triggerEvent(projectId, eventId);
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.TOPIC_ADDED));
    }
    
    


    

	@PostMapping(value = "/updateEventType/{projectId}/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateEventType(@PathVariable("projectId") String projectId,@PathVariable("eventId") String eventId, @RequestBody String eventType) {

		try {
			runtimeDispatcher.updateEventType(projectId, eventId, eventType);
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.EVENT_ADDED));
	}

	/*
	 * delete event
	 */
	@DeleteMapping(value = "/deleteEvent/{projectId}/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteEvent(@PathVariable("projectId") String projectId,
										 @PathVariable("eventId") String eventId) {

		try {
			JELogger.info(JEMessages.DELETING_EVENT + " [ id="+eventId+"]");
			runtimeDispatcher.deleteEvent(projectId, eventId);

		} catch (Exception e) {
			JELogger.info(getClass(), JEMessages.ERROR_DELETING_EVENT);
			return JEExceptionHandler.handleException(e);
		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.EVENT_DELETED));
	}

}
