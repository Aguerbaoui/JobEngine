package io.je.project.exception;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JEException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;

public class JEExceptionHandler {

	private JEExceptionHandler() {}
	public static ResponseEntity<?> handleException(Exception e) {
		e.printStackTrace();
		if (e instanceof JEException) {
			JEException ex = (JEException) e;
			JELogger.error(e.getMessage(), LogCategory.RUNTIME, null,
					LogSubModule.JEBUILDER, null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JEResponse(ex.getCode(), ex.getMessage()));
			//return ResponseEntity.badRequest().body(new JEResponse(ex.getCode(), ex.getMessage()));
		}

		else if (e instanceof ExecutionException) {
			try {
				JEException ex = (JEException) e.getCause();
				JELogger.error(JEMessages.UKNOWN_ERROR + Arrays.toString(ex.getStackTrace()), LogCategory.RUNTIME, null,
						LogSubModule.JEBUILDER, null);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JEResponse(ex.getCode(), ex.getMessage()));
			} catch (Exception e1) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, e.getMessage()));

			}
			
				}
		else if ( e instanceof CompletionException) {
			try {
				JEException ex = (JEException) e.getCause().getCause();
				if(ex==null)
				{
					ex = (JEException) e.getCause();
				}
				JELogger.error(JEMessages.UKNOWN_ERROR + Arrays.toString(ex.getStackTrace()), LogCategory.RUNTIME, null,
						LogSubModule.JEBUILDER, null);

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JEResponse(ex.getCode(), ex.getMessage()));
			} catch (Exception e1) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, e.getMessage()));
			}

		} else if (e instanceof IOException) {
			JELogger.error(JEMessages.UKNOWN_ERROR + Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME, null,
					LogSubModule.JEBUILDER, null);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JEResponse(ResponseCodes.NETWORK_ERROR, String.valueOf(ResponseCodes.NETWORK_ERROR)));
		}

		else {
			JELogger.error(JEMessages.UKNOWN_ERROR + Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME, null,
					LogSubModule.JEBUILDER, null);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, e.getMessage()));
		}

	}

}
