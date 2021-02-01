package io.je.project.controllers;

import java.io.IOException;
import java.util.Collection;

import io.je.project.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.je.project.services.RuleService;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.rulebuilder.models.ScriptRuleModel;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.AddRuleBlockException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleBlockNotFoundException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleNotAddedException;
import io.je.utilities.exceptions.RuleNotFoundException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;

/*
 * Rule Builder Rest Controller
 */

@RestController
@RequestMapping(value= "/rule")
@CrossOrigin(maxAge = 3600)
public class RuleController {

	@Autowired
	RuleService ruleService ;

	@Autowired
	ProjectService projectService;
	
	/*
	 * Retrieve all rules in a project
	 */
	@GetMapping(value="{projectId}/getAllRules")
	@ResponseBody
	public ResponseEntity<?> getAllRule(@PathVariable("projectId") String projectId) {
		Collection<?> rules = null;
		try {
			 rules = ruleService.getAllRules(projectId);
			 if(rules.isEmpty())
			 {
					return ResponseEntity.noContent().build();

			 }
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		}
		
		return	new ResponseEntity<Object>(rules,HttpStatus.OK);
	
}
	/*
	 * temporary function until autosave is implemented
	 */
	@PostMapping(value = "/{projectId}/saveRuleFrontConfig/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveRuleFrontConfig(@PathVariable("projectId") String projectId,@PathVariable("ruleId") String ruleId, @RequestBody String config) {
		
			try {
				ruleService.saveRuleFrontConfig(projectId,ruleId,config);
				projectService.saveProject(ProjectService.getProjectById(projectId));
				JELogger.info(getClass(), ResponseMessages.RuleAdditionSucceeded);

			} catch (ProjectNotFoundException | RuleNotFoundException  e) {
				//e.printStackTrace();
				JELogger.error(RuleController.class, e.getMessage());
				return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
			}
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleAdditionSucceeded));
	}

	
	/*
	 * Retrieve a rule in a project, by its id
	 */
	@GetMapping(value="/{projectId}/getRule/{ruleId}")
	@ResponseBody
	public ResponseEntity<?> getRule(@PathVariable("projectId") String projectId,@PathVariable("ruleId") String ruleId) {
		JERule rule = null;
		
			 try {
				rule = ruleService.getRule(projectId,ruleId);
			} catch (ProjectNotFoundException | RuleNotFoundException e) {
				//e.printStackTrace();
				JELogger.error(RuleController.class, e.getMessage()+" [id : "+ruleId+"]");
				return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
			}
		
		
		return	new ResponseEntity<>(rule,HttpStatus.OK);
	
}
	
	

	/*
	 * add a new Rule
	 */
	@PostMapping(value = "/{projectId}/addRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addRule(@PathVariable("projectId") String projectId, @RequestBody RuleModel ruleModel) {
		
			try {
				JELogger.info(getClass(), " Adding rule " + ruleModel.getRuleName() +"..");
				ruleService.addRule(projectId,ruleModel);
				projectService.saveProject(ProjectService.getProjectById(projectId));
				JELogger.info(getClass(), ResponseMessages.RuleAdditionSucceeded);

			} catch (ProjectNotFoundException | RuleNotAddedException | RuleAlreadyExistsException e) {
				//e.printStackTrace();
				JELogger.error(RuleController.class, e.getMessage());
				return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
			}
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleAdditionSucceeded));
	}


	/*
	 * add a new scripted Rule
	 */
	@PostMapping(value = "/{projectId}/addScriptedRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addScriptedRule(@PathVariable("projectId") String projectId,@RequestBody ScriptRuleModel ruleModel) {
		
				try {
					ruleService.addScriptedRule(projectId,ruleModel);
					projectService.saveProject(ProjectService.getProjectById(projectId));

				} catch (ProjectNotFoundException | RuleAlreadyExistsException e) {
					e.printStackTrace();
					JELogger.error(RuleController.class, e.getMessage());
					return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
				}
				projectService.saveProject(ProjectService.getProjectById(projectId));
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleAdditionSucceeded));
	}
	
	/*
	 * add a new scripted Rule
	 */
	@PostMapping(value = "/{projectId}/updateScriptedRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateScriptedRule(@PathVariable("projectId") String projectId,@RequestBody ScriptRuleModel ruleModel) {
		
				try {
					ruleService.updateScriptedRule(projectId,ruleModel);
					projectService.saveProject(ProjectService.getProjectById(projectId));

				} catch (ProjectNotFoundException | RuleNotFoundException e) {
					e.printStackTrace();
					JELogger.error(RuleController.class, e.getMessage());
					return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
				}
				projectService.saveProject(ProjectService.getProjectById(projectId));
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleAdditionSucceeded));
	}


	/*
	 * Delete Rule
	 */
	@DeleteMapping(value = "/{projectId}/deleteRule/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteRule(@PathVariable("projectId") String projectId,@PathVariable("ruleId") String ruleId) {
		
			
				try {
					ruleService.deleteRule(projectId,ruleId);
					projectService.saveProject(ProjectService.getProjectById(projectId));
				} catch (ProjectNotFoundException | RuleNotFoundException e) {
					e.printStackTrace();
					JELogger.error(RuleController.class, e.getMessage());
					return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
				}
			
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleDeletionSucceeded));
	}
	
	/*
	 * update rule attributes
	 */
	@PatchMapping(value = "/{projectId}/updateRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateRule(@PathVariable("projectId") String projectId,@RequestBody RuleModel ruleModel) {
		
			
				try {
					ruleService.updateRule(projectId,ruleModel);
					projectService.saveProject(ProjectService.getProjectById(projectId));
				} catch (RuleNotAddedException | ProjectNotFoundException | RuleNotFoundException e) {
					e.printStackTrace();
					JELogger.error(RuleController.class, e.getMessage());
					return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
				}
			
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleUpdateSucceeded));
	}
	
	/*
	 * update rule : add block
	 */
	@PostMapping(value = "/{projectId}/{ruleId}/addBlock", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addBlock(@PathVariable("projectId") String projectId,@PathVariable("ruleId") String ruleId,@RequestBody BlockModel blockModel) {

		try {
			blockModel.setRuleId(ruleId);
			blockModel.setProjectId(projectId);
			ruleService.addBlockToRule(blockModel);
			projectService.saveProject(ProjectService.getProjectById(projectId));
		} catch (AddRuleBlockException | ProjectNotFoundException | RuleNotFoundException | DataDefinitionUnreachableException | JERunnerErrorException | AddClassException | ClassLoadException  e) {
			e.printStackTrace();
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		} catch (IOException e)
		{
			JELogger.error(RuleController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, e.getMessage()));

		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleUpdateSucceeded));
	}
	
	/*
	 * update rule : update block
	 */
	@PutMapping(value = "/{projectId}/{ruleId}/updateBlock", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateBlock(@PathVariable("projectId") String projectId,@PathVariable("ruleId") String ruleId,@RequestBody BlockModel blockModel) {

		try {
			blockModel.setRuleId(ruleId);
			blockModel.setProjectId(projectId);
			ruleService.updateBlock(blockModel);
			projectService.saveProject(ProjectService.getProjectById(projectId));
		} catch (AddRuleBlockException | ProjectNotFoundException | RuleNotFoundException e) {
			e.printStackTrace();
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleUpdateSucceeded));
	}
	
	/*
	 * update rule : delete block
	 */
	@DeleteMapping(value = "/{projectId}/{ruleId}/deleteBlock/{blockId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteBlock(@PathVariable("projectId") String projectId,@PathVariable("ruleId") String ruleId, @PathVariable("blockId") String blockId)
	{
		try {
			ruleService.deleteBlock(projectId, ruleId, blockId);
			projectService.saveProject(ProjectService.getProjectById(projectId));
		} catch (ProjectNotFoundException | RuleNotFoundException | RuleBlockNotFoundException e) {
			e.printStackTrace();
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		}
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleDeletionSucceeded));

	}
	
	/*
	 * build rule 
	 */
	@PostMapping(value = "/{projectId}/buildRule/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buildRule(@PathVariable("projectId") String projectId,@PathVariable("ruleId") String ruleId) {

		try {
			ruleService.buildRule(projectId,ruleId);
		} catch (ProjectNotFoundException | RuleNotFoundException | RuleBuildFailedException | JERunnerErrorException  e) {
			//e.printStackTrace();
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, e.getMessage()));

		}


		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleBuiltSuccessfully));
	}

	

}
