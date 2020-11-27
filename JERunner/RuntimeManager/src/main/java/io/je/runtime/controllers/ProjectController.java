package io.je.runtime.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.je.utilities.response.Response;

@RestController
public class ProjectController {
	
	
	@RequestMapping(value = "/buildProject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> buildProject(@RequestBody String input) {		
		Response response = new Response();
		return new ResponseEntity<Object>(response,HttpStatus.OK);
		
	}
	
	
	@RequestMapping(value = "/runProject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> runProject(@RequestBody String input) {		
		Response response = new Response();
		return new ResponseEntity<Object>(response,HttpStatus.OK);
		
	}

	@RequestMapping(value = "/stopProject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> stopProject(@RequestBody String input) {		
		Response response = new Response();
		return new ResponseEntity<Object>(response,HttpStatus.OK);
		
	}


}
