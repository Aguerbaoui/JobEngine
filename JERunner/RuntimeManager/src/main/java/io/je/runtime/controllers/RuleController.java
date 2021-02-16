package io.je.runtime.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.je.runtime.models.ClassModel;
import io.je.runtime.models.RuleModel;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/*
 * Rule Controller Class
 */

@RestController
@RequestMapping(value= "/rule")
@CrossOrigin(maxAge = 3600)
public class RuleController {

	




    @Autowired
    RuntimeDispatcher runtimeDispatcher = new RuntimeDispatcher();

    /*
     * add a new Rule
     */
    @PostMapping(value = "/addRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRule(@RequestBody RuleModel ruleModel) {

        try {
        	JELogger.info(getClass(),"adding rule : " + ruleModel.getRuleName());
            runtimeDispatcher.addRule(ruleModel);
        	JELogger.info(getClass(),"adding rule topics : " + ruleModel.getRuleName());

            runtimeDispatcher.addTopics(ruleModel.getProjectId(), ruleModel.getTopics());
        	JELogger.info(getClass(),"rule added successfully");


        } catch (RuleAlreadyExistsException | JEFileNotFoundException | RuleFormatNotValidException | RuleNotAddedException e) {
            e.printStackTrace();
            JELogger.error(RuleController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

        } catch (RuleCompilationException e) {

            JELogger.error(RuleController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getCompilationError()));

        }


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleAdditionSucceeded));
    }

    /*
     * update a  Rule
     */
    @PostMapping(value = "/updateRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRule(@RequestBody RuleModel ruleModel) {

        try {
            runtimeDispatcher.updateRule(ruleModel);
            runtimeDispatcher.addTopics(ruleModel.getProjectId(), ruleModel.getTopics());

        } catch (JEFileNotFoundException | RuleFormatNotValidException e) {
            e.printStackTrace();
            JELogger.error(RuleController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

        } catch (RuleCompilationException e) {

            JELogger.error(RuleController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getCompilationError()));

        }


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleUpdateSucceeded));
    }


    /*
     * compile  a  Rule
     */
    @PostMapping(value = "/compileRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> compileRule(@RequestBody RuleModel ruleModel) {

        try {
            runtimeDispatcher.compileRule(ruleModel);
        } catch (JEFileNotFoundException | RuleFormatNotValidException e) {
            e.printStackTrace();
            JELogger.error(RuleController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

        } catch (RuleCompilationException e) {

            JELogger.error(RuleController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getCompilationError()));

        }


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleUpdateSucceeded));
    }
    
    

    /*
     * compile  a  Rule
     */
    @GetMapping(value = "/deleteRule/{projectId}/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteRule(@PathVariable("projectId") String projectId,
			@PathVariable("ruleId") String ruleId) {


			try {
				runtimeDispatcher.deleteRule(projectId, ruleId);
			} catch (DeleteRuleException e) {
				e.printStackTrace();
				 JELogger.error(RuleController.class, e.getMessage());
		            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

			}
	
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleDeletionSucceeded));
	}



}
