package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.rulebuilder.components.*;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.rulebuilder.models.ScriptRuleModel;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.RuleBuilderErrors;
import io.je.utilities.exceptions.*;
import io.je.utilities.files.JEFileUtils;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.ClassDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/*
 * Service class to handle business logic for rules
 */
@Service
public class RuleService {

    private static final String DEFAULT_DELETE_CONSTANT = "DELETED";

    @Autowired
    ClassService classService;

    @Autowired
    AsyncRuleService asyncRuleService;

    /*
     * Add a rule to a project
     */
    public void addRule(String projectId, RuleModel ruleModel)
            throws ProjectNotFoundException, RuleAlreadyExistsException, RuleNotAddedException {
        JELogger.info(getClass(), "adding rule " + ruleModel.getRuleName());
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
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
        rule.setJeObjectCreationDate(LocalDateTime.now());
        rule.setJeObjectLastUpdate(LocalDateTime.now());
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

    public void deleteRule(String projectId, String ruleId) throws ProjectNotFoundException, RuleNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JELogger.trace(getClass(), "deleting rule [id : " + ruleId + ")");
        if (project.getRule(ruleId) instanceof UserDefinedRule) {
            UserDefinedRule rule = (UserDefinedRule) project.getRule(ruleId);
            if (rule.getSubRules() != null) {
                for (String subRuleId : rule.getSubRules()) {

                    JERunnerAPIHandler.deleteRule(projectId, subRuleId);

                }
            }
        } else {
            JERunnerAPIHandler.deleteRule(projectId, ruleId);

        }
        project.deleteRule(ruleId);

    }

    /*
     * update rule : update rule attributes
     */

    public void updateRule(String projectId, RuleModel ruleModel)
            throws ProjectNotFoundException, RuleNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        UserDefinedRule ruleToUpdate = (UserDefinedRule) project.getRule(ruleModel.getRuleId());
        ruleToUpdate.setJeObjectLastUpdate(LocalDateTime.now());

        JELogger.trace(getClass(), "deleting rule [id : " + ruleModel.getRuleId() + ") in project id = " + projectId);
        // update rule name
        if (ruleModel.getRuleName() != null && !ruleModel.getRuleName().equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.setRuleName(ruleModel.getRuleName());
        } else if (ruleModel.getRuleName() != null && ruleModel.getRuleName().equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.setRuleName(null);

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
        } else if (ruleModel.getDateEffective() != null && ruleModel.getDateEffective().equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters().setDateEffective(null);

        }

        // update DateExpires
        if (ruleModel.getDateExpires() != null && !ruleModel.getDateExpires().equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters().setDateExpires(ruleModel.getDateExpires());
        } else if (ruleModel.getDateExpires() != null && ruleModel.getDateExpires().equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters().setDateExpires(null);

        }

        // update Enabled
        if (ruleModel.getEnabled() != null && !ruleModel.getEnabled().equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters().setEnabled(ruleModel.getEnabled());
        } else if (ruleModel.getEnabled() != null && ruleModel.getEnabled().equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters().setEnabled(null);

        }

