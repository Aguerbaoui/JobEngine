package io.je.runtime.controllers;


import io.je.runtime.data.DataListener;
import io.je.runtime.models.RuleModel;
import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleCompilationException;
import io.je.utilities.exceptions.RuleNotAddedException;
import io.je.utilities.logger.RuleEngineLogConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;


/*
 * Rule Controller Class
 */

@RestController
public class RuleController {

    // Handler Responsible for calling the ryle engine
    RuleEngineHandler ruleHandler = new RuleEngineHandler();


    /*
     * add rule
     */
    @RequestMapping(value = "/addRule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRule(@RequestBody RuleModel rule) {
        System.out.println(rule);
        try {
            ruleHandler.addRule(rule);
            DataListener.addTopics(rule.getTopics());
        } catch (RuleAlreadyExistsException e) {
            return new ResponseEntity<Object>(RuleEngineLogConstants.ruleExists, HttpStatus.BAD_REQUEST);
        } catch (RuleCompilationException e) {
            return new ResponseEntity<Object>(RuleEngineLogConstants.ruleCompilationError, HttpStatus.BAD_REQUEST);
        } catch (RuleNotAddedException e) {
            return new ResponseEntity<Object>(RuleEngineLogConstants.ruleCompilationError, HttpStatus.BAD_REQUEST);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<Object>("File Not Found", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Object>(RuleEngineLogConstants.sucessfullyAddedRule, HttpStatus.OK);

    }

    /*
     * update rule
     */
    @RequestMapping(value = "/updateRule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRule(@RequestBody RuleModel rule) {
        System.out.println(rule);
        try {
            ruleHandler.updateRule(rule);
        } catch (RuleCompilationException e) {
            return new ResponseEntity<Object>(RuleEngineLogConstants.ruleCompilationError, HttpStatus.BAD_REQUEST);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<Object>("File Not Found", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Object>(RuleEngineLogConstants.sucessfullyUpdatedRule, HttpStatus.OK);

    }


    /*
     * run project
     */
	/*@RequestMapping(value = "/runProject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runProject(@RequestBody String id) {		
		System.out.println(id);
		
			try {
				ruleHandler.runRuleEngineProject(id);
			} catch (RulesNotFiredException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuleEngineBuildFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProjectAlreadyRunningException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return new ResponseEntity<Object>("project sucesffully running" ,HttpStatus.OK);
		
	}*/

}
