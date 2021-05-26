package io.je.project.services;


import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.utilities.beans.JEMessages;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleNotFoundException;
import io.je.utilities.logger.JELogger;

@Service
public class AsyncRuleService {
	
	/*
	 * build rule : create drl + check for compilation errors
	 */
	@Async
	public CompletableFuture<Void> buildRule(String projectId, String ruleId)
			throws RuleNotFoundException, RuleBuildFailedException, JERunnerErrorException,
			IOException, InterruptedException, ExecutionException {
		JEProject project = ProjectService.getProjectById(projectId);
		 if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException(JEMessages.RULE_NOT_FOUND);
		}
		JELogger.trace(" [projectId="+ projectId +" ]" + JEMessages.BUILDING_RULE +" : " + project.getRule(ruleId).getRuleName());
		RuleBuilder.buildRule(project.getRule(ruleId), project.getConfigurationPath());
		project.getRules().get(ruleId).setBuilt(true);
		project.getRules().get(ruleId).setAdded(true);
		return CompletableFuture.completedFuture(null);

	}


}
