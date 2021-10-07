package io.je.project.services;


import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleNotFoundException;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import static io.je.utilities.constants.JEMessages.ADDING_JAR_FILE_TO_RUNNER;

@Service
public class AsyncRuleService {
	
	/*
	 * build rule : create drl + check for compilation errors
	 */
	@Async
	public CompletableFuture<Void> compileRule(String projectId, String ruleId, boolean compileOnly)
			throws RuleNotFoundException, RuleBuildFailedException, JERunnerErrorException
			 {
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
		return CompletableFuture.completedFuture(null);

	}


}
