package io.je.runtime.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.models.ConfigModel;
import io.je.utilities.network.JEResponse;

/*
 * Class Controller Class
 */

@RestController
@CrossOrigin(maxAge = 3600)
public class ConfigurationController {

	@Autowired
	RuntimeDispatcher runtimeDispatcher;

	/*
	 * add a new class
	 */
	@PostMapping(value = "/updateConfig", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateConfig(@RequestBody ConfigModel configModel) {

		JEConfiguration.updateConfig(configModel);

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.ConfigUpdated));
	}

}