        // update Timer
        if (ruleModel.getTimer() != null && !ruleModel.getTimer().equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters().setTimer(ruleModel.getTimer());
        } else if (ruleModel.getTimer() != null && ruleModel.getTimer().equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters().setTimer(null);

        }
        ruleToUpdate.setBuilt(false);
        project.setBuilt(false);
    }

    /*
     * update rule : add block to rule
     */
    public void addBlockToRule(BlockModel blockModel) throws AddRuleBlockException, ProjectNotFoundException,
            RuleNotFoundException, DataDefinitionUnreachableException, JERunnerErrorException, AddClassException,
            ClassLoadException, IOException, InterruptedException, ExecutionException {

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
            throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound + " [ " + blockModel.getRuleId() + "]");
        }
        verifyBlockFormatIsValid(blockModel);
        JELogger.trace(getClass(), " Adding block name = " + blockModel.getBlockName() + " to rule [id : " + blockModel.getRuleId() + ")");
        JERule rule = project.getRule(blockModel.getRuleId());
        Block block = BlockGenerator.createBlock(blockModel);
        block.setInputBlockIds(blockModel.getInputBlocksIds());
        block.setOutputBlockIds(blockModel.getOutputBlocksIds());
        ((UserDefinedRule) rule).addBlock(block);
        rule.setJeObjectLastUpdate(LocalDateTime.now());
        // retrieve topic names from getter blocks
        if (blockModel.getOperationId() == 4002 && blockModel.getBlockConfiguration() != null
                & blockModel.getBlockConfiguration().getClassId() != null) {
            ClassDefinition classDef = new ClassDefinition(blockModel.getBlockConfiguration().getWorkspaceId(),
                    blockModel.getBlockConfiguration().getClassId());
            rule.addTopic(classDef.getClassId());
            classService.addClass(classDef);
        }
        project.setBuilt(false);


    }

    /*
     * delete block
     */

    public void deleteBlock(String projectId, String ruleId, String blockId) throws ProjectNotFoundException,
            RuleNotFoundException, RuleBlockNotFoundException, InterruptedException, ExecutionException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.ruleExists(ruleId)) {
            throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
        }
        JELogger.trace(getClass(), "deleting block id = " + blockId + " in rule [id : " + ruleId + ") in project id = " + projectId);
        project.deleteRuleBlock(ruleId, blockId);

    }

    @Async
    public CompletableFuture<Void> buildRules(String projectId)
            throws ProjectNotFoundException, RuleBuildFailedException, JERunnerErrorException, IOException,
            RuleNotFoundException, InterruptedException, ExecutionException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

        JELogger.trace(" Building all rules in project id = " + projectId);
        cleanUpRules(project);
        ArrayList<CompletableFuture<?>> ruleFuture = new ArrayList<>();

        for (Entry<String, JERule> entry : project.getRules().entrySet()) {
            String ruleId = entry.getKey();
            ruleFuture.add(asyncRuleService.buildRule(projectId, ruleId));
        }

        ruleFuture.forEach(CompletableFuture::join);
        return CompletableFuture.completedFuture(null);
    }

    private void cleanUpRules(JEProject project) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {

        for (JERule rule : project.getRules().values()) {
            cleanUpRule(project, rule.getJobEngineElementID());
        }

    }

    private void cleanUpRule(JEProject project, String ruleId) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {

        String rulePrefix = "[" + ruleId + "]";
        JEFileUtils.deleteFilesForPathByPrefix(project.getConfigurationPath(), rulePrefix);
        JELogger.trace(" Deleting rule from runner");
        JERunnerAPIHandler.deleteRule(project.getProjectId(), ruleId);

    }




    /*
     * Retrieve list of all rules that exist in a project.
     */

    public Collection<RuleModel> getAllRules(String projectId) throws ProjectNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

        List<RuleModel> rules = new ArrayList<>();
        JELogger.trace(" Getting all rules in project id = " + projectId);
        for (JERule rule : project.getRules().values()) {
            rules.add(new RuleModel(rule));
        }
        return rules;
    }

    public RuleModel getRule(String projectId, String ruleId) throws ProjectNotFoundException, RuleNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);

        } else if (!project.ruleExists(ruleId)) {
            throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
        }
        JELogger.trace(" Getting rule id = " + ruleId + " in project id = " + projectId);
        return new RuleModel(project.getRules().get(ruleId));
    }

    /*
     * add scripted rule
     */

    public void addScriptedRule(String projectId, ScriptRuleModel ruleModel)
            throws ProjectNotFoundException, RuleAlreadyExistsException {
        ScriptedRule rule = new ScriptedRule(projectId, ruleModel.getRuleId(), ruleModel.getScript(),
                ruleModel.getRuleName());
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JELogger.trace(" Adding new scripted rule in project id = " + projectId);
        project.addRule(rule);

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
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JELogger.trace(" Updating new scripted rule in project id = " + projectId);
        project.updateRule(rule);

    }

    public void saveRuleFrontConfig(String projectId, String ruleId, String config)
            throws ProjectNotFoundException, RuleNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.ruleExists(ruleId)) {
            throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
        }
        JELogger.trace("Setting rule front config in rule id = " + ruleId + " in project id = " + projectId);
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


    /*
     * deletes multiple rules in a project using their id.
     * returns nothing if rules were deleted successfully
     * if some rules were not deleted, throws exception with map [ key: rule that was not deleted , value : cause of the deletion failure ]
     */
    public void deleteRules(String projectId, List<String> ruleIds) throws ProjectNotFoundException, RuleDeletionException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        HashMap<String, String> undeletedRules = new HashMap<String, String>();
        JELogger.trace(" Deleting list of rules = " + ruleIds + " In project id = " + projectId);
        for (String ruleId : ruleIds) {
            if (project.ruleExists(ruleId)) {
                try {
                    JELogger.trace(getClass(), "deleting rule [id : " + ruleId + ")");
                    if (project.getRule(ruleId) instanceof UserDefinedRule) {
                        UserDefinedRule rule = (UserDefinedRule) project.getRule(ruleId);
                        if (rule.getSubRules() != null) {
                            for (String subRuleId : rule.getSubRules()) {
                                JERunnerAPIHandler.deleteRule(projectId, subRuleId);
                            }
                        }
                    } else {
                        JERunnerAPIHandler.deleteRule(projectId, ruleId);

                    }
                    project.deleteRule(ruleId);
                } catch (Exception e) {
                    undeletedRules.put(ruleId, e.getMessage());
                }

            } else {
                undeletedRules.put(ruleId, RuleBuilderErrors.RuleNotFound);

            }
        }

        if (!undeletedRules.isEmpty()) {
            throw new RuleDeletionException("Failed to delete the following rules : " + undeletedRules);
        }


    }

    /*
     * build rule : create drl + check for compilation errors
     */
    public void buildRule(String projectId, String ruleId) throws ProjectNotFoundException, RuleNotFoundException,
            RuleBuildFailedException, JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        } else if (!project.ruleExists(ruleId)) {
            throw new RuleNotFoundException(RuleBuilderErrors.RuleNotFound);
        }
        JELogger.trace(getClass(), "building rule [id : " + ruleId + ")" + " in project id = " + projectId);
        cleanUpRule(project, ruleId);
        RuleBuilder.buildRule(project.getRule(ruleId), project.getConfigurationPath());

    }

}
