package io.je.project.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.je.project.models.JEBuilderConfigModel;
import io.je.project.services.ConfigurationService;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.JEGlobalconfig;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.network.JEResponse;

@RestController
@RequestMapping(value= "/config")
@CrossOrigin(maxAge = 3600)
public class ConfigurationController {
	
	ConfigurationService configService = new ConfigurationService();
	
	//TODO: separate config
	@PostMapping(value = "/updateConfiguration", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateConfig( @RequestBody JEBuilderConfigModel configModel) {
		
			//TODO: add calls
			configService.updateConfig(configModel);
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.ConfigUpdated));
	}
	
	
	@GetMapping(value = "/getConfiguration", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getConfig( ) {
		
			//TODO: add to service
			Map<String,String> config = new HashMap<>();
			config.put("class definition api : " , JEGlobalconfig.CLASS_DEFINITION_API);
			config.put("JErunner api : " , JEGlobalconfig.RUNTIME_MANAGER_BASE_API);
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, config.toString()));
	}
	
	

}
