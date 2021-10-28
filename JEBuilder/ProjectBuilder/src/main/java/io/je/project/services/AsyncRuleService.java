package io.je.project.services;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
		JEProject project = null;
		JERule rule = null;
		// check rule exists
		try {
			 project = getProject(projectId);
				rule = project.getRule(ruleId);
		} catch (Exception e) {
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());
			return CompletableFuture.completedFuture(result);
		}
		result.setItemName(rule.getJobEngineElementName());

		try {
			if(rule.isEnabled())
			{
				RuleBuilder.buildRule(rule, getProject(projectId).getConfigurationPath(), compileOnly);
				// update rule status
				// rule built
				rule.setBuilt(true);
				if (!compileOnly) {
					rule.setAdded(true);
					project.getRuleEngine().add(ruleId);
				}
				result.setOperationSucceeded(true);
			}
		} catch (RuleBuildFailedException | JERunnerErrorException | ProjectNotFoundException e) {
			rule.setBuilt(false);
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());
		}
		try {
			RuleService.updateRuleStatus(rule);
			ruleRepository.save(rule);
		} catch (Exception e) {
			JELogger.error("[rule = "+rule.getJobEngineElementName() +"]"+JEMessages.STATUS_UPDATE_FAILED ,
					CATEGORY, projectId, RULE, null);

		}
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

		JEProject project = null;
		JERule rule = null;

		// check rule exists
		try {
		    project = getProject(projectId);
			rule = project.getRule(ruleId);
		} catch (Exception e) {
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());
			return CompletableFuture.completedFuture(result);
		}
		result.setItemName(rule.getJobEngineElementName());
		
		if(!rule.isEnabled())
		{
			result.setOperationSucceeded(false);
			result.setOperationError(JEMessages.RULE_DISABLED);
			return CompletableFuture.completedFuture(result);
		}
		
		
		if(!rule.isBuilt())
		{
			result.setOperationSucceeded(false);
			result.setOperationError(JEMessages.RULE_NOT_BUILT);
			return CompletableFuture.completedFuture(result);
			
		}
		try {
			//set rule topics
			rule.loadTopics();
			
			buildRule(projectId,ruleId).get();
			if (!project.getRuleEngine().isRunning()) {
				JERunnerAPIHandler.runProjectRules(projectId);
				project.getRuleEngine().setRunning(true);
			}
			project.getRule(ruleId).setRunning(false);
			rule.setRunning(true);
			result.setOperationSucceeded(true);
		} catch (  JERunnerErrorException | InterruptedException e) {
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());
		} catch (ExecutionException e) {
			result.setOperationSucceeded(false);
			result.setOperationError(e.getCause().getMessage());
		}
		try {
			RuleService.updateRuleStatus(rule);
			ruleRepository.save(rule);
		} catch (Exception e) {
			JELogger.error("[rule = "+rule.getJobEngineElementName() +"]"+JEMessages.STATUS_UPDATE_FAILED ,
					CATEGORY, projectId, RULE, null);

		}
		return CompletableFuture.completedFuture(result);

	}



	private JEProject getProject(String projectId) throws ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			JELogger.error(
					"[projectId = " + projectId + "] " + JEMessages.PROJECT_NOT_FOUND,
					CATEGORY, projectId, RULE, projectId);
			throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
		}
		return project;
	}

	public JERule getRule(String projectId, String ruleId)
			throws ProjectNotFoundException, RuleNotFoundException, LicenseNotActiveException {
		LicenseProperties.checkLicenseIsActive();

		JEProject project = getProject(projectId);
		if (!project.ruleExists(ruleId)) {
			JELogger.error(
					"[project = " + project.getProjectName() + "] [rule = "
							+ project.getRules().get(ruleId).getJobEngineElementName() + "]" + JEMessages.RULE_NOT_FOUND,
					CATEGORY, project.getProjectId(), RULE, ruleId);
			throw new RuleNotFoundException(projectId, ruleId);
		}
		JELogger.debug(
				"[project = " + project.getProjectName() + "] [rule = "
						+ project.getRules().get(ruleId).getJobEngineElementName() + "]" + JEMessages.LOADING_RULE,
				CATEGORY, project.getProjectId(), RULE, ruleId);
		return ruleRepository.findById(ruleId).get();
	}

}
