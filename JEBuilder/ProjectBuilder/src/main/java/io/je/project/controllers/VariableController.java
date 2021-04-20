package io.je.project.controllers;

import io.je.project.exception.JEExceptionHandler;
import io.je.project.services.ProjectService;
import io.je.project.services.VariableService;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.models.VariableModel;
import io.je.utilities.network.JEResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.je.utilities.constants.JEMessages.WORKFLOW_UPDATED_SUCCESS;

@RestController
@RequestMapping(value = "/variable")
@CrossOrigin(maxAge = 3600)
public class VariableController {

    @Autowired
    VariableService variableService;

    @Autowired
    ProjectService projectService;

    /*
     * add a new variable
     */
    @PostMapping(value = "/addVariable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addVariable(@RequestBody VariableModel variableModel) {

        try {
        variableService.addVariable(variableModel);
        projectService.saveProject(variableModel.getProjectId());
        } catch (Exception e) {
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
            variableService.deleteVariable(projectId, varId);
            projectService.saveProject(projectId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.VAR_DELETED));
    }

    /*
     * Delete a workflow
     */
    @PatchMapping(value = "/updateVariable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateVariable(@RequestBody VariableModel variableModel) {

        try {
            variableService.updateVariable(variableModel);
            projectService.saveProject(variableModel.getProjectId());
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_UPDATED_SUCCESS));
    }
}
