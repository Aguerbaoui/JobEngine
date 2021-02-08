package io.je.project.exception;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;

import io.je.project.controllers.RuleController;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JEException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;

public class JEExceptionHandler {
	
	public static ResponseEntity<?> handleException(Exception e) {
		if (e instanceof JEException) {
			JEException ex = (JEException) e;
			JELogger.error(RuleController.class, Arrays.toString(ex.getStackTrace()));

			return ResponseEntity.badRequest().body(new JEResponse(ex.getCode(), ex.getMessage()));
		}

		else if (e instanceof ExecutionException) {
			try {
				JEException ex = (JEException) e.getCause();
				JELogger.error(RuleController.class, Arrays.toString(ex.getStackTrace()));

				return ResponseEntity.badRequest().body(new JEResponse(ex.getCode(), ex.getMessage()));
			} catch (Exception e1) {
				return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, e.getMessage()));

			}

		} else if (e instanceof IOException) {
			JELogger.error(RuleController.class, Arrays.toString(e.getStackTrace()));

			return ResponseEntity.badRequest()
					.body(new JEResponse(ResponseCodes.NETWORK_ERROR, String.valueOf(ResponseCodes.NETWORK_ERROR)));
		}

		else {
			JELogger.error(RuleController.class, Arrays.toString(e.getStackTrace()));

			return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, e.getMessage()));
		}

	}

}
