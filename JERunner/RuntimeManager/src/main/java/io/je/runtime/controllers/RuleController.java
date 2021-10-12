package io.je.runtime.controllers;


import static io.je.utilities.constants.JEMessages.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.je.project.exception.JEExceptionHandler;
import io.je.runtime.models.RuleModel;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.beans.JEResponse;


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
            runtimeDispatcher.addTopics(ruleModel.getProjectId(), ruleModel.getRuleId(),"rule",ruleModel.getTopics());
            runtimeDispatcher.addRule(ruleModel);

        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, RULE_ADDED_SUCCESSFULLY));
    }

    /*
     * update a  Rule
     */
    @PostMapping(value = "/updateRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRule(@RequestBody RuleModel ruleModel) {

        try {
            runtimeDispatcher.removeRuleTopics(ruleModel.getProjectId(), ruleModel.getRuleId());
            runtimeDispatcher.addTopics(ruleModel.getProjectId(), ruleModel.getRuleId(),"rule",ruleModel.getTopics());
            runtimeDispatcher.updateRule(ruleModel);

        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_UPDATED));
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


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_UPDATED));
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
	
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_DELETED));
	}

    /*
     * Runs only rules
     * */
    @GetMapping(value = "/runAllRules/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runProjectRules(@PathVariable String projectId) {
        try {
        	runtimeDispatcher.runProjectRules(projectId);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.EXECUTING_PROJECT));

    }

    /*
     * Runs only rules
     * */
    @GetMapping(value = "/shutDownRuleEngine/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopRuleEngine(@PathVariable String projectId) {
        try {
        	runtimeDispatcher.shutDownRuleEngine(projectId);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.EXECUTING_PROJECT));

    }

}
