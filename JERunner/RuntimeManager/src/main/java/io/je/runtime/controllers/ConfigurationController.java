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
import io.je.utilities.constants.JEMessages;
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

		//  System.setProperty("drools.compiler", "JANINO");
		//  System.setProperty("drools.dialect.java.compiler", "JANINO");
		JEConfiguration.updateConfig(configModel);
		if(JEConfiguration.getDroolsDateFormat()!=null)
		{
			
	        System.setProperty("drools.dateformat", JEConfiguration.getDroolsDateFormat());

		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.CONFIGURATION_UPDATED));
	}

}
