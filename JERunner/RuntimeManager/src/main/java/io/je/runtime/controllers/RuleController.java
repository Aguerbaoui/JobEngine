package io.je.runtime.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.je.project.exception.JEExceptionHandler;
import io.je.runtime.models.RuleModel;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;
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
    RuntimeDispatcher runtimeDispatcher;

    /*
     * add a new Rule
     */
    @PostMapping(value = "/addRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRule(@RequestBody RuleModel ruleModel) {

        try {
            runtimeDispatcher.addRule(ruleModel);
            runtimeDispatcher.addTopics(ruleModel.getProjectId(), ruleModel.getTopics());
        	JELogger.trace(getClass(),"rule added successfully");

        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_ADDED_SUCCESSFULLY));
    }

    /*
     * update a  Rule
     */
    @PostMapping(value = "/updateRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRule(@RequestBody RuleModel ruleModel) {

        try {
            JELogger.debug(getClass(),"updating rule : " + ruleModel.getRuleName());
            runtimeDispatcher.updateRule(ruleModel);
            JELogger.debug(getClass(),"adding topics : " +  ruleModel.getTopics());

            runtimeDispatcher.addTopics(ruleModel.getProjectId(), ruleModel.getTopics());

        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_UPDATED_SUCCESSFULLY));
    }


    /*
     * compile  a  Rule
     */
    @PostMapping(value = "/compileRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> compileRule(@RequestBody RuleModel ruleModel) {

        try {
            runtimeDispatcher.compileRule(ruleModel);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_UPDATED_SUCCESSFULLY));
    }
    
    

    /*
     * compile  a  Rule
     */
    @GetMapping(value = "/deleteRule/{projectId}/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteRule(@PathVariable("projectId") String projectId,
			@PathVariable("ruleId") String ruleId) {


			try {
                runtimeDispatcher.removeRuleTopics(projectId,ruleId);
				runtimeDispatcher.deleteRule(projectId, ruleId);
			} catch (Exception e) {
				return JEExceptionHandler.handleException(e);
			}
	
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_DELETED_SUCCESSFULLY));
	}



}
