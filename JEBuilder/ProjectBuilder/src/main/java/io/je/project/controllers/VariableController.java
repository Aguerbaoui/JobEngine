package io.je.project.controllers;

import io.je.project.exception.JEExceptionHandler;
import io.je.project.services.ProjectService;
import io.je.project.services.VariableService;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.models.VariableModel;
import io.je.utilities.beans.JEResponse;

import static io.je.utilities.constants.JEMessages.WORKFLOW_UPDATED_SUCCESS;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/variable")
@CrossOrigin(maxAge = 3600)
public class VariableController {

    @Autowired
    VariableService variableService;
    @Autowired
	ProjectService projectService;

    
	/*
	 * Retrieve all variables in a project
	 */
	@GetMapping(value = "{projectId}/getAllVariables")
	@ResponseBody
	public ResponseEntity<?> getAllVariables(@PathVariable("projectId") String projectId) {
		Collection<?> variables = null;
		try {
			projectService.getProject(projectId);

			variables = variableService.getAllVariables(projectId);
			/*if (variables.isEmpty()) {
				return ResponseEntity.noContent().build();

			}*/
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(variables);

	}

	/*
	 * Retrieve an variable from a project
	 */
	@GetMapping(value = "{projectId}/getVariable/{variableId}")
	@ResponseBody
	public ResponseEntity<?> getVariable(@PathVariable("projectId") String projectId,
			@PathVariable("variableId") String variableId) {
		JEVariable variable = null;


		try {
			projectService.getProject(projectId);

			variable = variableService.getVariable(projectId, variableId);
			if (variable == null) {
				return ResponseEntity.noContent().build();

			}
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(new VariableModel(variable));

	}

    /*
     * add a new variable
     */
    @PostMapping(value = "/addVariable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addVariable(@RequestBody VariableModel variableModel) {

        try {
			projectService.getProject(variableModel.getProjectId());

        variableService.addVariable(variableModel);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.VAR_ADDED_SUCCESSFULLY));
    }

    /*
     * Validate variable type
     */
    @PostMapping(value = "/validateType", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateType(@RequestBody Map model) {

        try {

            return ResponseEntity.ok(variableService.validateType((HashMap<String, String>) model));
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        //return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.VAR_ADDED_SUCCESSFULLY));
    }


    /*
     * delete variable 
     */
    @DeleteMapping(value = "/deleteVariable/{projectId}/{varId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteVariable(@PathVariable("projectId") String projectId,
                                            @PathVariable("varId") String varId) {

        try {
			projectService.getProject(projectId);

            variableService.deleteVariable(projectId, varId);

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
			projectService.getProject(variableModel.getProjectId());

            variableService.updateVariable(variableModel);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_UPDATED_SUCCESS));
    }

    /*
    * write to variable
    */
   @PostMapping(value = "{projectId}/writeVariableValue/{variableId}", produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<?> writeVariableValue(@PathVariable("projectId") String projectId,@PathVariable("variableId") String variableId, @RequestBody String value ) {

       try {
			projectService.getProject(projectId);

       variableService.writeVariableValue(projectId,variableId, value);
       } catch (Exception e) {
           return JEExceptionHandler.handleException(e);
       }
       return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.VAR_ADDED_SUCCESSFULLY));
   }

    /*
     * delete multiple variables
     */
    @PostMapping(value = "/deleteVariables/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteVariables(@PathVariable("projectId") String projectId, @RequestBody(required = false) List<String> ids) {

        try {
            projectService.getProject(projectId);
            variableService.deleteVariables(projectId, ids);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.VARS_DELETED));
    }

}
