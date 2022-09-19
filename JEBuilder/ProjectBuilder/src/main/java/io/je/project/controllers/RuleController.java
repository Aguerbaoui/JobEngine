package io.je.project.controllers;

import io.je.project.exception.JEExceptionHandler;
import io.je.project.services.ProjectService;
import io.je.project.services.RuleService;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.rulebuilder.models.ScriptRuleModel;
import io.je.utilities.beans.JECustomResponse;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JEException;
import io.je.utilities.log.JELogger;
import io.je.utilities.ruleutils.OperationStatusDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.je.utilities.constants.WorkflowConstants.TWILIO_ACCOUNT_SID;
import static io.je.utilities.constants.WorkflowConstants.TWILIO_ACCOUNT_TOKEN;

/*
 * Rule Builder Rest Controller
 */

@RestController
@RequestMapping(value = "/rule")
@CrossOrigin(maxAge = 3600)
public class RuleController {

    @Autowired
    RuleService ruleService;

    @Autowired
    ProjectService projectService;


    /*  Retrieve rules */


    /*
     * Get all project rules
     */
    @GetMapping(value = "{projectId}/getAllRules")
    @ResponseBody
    public ResponseEntity<?> getAllRules(@PathVariable("projectId") String projectId) {
        Collection<RuleModel> rules = null;
        try {
            projectService.getProject(projectId);

            rules = ruleService.getAllRules(projectId);
			/*if (rules.isEmpty()) {
				return ResponseEntity.noContent().build();

			}*/
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return new ResponseEntity<>(rules, HttpStatus.OK);

    }


    /*
     * Get a project rule
     */
    @GetMapping(value = "/{projectId}/getRule/{ruleId}")
    @ResponseBody
    public ResponseEntity<?> getRule(@PathVariable("projectId") String projectId,
                                     @PathVariable("ruleId") String ruleId) {
        RuleModel rule = null;

        try {
            projectService.getProject(projectId);

            rule = ruleService.getRule(projectId, ruleId);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return new ResponseEntity<>(rule, HttpStatus.OK);

    }

    /* Rule management */

    /* User defined rules : rules created graphically */

    /*
     * Add a new rule
     */
    @PostMapping(value = "/{projectId}/addRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRule(@PathVariable("projectId") String projectId, @RequestBody RuleModel ruleModel) {

        try {
            projectService.getProject(projectId);

            ruleService.createRule(projectId, ruleModel);


        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_ADDED_SUCCESSFULLY));
    }


    /*
     * Delete a list of rules
     */
    @PostMapping(value = "/{projectId}/deleteRules", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteRules(@PathVariable("projectId") String projectId, @RequestBody List<String> ruleIds) {

        try {
            projectService.getProject(projectId);

            ruleService.deleteRules(projectId, ruleIds);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_DELETED));
    }


    /*
     * Delete a rule
     */
    @DeleteMapping(value = "/{projectId}/deleteRule/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteRule(@PathVariable("projectId") String projectId,
                                        @PathVariable("ruleId") String ruleId) {

        try {
            projectService.getProject(projectId);

            ruleService.deleteRule(projectId, ruleId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_DELETED));
    }

    /*
     * Update a rule
     */
    @PatchMapping(value = "/{projectId}/updateRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRule(@PathVariable("projectId") String projectId, @RequestBody RuleModel ruleModel) {

        try {
            projectService.getProject(projectId);

            ruleService.updateRule(projectId, ruleModel);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_UPDATED));
    }

