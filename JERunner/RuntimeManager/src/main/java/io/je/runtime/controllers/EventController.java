package io.je.runtime.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
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

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleAdditionSucceeded));
	}
	

    /*
     * trigger event
     * */
    @GetMapping(value = "/triggerEvent/{projectId}/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> triggerEvent(@PathVariable String projectId, @PathVariable String eventId) {
    	runtimeDispatcher.triggerEvent(projectId, eventId);
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.TOPIC_ADDED));
    }


}
