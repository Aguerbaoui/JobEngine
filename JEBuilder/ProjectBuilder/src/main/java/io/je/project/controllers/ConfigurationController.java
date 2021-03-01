package io.je.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.je.project.exception.JEExceptionHandler;
import io.je.project.services.ConfigurationService;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.models.ConfigModel;
import io.je.utilities.network.JEResponse;

@RestController
@RequestMapping(value= "/config")
@CrossOrigin(maxAge = 3600)
public class ConfigurationController {
	
	@Autowired
	ConfigurationService configService;
	
	@PostMapping(value = "/updateConfig", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateConfig(@RequestBody ConfigModel config		){
		configService.updateAll(config);
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.ConfigUpdated));
	}
	
	@PostMapping(value = "/setDataDefinitionURL", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> setDataDefinitionURL(@RequestBody String dataDefinitionURL) {
		
		configService.setDataDefinitionURL(dataDefinitionURL);
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.ConfigUpdated));
	}
	
	@GetMapping(value = "/updateRunner", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateRunner() {

		try {
			configService.updateRunner();
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, "Updated"));
	}
	
	

}
