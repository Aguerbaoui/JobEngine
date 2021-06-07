package io.je.project.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import io.je.project.exception.JEExceptionHandler;
import io.je.project.services.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.je.project.services.RuleService;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.rulebuilder.models.ScriptRuleModel;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;

/*
 * Rule Builder Rest Controller
 */

@RestController
@RequestMapping(value = "/rule")
@CrossOrigin(maxAge = 3600)
public class RuleController {

	@Autowired
	RuleService ruleService;


	
	
	/*  Retrieve Rules */
	

	/*
	 * Retrieve all rules in a project
	 */
	@GetMapping(value = "{projectId}/getAllRules")
	@ResponseBody
	public ResponseEntity<?> getAllRule(@PathVariable("projectId") String projectId) {
		Collection<RuleModel> rules = null;
		try {
			rules = ruleService.getAllRules(projectId);
			if (rules.isEmpty()) {
				return ResponseEntity.noContent().build();

			}
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		return new ResponseEntity<>(rules, HttpStatus.OK);

	}

	
	/*
	 * Retrieve a rule in a project, by its id
	 */
	@GetMapping(value = "/{projectId}/getRule/{ruleId}")
	@ResponseBody
	public ResponseEntity<?> getRule(@PathVariable("projectId") String projectId,
			@PathVariable("ruleId") String ruleId) {
		RuleModel rule = null;

		try {
			rule = ruleService.getRule(projectId, ruleId);
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return new ResponseEntity<>(rule, HttpStatus.OK);

	}
	
	/* rule management */
	
	/* user defined rules : rules created graphically */

	/*
	 * add a new Rule
	 */
	@PostMapping(value = "/{projectId}/addRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addRule(@PathVariable("projectId") String projectId, @RequestBody RuleModel ruleModel) {

		try {
			JELogger.info(getClass(), " Adding rule " + ruleModel.getRuleName() + "..");
			ruleService.addRule(projectId, ruleModel);
			

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		JELogger.info(getClass(), JEMessages.RULE_ADDED_SUCCESSFULLY);
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_ADDED_SUCCESSFULLY));
	}

	
	@PostMapping(value = "/{projectId}/deleteRules", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteRules(@PathVariable("projectId") String projectId, @RequestBody List<String> ruleIds) {

		try {
		
			ruleService.deleteRules(projectId, ruleIds);
			

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		JELogger.info(getClass(), JEMessages.RULE_ADDED_SUCCESSFULLY);
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_DELETED));
	}

	
	
	
	/*
	 * Delete Rule
	 */
	@DeleteMapping(value = "/{projectId}/deleteRule/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteRule(@PathVariable("projectId") String projectId,
			@PathVariable("ruleId") String ruleId) {

		try {
			ruleService.deleteRule(projectId, ruleId);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_DELETED));
	}

	/*
	 * update rule attributes
	 */
	@PatchMapping(value = "/{projectId}/updateRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateRule(@PathVariable("projectId") String projectId, @RequestBody RuleModel ruleModel) {

		try {
			ruleService.updateRule(projectId, ruleModel);
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_UPDATED));
	}

	/*
	 * update rule : add block
	 */
	@PostMapping(value = "/{projectId}/{ruleId}/addBlock", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addBlock(@PathVariable("projectId") String projectId,
			@PathVariable("ruleId") String ruleId, @RequestBody BlockModel blockModel) {
		String generatedBlockName = "";
		try {
			blockModel.setRuleId(ruleId);
			blockModel.setProjectId(projectId);
			 generatedBlockName = ruleService.addBlockToRule(blockModel);
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		
		HashMap<String, String> object = new HashMap<>();

		object.put("blockName", generatedBlockName);
		return ResponseEntity.ok(object);
	}
	
	@PatchMapping(value = "/{projectId}/{ruleId}/updateBlock", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateBlock(@PathVariable("projectId") String projectId,
			@PathVariable("ruleId") String ruleId, @RequestBody BlockModel blockModel) {
		try {
			blockModel.setRuleId(ruleId);
			blockModel.setProjectId(projectId);
			ruleService.updateBlockInRule(blockModel);
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_UPDATED));
	}
	

	/*
	 * update rule : delete block
	 */
	@DeleteMapping(value = "/{projectId}/{ruleId}/deleteBlock/{blockId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteBlock(@PathVariable("projectId") String projectId,
			@PathVariable("ruleId") String ruleId, @PathVariable("blockId") String blockId) {
		try {
			ruleService.deleteBlock(projectId, ruleId, blockId);
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_UPDATED));

	}

	/*
	 * build rule
	 */
	@PostMapping(value = "/{projectId}/buildRule/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buildRule(@PathVariable("projectId") String projectId,
			@PathVariable("ruleId") String ruleId) {

		try {
			ruleService.buildRule(projectId, ruleId);
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_WAS_BUILT_SUCCESSFULLY));
	}

	/*
	 * temporary function until autosave is implemented
	 */
	@PostMapping(value = "/{projectId}/saveRuleFrontConfig/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveRuleFrontConfig(@PathVariable("projectId") String projectId,
			@PathVariable("ruleId") String ruleId, @RequestBody String config) {

		try {
			ruleService.saveRuleFrontConfig(projectId, ruleId, config);
			JELogger.info(getClass(), JEMessages.RULE_ADDED_SUCCESSFULLY);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_ADDED_SUCCESSFULLY));
	}

	
/* scripted rules */
	
	
	/*
	 * add a new scripted Rule
	 */
	@PostMapping(value = "/{projectId}/addScriptedRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addScriptedRule(@PathVariable("projectId") String projectId,
			@RequestBody ScriptRuleModel ruleModel) {

		try {
			ruleService.addScriptedRule(projectId, ruleModel);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_ADDED_SUCCESSFULLY));
	}

	/*
	 * update a  scripted Rule
	 */
	@PostMapping(value = "/{projectId}/updateScriptedRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateScriptedRule(@PathVariable("projectId") String projectId,
			@RequestBody ScriptRuleModel ruleModel) {

		try {
			ruleService.updateScriptedRule(projectId, ruleModel);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.RULE_ADDED_SUCCESSFULLY));
	}

	
}
