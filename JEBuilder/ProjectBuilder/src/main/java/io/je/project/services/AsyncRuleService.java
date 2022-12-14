package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.config.LicenseProperties;
import io.je.project.repository.RuleRepository;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.rulebuilder.components.JERule;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.Status;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.ruleutils.OperationStatusDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Lazy
public class AsyncRuleService {

    private static final LogSubModule RULE = LogSubModule.RULE;
    private static final LogCategory CATEGORY = LogCategory.DESIGN_MODE;
    @Autowired
    @Lazy
    RuleRepository ruleRepository;
    @Autowired
    @Lazy
    ProjectService projectService;

    /*
     * Build rule : create drl + check for compilation errors
     */
    @Async
    public CompletableFuture<OperationStatusDetails> compileRule(String projectId, String ruleId, boolean compileOnly) {
        OperationStatusDetails result = new OperationStatusDetails(ruleId);
        JEProject project = null;
        JERule rule = null;
        // Check rule exists
        try {
            project = getProject(projectId);
            rule = project.getRule(ruleId);
        } catch (Exception e) {
            LoggerUtils.logException(e);
            result.setOperationSucceeded(false);
            result.setOperationError(e.getMessage());
            return CompletableFuture.completedFuture(result);
        }
        result.setItemName(rule.getJobEngineElementName());
        // Set rule topics
        rule.loadTopics();

        try {
            if (rule.isEnabled()) {

                RuleBuilder.buildRule(rule, getProject(projectId).getConfigurationPath(), compileOnly);

                // Update rule status
                // Rule built
                if (!compileOnly) {
                    rule.setAdded(true);
                    rule.setBuilt(true);
                    project.getRuleEngine().add(ruleId);
                }

                rule.setCompiled(true);
                result.setOperationSucceeded(true);
                rule.setContainsErrors(false);

            }
        } catch (RuleBuildFailedException | JERunnerErrorException | ProjectNotFoundException | ProjectLoadException |
                 LicenseNotActiveException e) {
            LoggerUtils.logException(e);

            rule.setBuilt(false);
            rule.setContainsErrors(true);
            rule.setStatus(Status.ERROR);
            ruleRepository.save(rule);

            result.setOperationSucceeded(false);
            result.setOperationError(e.getMessage());

            return CompletableFuture.completedFuture(result);
        }

        try {
            RuleService.updateRuleStatus(rule);
            ruleRepository.save(rule);
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error("[rule = " + rule.getJobEngineElementName() + "]" + JEMessages.STATUS_UPDATE_FAILED,
                    CATEGORY, projectId, RULE, null);

        }

        return CompletableFuture.completedFuture(result);

    }

    /*
     * Build rule
     * */
    @Async
    public CompletableFuture<OperationStatusDetails> buildRule(String projectId, String ruleId) {
        return compileRule(projectId, ruleId, false);

    }

    @Async
    /*
     * Run a specific rule.
     */
    public CompletableFuture<OperationStatusDetails> runRule(String projectId, String ruleId)
            throws LicenseNotActiveException {
        //LicenseProperties.checkLicenseIsActive();
        OperationStatusDetails result = new OperationStatusDetails(ruleId);

        JEProject project = null;
        JERule rule = null;

        // check rule exists
        try {
            project = getProject(projectId);
            rule = project.getRule(ruleId);
        } catch (Exception e) {
            LoggerUtils.logException(e);
            result.setOperationSucceeded(false);
            result.setOperationError(e.getMessage());
            return CompletableFuture.completedFuture(result);
        }
        result.setItemName(rule.getJobEngineElementName());

        if (rule.getStatus() == Status.RUNNING) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.RULE_ALREADY_RUNNING);
            return CompletableFuture.completedFuture(result);
        }

        if (!rule.isEnabled()) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.RULE_DISABLED);
            return CompletableFuture.completedFuture(result);
        }

        if (!rule.isCompiled()) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.RULE_NOT_BUILT);
            return CompletableFuture.completedFuture(result);

        }

        if (rule.getStatus() == Status.ERROR) {
            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.RULE_CONTAINS_ERRORS);
            return CompletableFuture.completedFuture(result);

        }
		
		/*if(rule.getStatus()==RuleStatus.RUNNING)
		{
			result.setOperationSucceeded(false);
			result.setOperationError(JEMessages.RULE_ALREADY_RUNNING);
			return CompletableFuture.completedFuture(result);
			
		}*/
        try {

            // FIXME to be removed, no build on run
            buildRule(projectId, ruleId).get();

            if (!project.getRuleEngine().isRunning()) {
                JERunnerAPIHandler.runRuleEngine(projectId);
                project.getRuleEngine().setRunning(true);
            }

            project.getRule(ruleId).setRunning(false);
            rule.setRunning(true);
            result.setOperationSucceeded(true);
            JELogger.control("[rule = " + rule.getJobEngineElementName() + "]" + JEMessages.RULE_RUNNING,
                    CATEGORY, projectId, RULE, null);
        } catch (JERunnerErrorException e) {
            LoggerUtils.logException(e);
            result.setOperationSucceeded(false);
            result.setOperationError(e.getMessage());
        } catch (ExecutionException e) {
            LoggerUtils.logException(e);
            result.setOperationSucceeded(false);
            result.setOperationError(e.getCause().getMessage());
        } catch (InterruptedException e) {
            LoggerUtils.logException(e);
            Thread.currentThread().interrupt();
            result.setOperationSucceeded(false);
            result.setOperationError(e.getMessage());
        }

        try {
            RuleService.updateRuleStatus(rule);
            ruleRepository.save(rule);
        } catch (Exception e) {
            LoggerUtils.logException(e);

            JELogger.error("[rule = " + rule.getJobEngineElementName() + "]" + JEMessages.STATUS_UPDATE_FAILED,
                    CATEGORY, projectId, RULE, null);

        }
        return CompletableFuture.completedFuture(result);

    }


    private JEProject getProject(String projectId) throws ProjectNotFoundException, ProjectLoadException, LicenseNotActiveException {
        JEProject project = projectService.getProjectById(projectId);
        if (project == null) {
            JELogger.error(
                    "[projectId = " + projectId + "] " + JEMessages.PROJECT_NOT_FOUND,
                    CATEGORY, projectId, RULE, projectId);
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        return project;
    }

    /*
     * Get rule bean
     * */
    public JERule getRule(String projectId, String ruleId)
            throws ProjectNotFoundException, RuleNotFoundException, LicenseNotActiveException, ProjectLoadException {
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