    /*
     * Add block to a rule
     */
    @PostMapping(value = "/{projectId}/{ruleId}/addBlock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addBlock(@PathVariable("projectId") String projectId,
                                      @PathVariable("ruleId") String ruleId, @RequestBody BlockModel blockModel) {
        String generatedBlockName = "";
        try {

            blockModel.setRuleId(ruleId);
            blockModel.setProjectId(projectId);
            projectService.getProject(projectId);

            generatedBlockName = ruleService.addBlockToRule(blockModel);
            projectService.saveProject(projectId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        HashMap<String, String> object = new HashMap<>();

        object.put("blockName", generatedBlockName);
        return ResponseEntity.ok(object);
    }


    /*
     * Update a rule block
     */
    @PatchMapping(value = "/{projectId}/{ruleId}/updateBlock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBlock(@PathVariable("projectId") String projectId,
                                         @PathVariable("ruleId") String ruleId, @RequestBody BlockModel blockModel) {
        try {
            JELogger.debug("Updating block");
            projectService.getProject(projectId);

            blockModel.setRuleId(ruleId);
            blockModel.setProjectId(projectId);

            ruleService.updateBlockInRule(blockModel);

            projectService.saveProject(projectId);
        } catch (Exception e) {
            JELogger.error("Exception occurred while updating Block.");
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_UPDATED));
    }


    /*
     * Delete a rule block
     */
    @DeleteMapping(value = "/{projectId}/{ruleId}/deleteBlock/{blockId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteBlock(@PathVariable("projectId") String projectId,
                                         @PathVariable("ruleId") String ruleId, @PathVariable("blockId") String blockId) {
        try {
            projectService.getProject(projectId);

            ruleService.deleteBlock(projectId, ruleId, blockId);
            projectService.saveProject(projectId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_UPDATED));

    }

    /*
     * Build a rule
     */
    @PostMapping(value = "/{projectId}/buildRule/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildRule(@PathVariable("projectId") String projectId,
                                       @PathVariable("ruleId") String ruleId) {

        try {
            projectService.getProject(projectId);

            ruleService.compileRule(projectId, ruleId);

            projectService.saveProject(projectId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_WAS_BUILT_SUCCESSFULLY));
    }

    /*
     * Build a list of rules
     */
    @PostMapping(value = "/{projectId}/buildRules", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildRules(@PathVariable("projectId") String projectId,
                                        @RequestBody List<String> ruleIds) {

        List<OperationStatusDetails> results;
        try {
            projectService.getProject(projectId);

            results = ruleService.compileRules(projectId, ruleIds)
                    .get();

            projectService.saveProject(projectId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JECustomResponse(ResponseCodes.CODE_OK, "Build completed.", results));
    }


    /*
     * Run rule
     */
    @PostMapping(value = "/{projectId}/runRule/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runRule(@PathVariable("projectId") String projectId,
                                     @PathVariable("ruleId") String ruleId) {

        try {
            projectService.getProject(projectId);

            ruleService.runRule(projectId, ruleId);

            projectService.saveProject(projectId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, "Rule is running."));
    }


    /*
     * Run a list of rules
     */
    @PostMapping(value = "/{projectId}/runRules", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runRules(@PathVariable("projectId") String projectId,
                                      @RequestBody List<String> ruleIds) {
        List<OperationStatusDetails> results;
        try {
            projectService.getProject(projectId);

            results = ruleService.runRules(projectId, ruleIds);

            projectService.saveProject(projectId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JECustomResponse(ResponseCodes.CODE_OK, "Run completed.", results));
    }


    /*
     * Stop a rule
     */
    @PostMapping(value = "/{projectId}/stopRule/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopRule(@PathVariable("projectId") String projectId,
                                      @PathVariable("ruleId") String ruleId) {

        try {
            projectService.getProject(projectId);
            ruleService.stopRule(projectId, ruleId);
            projectService.saveProject(projectId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_COMPILED));
    }


    /*
     * Stop a list of rules
     */
    @PostMapping(value = "/{projectId}/stopRules", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopRules(@PathVariable("projectId") String projectId,
                                       @RequestBody List<String> ruleIds) {
        List<OperationStatusDetails> results;

        try {
            projectService.getProject(projectId);

            results = ruleService.stopRules(projectId, ruleIds);

            projectService.saveProject(projectId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JECustomResponse(ResponseCodes.CODE_OK, JEMessages.RULE_STOPPED, results));
    }


    /*
     * Update rule editor config : Temporary function until auto-save is implemented
     */
    @PostMapping(value = "/{projectId}/saveRuleFrontConfig/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveRuleFrontConfig(@PathVariable("projectId") String projectId,
                                                 @PathVariable("ruleId") String ruleId, @RequestBody String config) {

        try {
            projectService.getProject(projectId);

            ruleService.saveRuleFrontConfig(projectId, ruleId, config);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_ADDED_SUCCESSFULLY));
    }


    /* Scripted rules */

    /*
     * Add a scripted rule
     */
    @PostMapping(value = "/{projectId}/addScriptedRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addScriptedRule(@PathVariable("projectId") String projectId,
                                             @RequestBody ScriptRuleModel ruleModel) {

        try {
            projectService.getProject(projectId);

            ruleService.addScriptedRule(projectId, ruleModel);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_ADDED_SUCCESSFULLY));
    }

    /*
     * Update a scripted rule
     */
    @PostMapping(value = "/{projectId}/updateScriptedRule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateScriptedRule(@PathVariable("projectId") String projectId,
                                                @RequestBody ScriptRuleModel ruleModel) {

        try {
            projectService.getProject(projectId);

            ruleService.updateScriptedRule(projectId, ruleModel);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_ADDED_SUCCESSFULLY));
    }

    // TODO Move to TwilioController
    /*
     * Get Twilio verified users for specific Id (Twilio account SID)
     */
    @PostMapping(value = "/getTwilioUsers")
    @ResponseBody
    public ResponseEntity<?> getTwilioVerifiedUsers(@RequestBody Map<String, String> twilio) {
        List<HashMap<String, String>> users = null;
        try {
            users = ruleService.getTwilioVerifiedUsers(twilio.get(TWILIO_ACCOUNT_SID), twilio.get(TWILIO_ACCOUNT_TOKEN));
        } catch (Exception e) {
            return JEExceptionHandler.handleException(new TwilioUsersError("Could not fetch verified users for this Twilio account"));
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // FIXME Move to Exceptions
    public class TwilioUsersError extends JEException {

        public TwilioUsersError(String message) {
            super(8888, message);
        }
    }

    @PostMapping(value = "/getSMSEagleContact")
    @ResponseBody
    public ResponseEntity<?> getSMSEagleContact(@RequestBody Map<String, String> smsEagle) {
        try {
            return new ResponseEntity<>(this.ruleService.getSMSEagleContacts(smsEagle), HttpStatus.OK);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(new TwilioUsersError("Could not fetch verified users for this SMSEagle account"));
        }
        //return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping(value = "/getSMSEagleGroup")
    @ResponseBody
    public ResponseEntity<?> getSMSEagleGroup(@RequestBody Map<String, String> smsEagle) {
        try {
            return new ResponseEntity<>(this.ruleService.getSMSEagleGroups(smsEagle), HttpStatus.OK);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(new TwilioUsersError("Could not fetch verified users for this SMSEagle account"));
        }
        //return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
