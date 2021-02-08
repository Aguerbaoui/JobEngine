package io.je.project.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.rulebuilder.components.BlockGenerator;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.RuleParameters;
import io.je.rulebuilder.components.ScriptedRule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.rulebuilder.models.ScriptRuleModel;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.RuleBuilderErrors;
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
import io.je.utilities.runtimeobject.ClassDefinition;

/*
 * Service class to handle business logic for rules
 */
@Service
public class RuleService {

	private static final String DEFAULT_CONSTANT = "DEFAULT";

	@Autowired
	ClassService classService;

	

	/*
	 * Add a rule to a project
	 */
	public void addRule(String projectId, RuleModel ruleModel)
			throws ProjectNotFoundException, RuleAlreadyExistsException, RuleNotAddedException, InterruptedException,
			ExecutionException {
		JELogger.info(getClass(), "adding rule");
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		} else if (project.ruleExists(ruleModel.getRuleId())) {
			throw new RuleAlreadyExistsException(RuleBuilderErrors.RuleAlreadyExists);
		}

		// TODO : remove harcoded msgs
		if (ruleModel.getRuleId() == null) {
			throw new RuleNotAddedException("Rule id can't be empty");
		}
		if (ruleModel.getRuleName() == null) {
			throw new RuleNotAddedException("Rule name can't be empty");
		}
		UserDefinedRule rule = new UserDefinedRule();
		rule.setJobEngineElementID(ruleModel.getRuleId());
		rule.setJobEngineProjectID(projectId);
		rule.setRuleName(ruleModel.getRuleName());
		rule.setDescription(ruleModel.getDescription());
		RuleParameters ruleParameters = new RuleParameters();
		ruleParameters.setSalience(String.valueOf(ruleModel.getSalience()));
		ruleParameters.setTimer(ruleModel.getTimer());
		ruleParameters.setEnabled(ruleModel.getEnabled());
		ruleParameters.setDateEffective(ruleModel.getDateEffective());
		ruleParameters.setDateExpires(ruleModel.getDateExpires());
		rule.setRuleParameters(ruleParameters);
		project.addRule(rule);
	}

	/*
	 * delete rule from a project
	 */
	
	public void deleteRule(String projectId, String ruleId)
			throws ProjectNotFoundException, RuleNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		} else if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
		}
		project.deleteRule(ruleId);
		

	}

	/*
	 * update rule : update rule attributes TODO: individual update function for
	 * each attribute
	 */
	@Async
	public void updateRule(String projectId, RuleModel ruleModel) throws RuleNotAddedException,
			ProjectNotFoundException, RuleNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		} else if (!project.ruleExists(ruleModel.getRuleId())) {
			throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
		}
		UserDefinedRule ruleToUpdate = (UserDefinedRule) project.getRules().get(ruleModel.getRuleId());

		// update rule name
		if (ruleModel.getRuleName() != null && !ruleModel.getRuleName().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.setRuleName(ruleModel.getRuleName());
		} else if (ruleModel.getRuleName().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.setRuleName(null);

		}

		// update rule description
		if (ruleModel.getDescription() != null && !ruleModel.getDescription().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.setDescription(ruleModel.getDescription());
		} else if (ruleModel.getDescription().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.setDescription(null);

		}

		// update Salience
		if (ruleModel.getSalience() != null && !ruleModel.getSalience().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setSalience(ruleModel.getSalience());
		} else if (ruleModel.getSalience().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setSalience(null);

		}

		// update DateEffective
		if (ruleModel.getDateEffective() != null && !ruleModel.getDateEffective().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setDateEffective(ruleModel.getDateEffective());
		} else if (ruleModel.getDateEffective().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setDateEffective(null);

		}

		// update DateExpires
		if (ruleModel.getDateExpires() != null && !ruleModel.getDateExpires().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setDateExpires(ruleModel.getDateExpires());
		} else if (ruleModel.getDateExpires().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setDateExpires(null);

		}

		// update Enabled
		if (ruleModel.getEnabled() != null && !ruleModel.getEnabled().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setEnabled(ruleModel.getEnabled());
		} else if (ruleModel.getEnabled().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setEnabled(null);

		}

		// update Timer
		if (ruleModel.getTimer() != null && !ruleModel.getTimer().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setTimer(ruleModel.getTimer());
		} else if (ruleModel.getTimer().equals(DEFAULT_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setTimer(null);

		}
		ruleToUpdate.setBuilt(false);
		project.getRules().put(ruleModel.getRuleId(), ruleToUpdate);
		


	}

	/*
	 * update rule : add block to rule
	 */
	public void addBlockToRule(BlockModel blockModel) throws AddRuleBlockException,
			ProjectNotFoundException, RuleNotFoundException, DataDefinitionUnreachableException, JERunnerErrorException,
			AddClassException, ClassLoadException, IOException, InterruptedException, ExecutionException {

		if (blockModel.getProjectId() == null) {
			throw new AddRuleBlockException(RuleBuilderErrors.BlockProjectIdentifierIsEmpty);
		}

		if (blockModel.getRuleId() == null) {
			throw new AddRuleBlockException(RuleBuilderErrors.BlockRuleIdentifierIsEmpty);
		}

		JEProject project = ProjectService.getProjectById(blockModel.getProjectId());
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		} else if (!project.ruleExists(blockModel.getRuleId())) {
			JELogger.error(getClass(), RuleBuilderErrors.RuleNotFound + " [ " + blockModel.getRuleId() + "]");
			throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
		}
		verifyBlockFormatIsValid(blockModel);
		JERule rule = project.getRule(blockModel.getRuleId());
		Block block = BlockGenerator.createBlock(blockModel);
		block.setInputBlockIds(blockModel.getInputBlocksIds());
		block.setOutputBlockIds(blockModel.getOutputBlocksIds());

		// retrieve topic names from getter blocks
		if (blockModel.getOperationId() == 4002 && blockModel.getBlockConfiguration() != null
				& blockModel.getBlockConfiguration().getClassId() != null) {
			ClassDefinition classDef = new ClassDefinition(blockModel.getBlockConfiguration().getWorkspaceId(),
					blockModel.getBlockConfiguration().getClassId());
			rule.addTopic(classDef.getClassId());
			classService.addClass(classDef);
		}
		((UserDefinedRule) rule).addBlock(block);
		

	}

	/*
	 * delete block
	 */
	@Async
	public void deleteBlock(String projectId, String ruleId, String blockId)
			throws ProjectNotFoundException, RuleNotFoundException, RuleBlockNotFoundException, InterruptedException,
			ExecutionException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		} else if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
		}
		project.deleteRuleBlock(ruleId, blockId);
		


	}

	/*
	 * build rule : create drl + check for compilation errors
	 */
	@Async
	public CompletableFuture<Void> buildRule(String projectId, String ruleId)
			throws ProjectNotFoundException, RuleNotFoundException, RuleBuildFailedException, JERunnerErrorException,
			IOException, InterruptedException, ExecutionException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		} else if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
		}
		RuleBuilder.buildRule(project.getRule(ruleId), project.getConfigurationPath());
		project.getRules().get(ruleId).setBuilt(true);
		project.getRules().get(ruleId).setAdded(true);
		return CompletableFuture.completedFuture(null);

	}

	@Async
	public CompletableFuture<Void> buildRules(String projectId) throws ProjectNotFoundException,
			RuleBuildFailedException, JERunnerErrorException, IOException, RuleNotFoundException, InterruptedException, ExecutionException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		}
		ArrayList<CompletableFuture<?>> ruleFuture = new ArrayList<>();
		for (Entry<String, JERule> entry : project.getRules().entrySet()) {
			String ruleId = entry.getKey();
			ruleFuture.add(buildRule(projectId, ruleId));
		}
		ruleFuture.forEach(CompletableFuture::join);
		return CompletableFuture.completedFuture(null);
	}

	/*
	 * Retrieve list of all rules that exist in a project.
	 */
	@Async
	public Collection<JERule> getAllRules(String projectId)
			throws ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		}
		return project.getRules().values();
	}

	public JERule getRule(String projectId, String ruleId)
			throws ProjectNotFoundException, RuleNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);

		} else if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
		}
		return project.getRules().get(ruleId);
	}

	/*
	 * add scripted rule
	 */
	@Async
	public void addScriptedRule(String projectId, ScriptRuleModel ruleModel)
			throws ProjectNotFoundException, RuleAlreadyExistsException {
		ScriptedRule rule = new ScriptedRule(projectId, ruleModel.getRuleId(), ruleModel.getScript(),
				ruleModel.getRuleName());
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		}
		project.addRule(rule);
		

	}

	/*
	 * update scripted rule
	 * 
	 */
	@Async
	public void updateScriptedRule(String projectId, ScriptRuleModel ruleModel)
			throws ProjectNotFoundException, RuleNotFoundException {
		ScriptedRule rule = new ScriptedRule(projectId, ruleModel.getRuleId(), ruleModel.getScript(),
				ruleModel.getRuleName());
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		}
		project.updateRule(rule);
		

		

	}

	@Async
	public void saveRuleFrontConfig(String projectId, String ruleId, String config)
			throws ProjectNotFoundException, RuleNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
		} else if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
		}
		project.getRule(ruleId).setRuleFrontConfig(config);
		

	}

	public void verifyBlockFormatIsValid(BlockModel blockModel) throws AddRuleBlockException {
		// block Id can't be null
		if (blockModel == null || blockModel.getBlockId() == null || blockModel.getBlockId().isEmpty()) {
			throw new AddRuleBlockException(RuleBuilderErrors.BlockIdentifierIsEmpty);

		}

		if (blockModel.getBlockName() == null || blockModel.getBlockName().isEmpty()) {
			throw new AddRuleBlockException(RuleBuilderErrors.BlockNameIsEmpty);

		}
		// block operation id can't be empty
		if (blockModel.getOperationId() == 0) {
			throw new AddRuleBlockException(RuleBuilderErrors.BlockOperationIdUnknown);
		}

	}
}
