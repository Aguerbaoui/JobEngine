package io.je.project.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.project.config.LicenseProperties;
import io.je.project.repository.RuleRepository;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.LicenseNotActiveException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleNotAddedException;
import io.je.utilities.exceptions.RuleNotFoundException;
import io.je.utilities.log.JELogger;
import io.je.utilities.ruleutils.RuleStatus;
import io.je.utilities.ruleutils.OperationStatusDetails;
import utils.log.LogCategory;
import utils.log.LogSubModule;

@Service
public class AsyncRuleService {

	@Autowired
	RuleRepository ruleRepository;

	private static final LogSubModule RULE = LogSubModule.RULE;
	private static final LogCategory CATEGORY = LogCategory.DESIGN_MODE;

	/*
	 * build rule : create drl + check for compilation errors
	 */
	@Async
	public CompletableFuture<OperationStatusDetails> compileRule(String projectId, String ruleId, boolean compileOnly)

	{
		OperationStatusDetails result = new OperationStatusDetails(ruleId);

		JERule rule = null;

		// check rule exists
		try {
			rule = getRule(projectId, ruleId);
		} catch (Exception e) {
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());
			return CompletableFuture.completedFuture(result);
		}
		result.setItemName(rule.getJobEngineElementName());

		try {
			RuleBuilder.buildRule(rule, getProject(projectId).getConfigurationPath(), compileOnly);
			// update rule status
			// rule built
			if (!compileOnly) {
				rule.setBuilt(true);
				rule.setAdded(true);
				if (rule.isRunning()) {
					rule.setStatus(RuleStatus.RUNNING);
				} else {
					rule.setStatus(RuleStatus.STOPPED);
				}
			}
			ruleRepository.save(rule);
			result.setOperationSucceeded(true);
		} catch (RuleBuildFailedException | JERunnerErrorException | ProjectNotFoundException e) {
			rule.setStatus(RuleStatus.ERROR);
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());
		}
		ruleRepository.save(rule);
		return CompletableFuture.completedFuture(result);

	}

	@Async
	public CompletableFuture<OperationStatusDetails> buildRule(String projectId, String ruleId) {
		return compileRule(projectId, ruleId, false);

	}

	@Async
	/*
	 * run a specific rule.
	 */
	public CompletableFuture<OperationStatusDetails> runRule(String projectId, String ruleId)
			throws LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();
		OperationStatusDetails result = new OperationStatusDetails(ruleId);

		JERule rule = null;

		// check rule exists
		try {
			rule = getRule(projectId, ruleId);
		} catch (Exception e) {
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());
			return CompletableFuture.completedFuture(result);
		}
		result.setItemName(rule.getJobEngineElementName());
		try {
			JEProject project = getProject(projectId);
			RuleBuilder.buildRule(project.getRules().get(ruleId), project.getConfigurationPath(), false);
			project.getRules().get(ruleId).setBuilt(true);
			project.getRules().get(ruleId).setAdded(true);
			project.getRuleEngine().add(ruleId);
			if (!project.getRuleEngine().isRunning()) {
				JERunnerAPIHandler.runProjectRules(projectId);
				project.getRuleEngine().setRunning(true);

			}
			project.getRules().get(ruleId).setRunning(true);
			project.getRules().get(ruleId).setStatus(RuleStatus.RUNNING);
		} catch (RuleBuildFailedException | ProjectNotFoundException | JERunnerErrorException e) {
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());
		}

		return CompletableFuture.completedFuture(result);

	}

	@Async
	public CompletableFuture<OperationStatusDetails> stopRule(String projectId, String ruleId)
			throws ProjectNotFoundException, RuleNotFoundException, JERunnerErrorException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		JEProject project = getProject(projectId);
		OperationStatusDetails result = new OperationStatusDetails(ruleId);

		UserDefinedRule rule = null;

		// check rule exists
		try {
			rule = (UserDefinedRule) getRule(projectId, ruleId);
		} catch (Exception e) {
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());
			return CompletableFuture.completedFuture(result);
		}
		result.setItemName(rule.getJobEngineElementName());

		if (rule.isRunning() && rule.getSubRules() != null) {
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

					project.getRules().get(ruleId).setStatus(RuleStatus.STOPPED);
					result.setOperationSucceeded(true);
				} catch (JERunnerErrorException e) {
					JELogger.error("Failed to stop rule : " + project.getRules().get(ruleId).getJobEngineElementName(),
							CATEGORY, projectId, RULE, null);

					result.setOperationSucceeded(false);
					result.setOperationError("Failed to stop rule : " + e.getMessage());

				}

			}
		}

		return CompletableFuture.completedFuture(result);

	}

	private JEProject getProject(String projectId) throws ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
		}
		return project;
	}

	public JERule getRule(String projectId, String ruleId)
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
		return ruleRepository.findById(ruleId).get();
	}

}
