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
import io.je.utilities.exceptions.JEFileNotFoundException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleCompilationException;
import io.je.utilities.exceptions.RuleFormatNotValidException;
import io.je.utilities.exceptions.RuleNotAddedException;
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
@RequestMapping(value= "/event")
@CrossOrigin(maxAge = 3600)
public class EventController {


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
            JELogger.error(EventController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

        } catch (RuleCompilationException e) {

            JELogger.error(EventController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getCompilationError()));

        }


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleAdditionSucceeded));
    }

}
