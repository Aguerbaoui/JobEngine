package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.config.LicenseProperties;
import io.je.project.repository.RuleRepository;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.rulebuilder.components.*;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.rulebuilder.models.ScriptRuleModel;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.ruleutils.RuleIdManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/*
 * Service class to handle business logic for rules
 */
@Service
public class RuleService {

	private static final String DEFAULT_DELETE_CONSTANT = "DELETED";

	@Autowired
	RuleRepository ruleRepository;

	@Autowired
	ClassService classService;

	@Autowired
	AsyncRuleService asyncRuleService;

	private static final LogSubModule RULE = LogSubModule.RULE;
	private static final LogCategory CATEGORY = LogCategory.DESIGN_MODE;

	/*
	 * Add a rule to a project
	 */
	public void createRule(String projectId, RuleModel ruleModel) throws ProjectNotFoundException,
			RuleAlreadyExistsException, RuleNotAddedException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
		}

		// TODO : remove harcoded msgs
		if (ruleModel.getRuleId() == null) {
			throw new RuleNotAddedException(JEMessages.RULE_ID_NULL);
		}
		if (ruleModel.getRuleName() == null) {
			throw new RuleNotAddedException(JEMessages.RULE_NAME_NULL);
		}

		JELogger.debug(" Creating rule " + ruleModel.getRuleName() + "..", CATEGORY, projectId, RULE,
				ruleModel.getRuleId());
		UserDefinedRule rule = new UserDefinedRule();
		rule.setJobEngineElementID(ruleModel.getRuleId());
		rule.setJobEngineProjectID(projectId);
		rule.setJobEngineElementName(ruleModel.getRuleName());
		rule.setDescription(ruleModel.getDescription());
		rule.setJeObjectCreationDate(LocalDateTime.now());
		rule.setJeObjectLastUpdate(LocalDateTime.now());
		RuleParameters ruleParameters = new RuleParameters();
		rule.setJeObjectCreatedBy(ruleModel.getCreatedBy());
		rule.setJeObjectModifiedBy(ruleModel.getModifiedBy());
		ruleParameters.setSalience(ruleModel.getSalience());
		ruleParameters.setTimer(ruleModel.getTimer());
		ruleParameters.setEnabled(String.valueOf(ruleModel.getEnabled()));
		ruleParameters.setDateEffective(ruleModel.getDateEffective());
		ruleParameters.setDateExpires(ruleModel.getDateExpires());
		rule.setRuleParameters(ruleParameters);
		project.addRule(rule);
		ruleRepository.save(rule);
	}

	/*
	 * delete rule from a project
	 */

	public void deleteRule(String projectId, String ruleId)
			throws ProjectNotFoundException, RuleNotFoundException, JERunnerErrorException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();
		JEProject project = getProject(projectId);
		if (project.getRule(ruleId) instanceof UserDefinedRule) {
			UserDefinedRule rule = getRule(project, ruleId);
			if (rule.getSubRules() != null) {
				for (String subRuleId : rule.getSubRules()) {

					JERunnerAPIHandler.deleteRule(projectId, subRuleId);
					JELogger.debug("[project = " + project.getProjectName() + "] [rule = "
							+ rule.getJobEngineElementName() + "]" + JEMessages.DELETING_RULE, CATEGORY, projectId,
							RULE, ruleId);
				}
			}
			removeAllRuleBlockNames(projectId, ruleId);
		} else {
			JERunnerAPIHandler.deleteRule(projectId, ruleId);

		}
		project.deleteRule(ruleId);
		project.getRuleEngine().remove(ruleId);
		ruleRepository.deleteById(ruleId);
	}

	/*
	 * update rule : update rule attributes
	 */

	public void updateRule(String projectId, RuleModel ruleModel)
			throws ProjectNotFoundException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();
		JEProject project = getProject(projectId);
		UserDefinedRule ruleToUpdate = (UserDefinedRule) project.getRule(ruleModel.getRuleId());
		ruleToUpdate.setJeObjectLastUpdate(LocalDateTime.now());

		JELogger.debug("[project = " + project.getProjectName() + "] [rule = "
				+ project.getRules().get(ruleModel.getRuleId()).getJobEngineElementName() + "]"
				+ JEMessages.UPDATING_RULE, CATEGORY, projectId, RULE, ruleModel.getRuleId());
		// update rule name
		if (ruleModel.getRuleName() != null && !ruleModel.getRuleName().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.setJobEngineElementName(ruleModel.getRuleName());
		} else if (ruleModel.getRuleName() != null && ruleModel.getRuleName().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.setJobEngineElementName(null);

		}

		// update createdBy
		if (ruleModel.getCreatedBy() != null && !ruleModel.getCreatedBy().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.setJeObjectCreatedBy(ruleModel.getCreatedBy());
		} else if (ruleModel.getCreatedBy() != null && ruleModel.getCreatedBy().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.setJeObjectCreatedBy(null);

		}

		// update modifiedBy
		if (ruleModel.getModifiedBy() != null && !ruleModel.getModifiedBy().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.setJeObjectModifiedBy(ruleModel.getModifiedBy());
		} else if (ruleModel.getModifiedBy() != null && ruleModel.getModifiedBy().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.setJeObjectModifiedBy(null);

		}

		// update rule description
		if (ruleModel.getDescription() != null && !ruleModel.getDescription().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.setDescription(ruleModel.getDescription());
		} else if (ruleModel.getDescription() != null && ruleModel.getDescription().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.setDescription(null);

		}

		// update Salience
		if (ruleModel.getSalience() != null && !ruleModel.getSalience().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setSalience(ruleModel.getSalience());
		} else if (ruleModel.getSalience() != null && ruleModel.getSalience().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setSalience(null);

		}

		// update DateEffective
		if (ruleModel.getDateEffective() != null && !ruleModel.getDateEffective().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setDateEffective(ruleModel.getDateEffective());
		} else if (ruleModel.getDateEffective() != null
				&& ruleModel.getDateEffective().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setDateEffective(null);

		}

		// update DateExpires
		if (ruleModel.getDateExpires() != null && !ruleModel.getDateExpires().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setDateExpires(ruleModel.getDateExpires());
		} else if (ruleModel.getDateExpires() != null && ruleModel.getDateExpires().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setDateExpires(null);

		}

		// update Timer
		if (ruleModel.getTimer() != null && !ruleModel.getTimer().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setTimer(ruleModel.getTimer());
		} else if (ruleModel.getTimer() != null && ruleModel.getTimer().equals(DEFAULT_DELETE_CONSTANT)) {
			ruleToUpdate.getRuleParameters().setTimer(null);

		}

		// update Enabled
		ruleToUpdate.setEnabled(ruleModel.getEnabled());
		if (ruleToUpdate.isEnabled()) {
			ruleToUpdate.getRuleParameters().setEnabled(String.valueOf(ruleModel.getEnabled()));

		} else {
			ruleToUpdate.getRuleParameters().setEnabled(null);
		}

		ruleToUpdate.setBuilt(false);
		project.setBuilt(false);
		ruleRepository.save(ruleToUpdate);
	}

	/*
	 * update rule : add block to rule
	 */
	public String addBlockToRule(BlockModel blockModel) throws AddRuleBlockException, ProjectNotFoundException,
			RuleNotFoundException, DataDefinitionUnreachableException, JERunnerErrorException, AddClassException,
			ClassLoadException, IOException, InterruptedException, ExecutionException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();
		if (blockModel.getProjectId() == null) {
			throw new AddRuleBlockException(JEMessages.BLOCK_PROJECT_ID_NULL);
		}

		if (blockModel.getRuleId() == null) {
			throw new AddRuleBlockException(JEMessages.BLOCK_RULE_ID_NULL);
		}

		JEProject project = getProject(blockModel.getProjectId());
		UserDefinedRule rule = getRule(project, blockModel.getRuleId());
		verifyBlockFormatIsValid(blockModel);

		// check if block already exists
		boolean blockExists = rule.containsBlock(blockModel.getBlockId());
		if (blockExists) {
			throw new AddRuleBlockException(JEMessages.BLOCK_EXISTS);
		}

		JELogger.debug(
				JEMessages.ADDING_BLOCK + blockModel.getBlockName() + " to rule [id : " + blockModel.getRuleId() + "]",
				CATEGORY, blockModel.getProjectId(), RULE, blockModel.getRuleId());
		String generatedBlockName = project.generateUniqueBlockName(blockModel.getBlockName());
		blockModel.setBlockName(generatedBlockName);

		// create block
		Block block = BlockGenerator.createBlock(blockModel);

		// add block to rule
		rule.addBlock(block);
		rule.setJeObjectLastUpdate(LocalDateTime.now());

		// retrieve topic names from getter blocks
		if (blockModel.getOperationId() == 4002 && blockModel.getBlockConfiguration() != null
				&& blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID) != null) {

			String classId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID);
			String workspaceId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.WORKSPACEID);

			rule.addTopic(classId);

			classService.addClass(workspaceId, classId, true);
		}

		project.addBlockName(blockModel.getBlockId(), generatedBlockName);
		project.setBuilt(false);
		ruleRepository.save(rule);
		return generatedBlockName;

	}

	/*
	 * update rule : update block in rule
	 */
	public void updateBlockInRule(BlockModel blockModel) throws AddRuleBlockException, ProjectNotFoundException,
			RuleNotFoundException, DataDefinitionUnreachableException, JERunnerErrorException, AddClassException,
			ClassLoadException, IOException, InterruptedException, ExecutionException, RuleBlockNotFoundException,
			LicenseNotActiveException {

		LicenseProperties.checkLicenseIsActive();
		// check project id is not null
		if (blockModel.getProjectId() == null) {
			throw new AddRuleBlockException(JEMessages.BLOCK_PROJECT_ID_NULL);
		}

		// check rule is is not null
		if (blockModel.getRuleId() == null) {
			throw new AddRuleBlockException(JEMessages.BLOCK_RULE_ID_NULL);
		}

		JEProject project = getProject(blockModel.getProjectId());
		UserDefinedRule rule = getRule(project, blockModel.getRuleId());
		boolean blockExists = rule.containsBlock(blockModel.getBlockId());

		if (!blockExists) {
			JELogger.error(JEMessages.BLOCK_NOT_FOUND + " [ " + blockModel.getBlockId() + "]", CATEGORY,
					blockModel.getProjectId(), RULE, blockModel.getBlockId());
			throw new RuleBlockNotFoundException("Block not found" + " [ " + blockModel.getRuleId() + "]");

		}
		verifyBlockFormatIsValid(blockModel);

		// check if block already exists
		Block oldblock = rule.getBlocks().getBlock(blockModel.getBlockId());

		JELogger.debug(JEMessages.UPDATING_BLOCK + blockModel.getBlockName() + " in rule [id : "
				+ blockModel.getRuleId() + "]", CATEGORY, blockModel.getProjectId(), RULE, blockModel.getBlockId());

		// create block
		Block block = BlockGenerator.createBlock(blockModel);

		// check block name is valid
		if (!oldblock.getBlockName().equals(block.getBlockName())) {
			if (project.blockNameExists(block.getBlockName())) {
				throw new AddRuleBlockException(JEMessages.BLOCK_NAME_EXISTS);
			} else {
				project.removeBlockName(block.getJobEngineElementID());
				project.addBlockName(blockModel.getBlockId(), block.getBlockName());
			}
		}

		// add block to rule
		rule.addBlock(block);
		rule.setJeObjectLastUpdate(LocalDateTime.now());

		// retrieve topic names from getter blocks
		if (blockModel.getOperationId() == 4002 && blockModel.getBlockConfiguration() != null
				&& blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID) != null) {

			String classId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID);
			String workspaceId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.WORKSPACEID);

			rule.updateTopic(((AttributeGetterBlock) oldblock).getClassId(), classId);

			classService.addClass(workspaceId, classId, true);
		}
		project.setBuilt(false);
		ruleRepository.save(rule);

	}

	/*
	 * delete block
	 */

	public void deleteBlock(String projectId, String ruleId, String blockId) throws ProjectNotFoundException,
			RuleNotFoundException, RuleBlockNotFoundException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();
		JEProject project = getProject(projectId);
		if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(projectId, ruleId);
		}
		JELogger.debug(JEMessages.DELETING_BLOCK + blockId + " in rule ["
				+ project.getRule(ruleId).getJobEngineElementName() + "] in project  = " + project.getProjectName(),
				CATEGORY, projectId, RULE, ruleId);
		project.deleteRuleBlock(ruleId, blockId);
		project.removeBlockName(blockId);
		ruleRepository.save(project.getRule(ruleId));
	}

	@Async
	public CompletableFuture<Void> compileRules(String projectId)
			throws ProjectNotFoundException, RuleBuildFailedException, JERunnerErrorException, RuleNotFoundException {
		JEProject project = getProject(projectId);
		JELogger.debug("[project = " + project.getProjectName() + "]" + JEMessages.BUILDING_RULES, CATEGORY, projectId,
				RULE, null);
		cleanUpRules(project);
		ArrayList<CompletableFuture<?>> ruleFuture = new ArrayList<>();

		for (Entry<String, JERule> entry : project.getRules().entrySet()) {
			String ruleId = entry.getKey();
			if (entry.getValue().isEnabled()) {
				ruleFuture.add(asyncRuleService.compileRule(projectId, ruleId, true));
			}
		}

		ruleFuture.forEach(CompletableFuture::join);
		return CompletableFuture.completedFuture(null);
	}

	private void cleanUpRules(JEProject project) throws JERunnerErrorException {

		for (JERule rule : project.getRules().values()) {
			cleanUpRule(project, rule.getJobEngineElementID());
		}

	}

	@Async
	/*
	 * Add Rule To Rule engine
	 */
	public CompletableFuture<Void> buildRules(String projectId)
			throws ProjectNotFoundException, RuleBuildFailedException, JERunnerErrorException, RuleNotFoundException {
		JEProject project = getProject(projectId);
		JELogger.debug("[project = " + project.getProjectName() + "]" + JEMessages.BUILDING_RULES, CATEGORY, projectId,
				RULE, null);
		cleanUpRules(project);
		ArrayList<CompletableFuture<?>> ruleFuture = new ArrayList<>();

		for (Entry<String, JERule> entry : project.getRules().entrySet()) {
			String ruleId = entry.getKey();
			if (entry.getValue().isEnabled() && !entry.getValue().isRunning()) {
				ruleFuture.add(asyncRuleService.compileRule(projectId, ruleId, false));
			}
		}

		ruleFuture.forEach(CompletableFuture::join);
		return CompletableFuture.completedFuture(null);
	}

	private void cleanUpRule(JEProject project, String ruleId) throws JERunnerErrorException {

		String rulePrefix = RuleIdManager.generateSubRulePrefix(ruleId);
		FileUtilities.deleteFilesInPathByPrefix(project.getConfigurationPath(), rulePrefix);
		JELogger.debug(JEMessages.DELETING_RULE_RUNNER, CATEGORY, project.getProjectId(), RULE, ruleId);
		if (project.getRule(ruleId) instanceof UserDefinedRule) {
			UserDefinedRule rule = (UserDefinedRule) project.getRule(ruleId);
			for (String subRuleId : rule.getSubRules()) {
				JERunnerAPIHandler.deleteRule(project.getProjectId(), subRuleId);
			}
		}

	}

	/*
	 * Retrieve list of all rules that exist in a project.
	 */

	public Collection<RuleModel> getAllRules(String projectId)
			throws ProjectNotFoundException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();
		JEProject project = getProject(projectId);

		List<RuleModel> rules = new ArrayList<>();
		JELogger.debug("[project = " + project.getProjectName() + "]" + JEMessages.LOADING_RULES, CATEGORY,
				project.getProjectId(), RULE, null);
		for (JERule rule : ruleRepository.findByJobEngineProjectID(projectId)) {

			rules.add(new RuleModel(rule));

		}
		return rules;
	}

	public RuleModel getRule(String projectId, String ruleId)
			throws ProjectNotFoundException, RuleNotFoundException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		JEProject project = getProject(projectId);
		if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(projectId, ruleId);
		}
		JELogger.debug(
				"[project = " + project.getProjectName() + "] [rule = "
						+ project.getRules().get(ruleId).getJobEngineElementName() + "]" + JEMessages.LOADING_RULE,
				CATEGORY, project.getProjectId(), RULE, ruleId);
		return new RuleModel(ruleRepository.findById(ruleId).get());
	}

	/*
	 * add scripted rule
	 */

	public void addScriptedRule(String projectId, ScriptRuleModel ruleModel)
			throws ProjectNotFoundException, RuleAlreadyExistsException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		ScriptedRule rule = new ScriptedRule(projectId, ruleModel.getRuleId(), ruleModel.getScript(),
				ruleModel.getRuleName());
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
		}
		JELogger.debug("[projectId = " + projectId + "]" + JEMessages.ADDING_SCRIPTED_RULE, CATEGORY,
				project.getProjectId(), RULE, ruleModel.getRuleId());
		project.addRule(rule);
		ruleRepository.save(rule);
	}

	/*
	 * update scripted rule
	 *
	 */

	public void updateScriptedRule(String projectId, ScriptRuleModel ruleModel)
			throws ProjectNotFoundException, RuleNotFoundException {

		ScriptedRule rule = new ScriptedRule(projectId, ruleModel.getRuleId(), ruleModel.getScript(),
				ruleModel.getRuleName());
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
		}
		JELogger.debug("[projectId = " + projectId + "]" + JEMessages.UPDATING_SCRIPTED_RULE, CATEGORY, projectId, RULE,
				ruleModel.getRuleId());
		project.updateRule(rule);
		ruleRepository.save(rule);
	}

	public void saveRuleFrontConfig(String projectId, String ruleId, String config)
			throws ProjectNotFoundException, RuleNotFoundException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		JEProject project = getProject(projectId);
		if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(projectId, ruleId);
		}
		JELogger.debug(
				"[project = " + project.getProjectName() + "] [rule = "
						+ project.getRules().get(ruleId).getJobEngineElementName() + "]" + JEMessages.FRONT_CONFIG,
				CATEGORY, projectId, RULE, ruleId);
		project.getRule(ruleId).setRuleFrontConfig(config);
		ruleRepository.save(project.getRule(ruleId));
	}

	public void verifyBlockFormatIsValid(BlockModel blockModel)
			throws AddRuleBlockException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		// block Id can't be null
		if (blockModel == null || blockModel.getBlockId() == null || blockModel.getBlockId().isEmpty()) {
			throw new AddRuleBlockException(JEMessages.BLOCK_ID_NULL);

		}

		if (blockModel.getBlockName() == null || blockModel.getBlockName().isEmpty()) {
			throw new AddRuleBlockException(JEMessages.BLOCK_NAME_EMPTY);

		}
		// block operation id can't be empty
		if (blockModel.getOperationId() == 0) {
			throw new AddRuleBlockException(JEMessages.BLOCK_OPERATION_ID_UNKNOWN);
		}

	}

	/*
	 * deletes multiple rules in a project using their id. returns nothing if rules
	 * were deleted successfully if some rules were not deleted, throws exception
	 * with map [ key: rule that was not deleted , value : cause of the deletion
	 * failure ]
	 */
	public void deleteRules(String projectId, List<String> ruleIds)
			throws ProjectNotFoundException, RuleDeletionException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		JEProject project = getProject(projectId);
		HashMap<String, String> undeletedRules = new HashMap<>();
		for (String ruleId : ruleIds) {
			if (project.ruleExists(ruleId)) {
				try {
					JELogger.debug("[project = " + project.getProjectName() + "] [rule = "
							+ project.getRules().get(ruleId).getJobEngineElementName() + "]" + JEMessages.DELETING_RULE,
							CATEGORY, projectId, RULE, ruleId);
					if (project.getRule(ruleId) instanceof UserDefinedRule) {
						UserDefinedRule rule = (UserDefinedRule) project.getRule(ruleId);
						if (rule.getSubRules() != null) {
							for (String subRuleId : rule.getSubRules()) {
								JERunnerAPIHandler.deleteRule(projectId, subRuleId);
							}
							removeAllRuleBlockNames(projectId, ruleId);
						}
					} else {
						JERunnerAPIHandler.deleteRule(projectId, ruleId);

					}

					project.deleteRule(ruleId);
					ruleRepository.deleteById(ruleId);
				} catch (Exception e) {
					undeletedRules.put(ruleId, e.getMessage());
				}

			} else {
				undeletedRules.put(ruleId, JEMessages.RULE_NOT_FOUND);

			}
		}

		if (!undeletedRules.isEmpty()) {
			throw new RuleDeletionException(JEMessages.FAILED_TO_DELETE_SOME_RULES + undeletedRules);
		}

	}

	/*
	 * build rule : create drl + check for compilation errors
	 */
	public void compileRule(String projectId, String ruleId) throws ProjectNotFoundException, RuleNotFoundException,
			RuleBuildFailedException, JERunnerErrorException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
		} else if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(projectId, ruleId);
		}
		JELogger.debug("[project = " + project.getProjectName() + "] [rule = " + project.getRule(ruleId).getJobEngineElementName() + "]" + JEMessages.BUILDING_RULE, CATEGORY,
				projectId, RULE, ruleId);
		cleanUpRule(project, ruleId);
		RuleBuilder.buildRule(project.getRule(ruleId), project.getConfigurationPath(), true);

	}

	private void removeAllRuleBlockNames(String projectId, String ruleId) throws LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		JEProject project = ProjectService.getProjectById(projectId);
		Enumeration<String> blockIds = ((UserDefinedRule) project.getRule(ruleId)).getBlocks().getAllBlockIds();
		while (blockIds.hasMoreElements()) {
			project.removeBlockName(blockIds.nextElement());
		}
	}

	public void deleteAll(String projectId) {
		ruleRepository.deleteByJobEngineProjectID(projectId);

	}

	public ConcurrentHashMap<String, JERule> getAllJERules(String projectId) throws LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();
		List<JERule> rules = ruleRepository.findByJobEngineProjectID(projectId);
		ConcurrentHashMap<String, JERule> map = new ConcurrentHashMap<>();
		for (JERule rule : rules) {
			map.put(rule.getJobEngineElementID(), rule);
		}
		return map;
	}

	/*
	 * run a specific rule.
	 */
	public void runRule(String projectId, String ruleId) throws LicenseNotActiveException, ProjectNotFoundException,
			RuleNotFoundException, RuleBuildFailedException, JERunnerErrorException {
		LicenseProperties.checkLicenseIsActive();

		JEProject project = getProject(projectId);
		if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(projectId, ruleId);
		}
		if (!project.getRules().get(ruleId).isEnabled()) {
			throw new RuleBuildFailedException("Rule [" + project.getRules().get(ruleId).getJobEngineElementName()
					+ "] is currently not enabled. Make sure to enable rule before attempting to run it.");
		}
		RuleBuilder.buildRule(project.getRules().get(ruleId), project.getConfigurationPath(), false);
		project.getRules().get(ruleId).setBuilt(true);
		project.getRules().get(ruleId).setAdded(true);
		project.getRules().get(ruleId).setRunning(true);
		project.getRuleEngine().add(ruleId);
		if (!project.getRuleEngine().isRunning()) {
			JERunnerAPIHandler.runProjectRules(projectId);
			project.getRuleEngine().setRunning(true);

		}
	}

	public void stopRule(String projectId, String ruleId)
			throws ProjectNotFoundException, RuleNotFoundException, JERunnerErrorException, RuleNotAddedException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		JEProject project = getProject(projectId);
		UserDefinedRule rule = getRule(project, ruleId);
		if (rule.isRunning()) {
			if (rule.getSubRules() != null) {
				for (String subRuleId : rule.getSubRules()) {

					try {
						JERunnerAPIHandler.deleteRule(projectId, subRuleId);
						project.getRule(ruleId).setAdded(false);
						project.getRule(ruleId).setBuilt(false);
						project.getRules().get(ruleId).setRunning(false);
						project.getRuleEngine().remove(ruleId);

						if (project.getRuleEngine().getBuiltRules().isEmpty()) {
							JERunnerAPIHandler.shutDownRuleEngine(projectId);
							project.getRuleEngine().setRunning(false);
						} else if (project.getRuleEngine().getBuiltRules().size() == 1) {
							JERunnerAPIHandler.shutDownRuleEngine(projectId);
							JERunnerAPIHandler.runProjectRules(projectId);

						}

					} catch (JERunnerErrorException e) {
						JELogger.error(
								"Failed to stop rule : " + project.getRules().get(ruleId).getJobEngineElementName(),
								CATEGORY, projectId, RULE, null);
						throw new JERunnerErrorException("Failed to stop rule : " + e.getMessage());
					}

				}
			}
		} /*else {
			JELogger.error("Rule already stopped : " + project.getRules().get(ruleId).getJobEngineElementName(),
					CATEGORY, projectId, RULE, null);
			throw new RuleNotAddedException("Rule Already stopped");
		}
*/
	}

	private JEProject getProject(String projectId) throws ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
		}
		return project;
	}

	private UserDefinedRule getRule(JEProject project, String ruleId) throws RuleNotFoundException {
		if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(project.getProjectId(), ruleId);
		}
		return (UserDefinedRule) project.getRule(ruleId);
	}

	public void updateRulesStatus(String projectId, boolean setRunning) throws ProjectNotFoundException {
		JEProject project = getProject(projectId);
		for (Entry<String, JERule> rule : project.getRules().entrySet()) {
			rule.getValue().setRunning(setRunning);
			if (setRunning) {
				project.getRuleEngine().add(rule.getKey());
			} else {
				project.getRuleEngine().remove(rule.getKey());
			}
		}

	}

	public void runRules(String projectId, List<String> ruleIds) throws LicenseNotActiveException, ProjectNotFoundException, RuleNotFoundException, RuleBuildFailedException, JERunnerErrorException {
		LicenseProperties.checkLicenseIsActive();

		for(String ruleId : ruleIds)
		{
			//TODO: add build multiple rule to JERunner

			runRule(projectId,ruleId);
		}
		
	}

	public void stopRules(String projectId, List<String> ruleIds)
			throws ProjectNotFoundException, RuleNotFoundException, JERunnerErrorException, RuleNotAddedException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		for(String ruleId : ruleIds) {
			stopRule(projectId,ruleId);
			
		}

	}


}
