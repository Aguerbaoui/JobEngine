package io.je.runtime.controllers;

import io.je.project.exception.JEExceptionHandler;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.models.VariableModel;
import io.je.utilities.network.JEResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/variable")
@CrossOrigin(maxAge = 3600)
public class VariableController {

    @Autowired
    RuntimeDispatcher runtimeDispatcher ;

    /*
     * add a new variable
     */
    @PostMapping(value = "/addVariable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addVariable(@RequestBody VariableModel variableModel) {

        try {
            runtimeDispatcher.addVariable(variableModel);
        }
        catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.VAR_ADDED_SUCCESSFULLY));
    }


    /*
     * delete event
     */
    @DeleteMapping(value = "/deleteVariable/{projectId}/{varId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteVariable(@PathVariable("projectId") String projectId,
                                         @PathVariable("varId") String varId) {

        try {
            runtimeDispatcher.deleteVariable(projectId, varId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.VAR_DELETED));
    }
}
