package io.je.project.services;


import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.project.config.LicenseProperties;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.LicenseNotActiveException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleNotFoundException;
import io.je.utilities.log.JELogger;
import io.je.utilities.ruleutils.RuleStatus;
import io.je.utilities.ruleutils.OperationStatusDetails;
import utils.log.LogCategory;
import utils.log.LogSubModule;

@Service
public class AsyncRuleService {
	
	/*
	 * build rule : create drl + check for compilation errors
	 */
	@Async
	public CompletableFuture<OperationStatusDetails> compileRule(String projectId, String ruleId, boolean compileOnly)
			
			 {
		
		OperationStatusDetails result = new OperationStatusDetails(ruleId);
		try {
			JEProject project = ProjectService.getProjectById(projectId);
			 if (!project.ruleExists(ruleId)) {
				throw new RuleNotFoundException(projectId, ruleId);
			}
			JELogger.debug(" [projectId="+ projectId +" ]" + JEMessages.BUILDING_RULE +" : " + project.getRule(ruleId).getJobEngineElementName(),
					LogCategory.DESIGN_MODE, projectId,
					LogSubModule.JEBUILDER, ruleId);
			RuleBuilder.buildRule(project.getRule(ruleId), project.getConfigurationPath(),compileOnly);
			if(!compileOnly)
			{
				project.getRules().get(ruleId).setBuilt(true);
				project.getRules().get(ruleId).setAdded(true);
			}
			result.setOperationSucceeded(true);
		}catch(RuleNotFoundException | RuleBuildFailedException | JERunnerErrorException e )
		{
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());

		}
		
		return CompletableFuture.completedFuture(result);

	}
	
	@Async
	public CompletableFuture<OperationStatusDetails> buildRule(String projectId, String ruleId) {
		return compileRule(projectId,ruleId,false);

	}

	
	@Async
	/*
	 * run a specific rule.
	 */
	public  CompletableFuture<OperationStatusDetails> runRule(String projectId, String ruleId) throws LicenseNotActiveException
			{
		LicenseProperties.checkLicenseIsActive();
		OperationStatusDetails result = new OperationStatusDetails(ruleId);
		try {
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
			project.getRuleEngine().add(ruleId);
			if (!project.getRuleEngine().isRunning()) {
				JERunnerAPIHandler.runProjectRules(projectId);
				project.getRuleEngine().setRunning(true);

			}
			project.getRules().get(ruleId).setRunning(true);
			project.getRules().get(ruleId).setStatus(RuleStatus.RUNNING);
		}catch(RuleBuildFailedException | ProjectNotFoundException | JERunnerErrorException | RuleNotFoundException e)
		{
			result.setOperationSucceeded(false);
			result.setOperationError(e.getMessage());
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


}
