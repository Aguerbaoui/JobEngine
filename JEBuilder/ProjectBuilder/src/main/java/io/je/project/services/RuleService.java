package io.je.project.services;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.OutgoingCallerId;
import io.je.project.beans.JEProject;
import io.je.project.config.LicenseProperties;
import io.je.project.repository.RuleRepository;
import io.je.rulebuilder.components.*;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.rulebuilder.models.ScriptRuleModel;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.beans.Status;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.ruleutils.IdManager;
import io.je.utilities.ruleutils.OperationStatusDetails;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.impl.util.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static io.je.utilities.constants.WorkflowConstants.*;

/*
 * Service class to handle business logic for rules
 */
@Service
@Lazy
public class RuleService {

    private static final String DEFAULT_DELETE_CONSTANT = "DELETED";
    private static final LogSubModule RULE = LogSubModule.RULE;
    private static final LogCategory CATEGORY = LogCategory.DESIGN_MODE;
    @Autowired
    @Lazy
    RuleRepository ruleRepository;
    @Autowired
    @Lazy
    ClassService classService;
    @Autowired
    @Lazy
    AsyncRuleService asyncRuleService;
    @Autowired
    @Lazy
    ProjectService projectService;

    /*
     * Add a rule to a project
     */

    /*
     * Update rule status
     * */
    public static void updateRuleStatus(JERule rule) {

        if (rule.isRunning() || rule.getStatus() == Status.RUNNING_NOT_UP_TO_DATE) {
            if (rule.isBuilt()) {
                rule.setStatus(Status.RUNNING);
            } else {
                rule.setStatus(Status.RUNNING_NOT_UP_TO_DATE);
            }
        } else {

            if (rule.containsErrors()) {
                rule.setStatus(Status.ERROR);
            } else if (rule.isCompiled()) {
                rule.setStatus(Status.STOPPED);
            } else { // FIXME case status equals Status.ERROR
                rule.setStatus(Status.NOT_BUILT);
            }

        }

    }

    /*
     * delete rule from a project
     */

    public void createRule(String projectId, RuleModel ruleModel) throws ProjectNotFoundException,
            RuleAlreadyExistsException, RuleNotAddedException, LicenseNotActiveException, ProjectLoadException {
        //LicenseProperties.checkLicenseIsActive();
        JEProject project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        if (ruleModel.getRuleId() == null) {
            throw new RuleNotAddedException(JEMessages.RULE_ID_NULL);
        }

        if (ruleModel.getRuleName() == null) {
            throw new RuleNotAddedException(JEMessages.RULE_NAME_NULL);
        }

        JELogger.control(JEMessages.ADDING_RULE + ruleModel.getRuleName() + "..", CATEGORY, projectId, RULE,
                ruleModel.getRuleId());
        UserDefinedRule rule = new UserDefinedRule();
        rule.setJobEngineElementID(ruleModel.getRuleId());
        rule.setJobEngineProjectID(projectId);
        rule.setJobEngineElementName(ruleModel.getRuleName());
        rule.setJobEngineProjectName(project.getProjectName());
        rule.setDescription(ruleModel.getDescription());
        rule.setJeObjectCreationDate(Instant.now());
        rule.setJeObjectLastUpdate(Instant.now());
        RuleParameters ruleParameters = new RuleParameters();
        rule.setJeObjectCreatedBy(ruleModel.getCreatedBy());
        rule.setJeObjectModifiedBy(ruleModel.getModifiedBy());
        ruleParameters.setSalience(ruleModel.getSalience());
        ruleParameters.setTimer(ruleModel.getTimer());
        ruleParameters.setEnabled(String.valueOf(ruleModel.getEnabled()));
        ruleParameters.setDateEffective(ruleModel.getDateEffective());
        ruleParameters.setDateExpires(ruleModel.getDateExpires());
        rule.setRuleParameters(ruleParameters);

        rule.setStatus(Status.NOT_BUILT);

        project.addRule(rule);
        ruleRepository.save(rule);
    }

    /*
     * update rule : update rule attributes
     */

    public void deleteRule(String projectId, String ruleId)
            throws ProjectNotFoundException, RuleNotFoundException, JERunnerErrorException, LicenseNotActiveException, ProjectLoadException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = getProject(projectId);
        JELogger.info(JEMessages.DELETING_RULE, LogCategory.DESIGN_MODE, projectId, LogSubModule.RULE,
                ruleId);
        if (project.getRule(ruleId) instanceof UserDefinedRule) {
            UserDefinedRule rule = getRule(project, ruleId);
            if (rule.getSubRules() != null) {
                for (String subRuleId : rule.getSubRules()) {

                    JERunnerAPIHandler.deleteRule(projectId, subRuleId);
                    JELogger.control("[project = " + project.getProjectName() + "] [rule = "
                                    + rule.getJobEngineElementName() + "] " + JEMessages.DELETING_RULE, CATEGORY, projectId,
                            RULE, ruleId);
                }
            }
            removeAllRuleBlockNames(projectId, ruleId);
        } else {
            JERunnerAPIHandler.deleteRule(projectId, ruleId);

        }
        project.deleteRule(ruleId);
        project.getRuleEngine()
                .remove(ruleId);
        ruleRepository.deleteById(ruleId);
        JELogger.info(JEMessages.RULE_DELETED, LogCategory.DESIGN_MODE, projectId, LogSubModule.RULE,
                ruleId);
    }

    public void updateRule(String projectId, RuleModel ruleModel)
            throws ProjectNotFoundException, LicenseNotActiveException, ProjectLoadException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = getProject(projectId);
        UserDefinedRule ruleToUpdate = (UserDefinedRule) project.getRule(ruleModel.getRuleId());
        ruleToUpdate.setJeObjectLastUpdate(Instant.now());
        updateRuleStatus(ruleToUpdate);
        JELogger.debug("[project = " + project.getProjectName() + "] [rule = "
                + project.getRules()
                .get(ruleModel.getRuleId())
                .getJobEngineElementName() + "]"
                + JEMessages.UPDATING_RULE, CATEGORY, projectId, RULE, ruleModel.getRuleId());
        // update rule name
        if (ruleModel.getRuleName() != null && !ruleModel.getRuleName()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.setJobEngineElementName(ruleModel.getRuleName());
        } else if (ruleModel.getRuleName() != null && ruleModel.getRuleName()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.setJobEngineElementName(null);

        }

        // update createdBy
        if (ruleModel.getCreatedBy() != null && !ruleModel.getCreatedBy()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.setJeObjectCreatedBy(ruleModel.getCreatedBy());
        } else if (ruleModel.getCreatedBy() != null && ruleModel.getCreatedBy()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.setJeObjectCreatedBy(null);

        }

        // update modifiedBy
        if (ruleModel.getModifiedBy() != null && !ruleModel.getModifiedBy()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.setJeObjectModifiedBy(ruleModel.getModifiedBy());
        } else if (ruleModel.getModifiedBy() != null && ruleModel.getModifiedBy()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.setJeObjectModifiedBy(null);

        }

        // update rule description
        if (ruleModel.getDescription() != null && !ruleModel.getDescription()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.setDescription(ruleModel.getDescription());
        } else if (ruleModel.getDescription() != null && ruleModel.getDescription()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.setDescription(null);

        }

        // update Salience
        if (ruleModel.getSalience() != null && !ruleModel.getSalience()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters()
                    .setSalience(ruleModel.getSalience());
        } else if (ruleModel.getSalience() != null && ruleModel.getSalience()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters()
                    .setSalience(null);

        }

        // update DateEffective
        if (ruleModel.getDateEffective() != null && !ruleModel.getDateEffective()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters()
                    .setDateEffective(ruleModel.getDateEffective());
        } else if (ruleModel.getDateEffective() != null
                && ruleModel.getDateEffective()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters()
                    .setDateEffective(null);

        }

        // update DateExpires
        if (ruleModel.getDateExpires() != null && !ruleModel.getDateExpires()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters()
                    .setDateExpires(ruleModel.getDateExpires());
        } else if (ruleModel.getDateExpires() != null && ruleModel.getDateExpires()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters()
                    .setDateExpires(null);

        }

        // update Timer
        if (ruleModel.getTimer() != null && !ruleModel.getTimer()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters()
                    .setTimer(ruleModel.getTimer());
        } else if (ruleModel.getTimer() != null && ruleModel.getTimer()
                .equals(DEFAULT_DELETE_CONSTANT)) {
            ruleToUpdate.getRuleParameters()
                    .setTimer(null);

        }

        // update Enabled
        ruleToUpdate.setEnabled(ruleModel.getEnabled());
        if (ruleToUpdate.isEnabled()) {
            ruleToUpdate.getRuleParameters()
                    .setEnabled(String.valueOf(ruleModel.getEnabled()));

        } else {
            ruleToUpdate.getRuleParameters()
                    .setEnabled(null);
        }

        ruleToUpdate.setBuilt(false);
        project.setBuilt(false);
        ruleRepository.save(ruleToUpdate);
    }

    /*
     * update rule : add block to rule
     */
    public String addBlockToRule(BlockModel blockModel) throws AddRuleBlockException, ProjectNotFoundException,
            RuleNotFoundException, AddClassException,
            ClassLoadException, LicenseNotActiveException, ProjectLoadException {
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
        rule.setJeObjectLastUpdate(Instant.now());

        // retrieve topic names from getter blocks
        if (blockModel.getOperationId() == 4002 && blockModel.getBlockConfiguration() != null
                && blockModel.getBlockConfiguration()
                .get(AttributesMapping.CLASSID) != null) {

            String classId = (String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.CLASSID);
            String workspaceId = (String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.WORKSPACEID);

            // rule.addTopic(classId);

            classService.loadClassFromDataModel(workspaceId, classId, true);
        }

        project.addBlockName(blockModel.getBlockId(), generatedBlockName);
        project.setBuilt(false);
        rule.setCompiled(false);
        updateRuleStatus(rule);
        return generatedBlockName;

    }

    /*
     * delete block
     */

    /*
     * update rule : update block in rule
     */
    public void updateBlockInRule(BlockModel blockModel) throws AddRuleBlockException, ProjectNotFoundException,
            RuleNotFoundException, AddClassException, ClassLoadException,
            RuleBlockNotFoundException, LicenseNotActiveException, ProjectLoadException {

        LicenseProperties.checkLicenseIsActive();
        // check project id is not null
        if (blockModel.getProjectId() == null) {
            throw new AddRuleBlockException(JEMessages.BLOCK_PROJECT_ID_NULL);
        }

        // check rule id is not null
        if (blockModel.getRuleId() == null) {
            throw new AddRuleBlockException(JEMessages.BLOCK_RULE_ID_NULL);
        }

        JEProject project = getProject(blockModel.getProjectId());
        UserDefinedRule rule = getRule(project, blockModel.getRuleId());
        boolean blockExists = rule.containsBlock(blockModel.getBlockId());

        if (!blockExists) {
            JELogger.error(JEMessages.BLOCK_NOT_FOUND + " [ " + blockModel.getBlockId() + "]", CATEGORY,
                    blockModel.getProjectId(), RULE, blockModel.getBlockId());
            throw new RuleBlockNotFoundException("Block not found" + " [ " + blockModel.getBlockId() + "]");

        }
        verifyBlockFormatIsValid(blockModel);

        // check if block already exists
        Block oldblock = rule.getBlocks()
                .getBlock(blockModel.getBlockId());

        JELogger.debug(JEMessages.UPDATING_BLOCK + blockModel.getBlockName() + " in rule [id : "
                + blockModel.getRuleId() + "]", CATEGORY, blockModel.getProjectId(), RULE, blockModel.getBlockId());

        // create block
        Block block = BlockGenerator.createBlock(blockModel);

        // check block name is valid
        if (!oldblock.getBlockName()
                .equals(block.getBlockName())) {
            if (project.blockNameExists(block.getBlockName())) {
                throw new AddRuleBlockException(JEMessages.BLOCK_NAME_EXISTS);
            }
            project.removeBlockName(block.getJobEngineElementID());
            project.addBlockName(blockModel.getBlockId(), block.getBlockName());

        }

        // add block to rule
        rule.addBlock(block);
        rule.setJeObjectLastUpdate(Instant.now());

        // retrieve topic names from getter blocks
        if (blockModel.getOperationId() == 4002 && blockModel.getBlockConfiguration() != null
                && blockModel.getBlockConfiguration()
                .get(AttributesMapping.CLASSID) != null) {

            String classId = (String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.CLASSID);
            String workspaceId = (String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.WORKSPACEID);

            // rule.updateTopic(((AttributeGetterBlock) oldblock).getClassId(), classId);

            classService.loadClassFromDataModel(workspaceId, classId, true);
        }
        project.setBuilt(false);
        rule.setCompiled(false);
        updateRuleStatus(rule);
        ruleRepository.save(rule);

    }

    public void deleteBlock(String projectId, String ruleId, String blockId) throws ProjectNotFoundException,
            RuleNotFoundException, LicenseNotActiveException, ProjectLoadException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = getProject(projectId);
        if (!project.ruleExists(ruleId)) {
            throw new RuleNotFoundException(projectId, ruleId);
        }
        JELogger.debug(JEMessages.DELETING_BLOCK + blockId + " in rule ["
                        + project.getRule(ruleId)
                        .getJobEngineElementName() + "] in project  = " + project.getProjectName(),
                CATEGORY, projectId, RULE, ruleId);
        project.deleteRuleBlock(ruleId, blockId);
        project.removeBlockName(blockId);
        project.getRule(ruleId)
                .setCompiled(false);
        updateRuleStatus(project.getRule(ruleId));

        ruleRepository.save(project.getRule(ruleId));
    }

    /*
     * @Async public CompletableFuture<List<OperationStatusDetails>>
     * compileAllProjectRules(String projectId) throws ProjectNotFoundException ,
     * LicenseNotActiveException { LicenseProperties.checkLicenseIsActive();
     *
     *
     * JELogger.debug("[project = " + project.getProjectName() + "]" +
     * JEMessages.BUILDING_RULES, CATEGORY, projectId, RULE, null); return
     * CompletableFuture.completedFuture(compileRules(projectId,ruleIds)); }
     */
    private void cleanUpRules(JEProject project) throws JERunnerErrorException {

        for (JERule rule : project.getRules()
                .values()) {
            cleanUpRule(project, rule.getJobEngineElementID());
        }

    }

    /*
     * Add Rule To Rule engine
     */
    public List<OperationStatusDetails> buildRules(String projectId)
            throws ProjectNotFoundException, JERunnerErrorException, ProjectLoadException, LicenseNotActiveException {
        List<OperationStatusDetails> result;
        JEProject project = getProject(projectId);
        JELogger.debug("[project = " + project.getProjectName() + "]" + JEMessages.BUILDING_RULES, CATEGORY, projectId,
                RULE, null);
        cleanUpRules(project);
        ArrayList<CompletableFuture<OperationStatusDetails>> ruleFuture = new ArrayList<>();

        for (Entry<String, JERule> entry : project.getRules()
                .entrySet()) {
            String ruleId = entry.getKey();
            if (entry.getValue()
                    .isEnabled() && !entry.getValue()
                    .isRunning()) {

                ruleFuture.add(asyncRuleService.buildRule(projectId, ruleId));

            }
        }
        //TODO: check if it improves speed
        result = ruleFuture.parallelStream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return result;
    }

    /*
     * Retrieve list of all rules that exist in a project.
     */

    private void cleanUpRule(JEProject project, String ruleId) throws JERunnerErrorException {

        String rulePrefix = IdManager.generateSubRulePrefix(ruleId);

        FileUtilities.deleteFilesInPathByPrefix(project.getConfigurationPath(), rulePrefix);

        JELogger.debug(JEMessages.DELETING_RULE_RUNNER, CATEGORY, project.getProjectId(), RULE, ruleId);

        if (project.getRule(ruleId) instanceof UserDefinedRule) {

            UserDefinedRule rule = (UserDefinedRule) project.getRule(ruleId);

            for (String subRuleId : rule.getSubRules()) {
                JERunnerAPIHandler.deleteRule(project.getProjectId(), subRuleId);
            }

        }

    }

    public Collection<RuleModel> getAllRules(String projectId)
            throws ProjectNotFoundException, LicenseNotActiveException, ProjectLoadException {
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

    /*
     * Get rule
     * */
    public RuleModel getRule(String projectId, String ruleId)
            throws ProjectNotFoundException, RuleNotFoundException, LicenseNotActiveException, ProjectLoadException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = getProject(projectId);
        if (!project.ruleExists(ruleId)) {
            throw new RuleNotFoundException(projectId, ruleId);
        }
        RuleService.updateRuleStatus(project.getRule(ruleId));
        JELogger.debug(
                "[project = " + project.getProjectName() + "] [rule = "
                        + project.getRules()
                        .get(ruleId)
                        .getJobEngineElementName() + "]" + JEMessages.LOADING_RULE,
                CATEGORY, project.getProjectId(), RULE, ruleId);
        return new RuleModel(project.getRule(ruleId));
    }

    /*
     * add scripted rule
     */
    public void addScriptedRule(String projectId, ScriptRuleModel ruleModel)
            throws ProjectNotFoundException, RuleAlreadyExistsException, LicenseNotActiveException, ProjectLoadException {
        LicenseProperties.checkLicenseIsActive();
        JEProject project = projectService.getProjectById(projectId);
        ScriptedRule rule = new ScriptedRule(projectId, ruleModel.getRuleId(), ruleModel.getScript(),
                ruleModel.getRuleName(), project.getProjectName());

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
            throws ProjectNotFoundException, RuleNotFoundException, ProjectLoadException, LicenseNotActiveException {
        JEProject project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        ScriptedRule rule = new ScriptedRule(projectId, ruleModel.getRuleId(), ruleModel.getScript(),
                ruleModel.getRuleName(), project.getProjectName());


        JELogger.debug("[projectId = " + projectId + "]" + JEMessages.UPDATING_SCRIPTED_RULE, CATEGORY, projectId, RULE,
                ruleModel.getRuleId());
        project.updateRule(rule);
        ruleRepository.save(rule);
    }

    /*
     * Save front config
     * */
    public void saveRuleFrontConfig(String projectId, String ruleId, String config)
            throws ProjectNotFoundException, RuleNotFoundException, LicenseNotActiveException, ProjectLoadException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = getProject(projectId);
        if (!project.ruleExists(ruleId)) {
            throw new RuleNotFoundException(projectId, ruleId);
        }

        JELogger.debug(
                "[project = " + project.getProjectName() + "] [rule = "
                        + project.getRules()
                        .get(ruleId)
                        .getJobEngineElementName() + "]" + JEMessages.FRONT_CONFIG,
                CATEGORY, projectId, RULE, ruleId);

        project.getRule(ruleId).setRuleFrontConfig(config);

        ruleRepository.save(project.getRule(ruleId));
    }

    /*
     * Check if block format is valid
     * */
    public void verifyBlockFormatIsValid(BlockModel blockModel)
            throws AddRuleBlockException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        // block Id can't be null
        if (blockModel == null || blockModel.getBlockId() == null || blockModel.getBlockId()
                .isEmpty()) {
            throw new AddRuleBlockException(JEMessages.BLOCK_ID_NULL);

        }

        if (blockModel.getBlockName() == null || blockModel.getBlockName()
                .isEmpty()) {
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
            throws ProjectNotFoundException, RuleDeletionException, LicenseNotActiveException, ProjectLoadException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = getProject(projectId);
        HashMap<String, String> undeletedRules = new HashMap<>();
        for (String ruleId : ruleIds) {
            if (project.ruleExists(ruleId)) {
                try {
                    JELogger.debug("[project = " + project.getProjectName() + "] [rule = "
                                    + project.getRules()
                                    .get(ruleId)
                                    .getJobEngineElementName() + "] " + JEMessages.DELETING_RULE,
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
                    LoggerUtils.logException(e);
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
    public void compileRule(String projectId, String ruleId)
            throws LicenseNotActiveException, InterruptedException, ExecutionException, RuleBuildFailedException {
        LicenseProperties.checkLicenseIsActive();

        OperationStatusDetails result = asyncRuleService.compileRule(projectId, ruleId, true).get();

        if (!result.isOperationSucceeded()) {
            throw new RuleBuildFailedException(result.getOperationError());
        }

    }

    /*
     * Delete all rule block names in project
     * */
    private void removeAllRuleBlockNames(String projectId, String ruleId) throws LicenseNotActiveException, ProjectNotFoundException, ProjectLoadException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = projectService.getProjectById(projectId);
        Enumeration<String> blockIds = ((UserDefinedRule) project.getRule(ruleId)).getBlocks()
                .getAllBlockIds();
        while (blockIds.hasMoreElements()) {
            project.removeBlockName(blockIds.nextElement());
        }
    }

    /*
     * Delete all rules in project
     * */
    public void deleteAll(String projectId) {
        ruleRepository.deleteByJobEngineProjectID(projectId);

    }

    /*
     * Get all rule beans in project
     * */
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
     * Get Project by id
     * */
    private JEProject getProject(String projectId) throws ProjectNotFoundException, ProjectLoadException, LicenseNotActiveException {
        JEProject project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        return project;
    }

    /*
     * Get rule
     * */
    private UserDefinedRule getRule(JEProject project, String ruleId) throws RuleNotFoundException {
        if (!project.ruleExists(ruleId)) {
            throw new RuleNotFoundException(project.getProjectId(), ruleId);
        }
        return (UserDefinedRule) project.getRule(ruleId);
    }

    /*
     * Update rules status
     * */
    public void updateRulesStatus(String projectId, boolean setRunning) throws ProjectNotFoundException, ProjectLoadException, LicenseNotActiveException {
        JEProject project = getProject(projectId);
        if (setRunning) {
            for (Entry<String, JERule> rule : project.getRules()
                    .entrySet()) {
                if (rule.getValue()
                        .isEnabled() && !rule.getValue()
                        .containsErrors()) {
                    try {
                        rule.getValue()
                                .setRunning(true);
                        RuleService.updateRuleStatus(rule.getValue());
                        ruleRepository.save(rule.getValue());
                    } catch (Exception e) {
                        LoggerUtils.logException(e);
                        JELogger.error("[rule = " + rule.getValue()
                                .getJobEngineElementName() + "]"
                                + JEMessages.STATUS_UPDATE_FAILED, CATEGORY, projectId, RULE, null);

                    }
                }
            }
        } else {
            for (Entry<String, JERule> rule : project.getRules()
                    .entrySet()) {
                try {
                    rule.getValue()
                            .setRunning(false);
                    RuleService.updateRuleStatus(rule.getValue());
                    ruleRepository.save(rule.getValue());
                } catch (Exception e) {
                    LoggerUtils.logException(e);
                    JELogger.error("[rule = " + rule.getValue()
                            .getJobEngineElementName() + "]"
                            + JEMessages.STATUS_UPDATE_FAILED, CATEGORY, projectId, RULE, null);

                }
            }

        }

    }

    /*
     * run a specific rule.
     */
    public void runRule(String projectId, String ruleId)
            throws LicenseNotActiveException, RuleBuildFailedException, InterruptedException, ExecutionException {
        //LicenseProperties.checkLicenseIsActive();
        OperationStatusDetails result = asyncRuleService.runRule(projectId, ruleId)
                .get();
        if (!result.isOperationSucceeded()) {
            throw new RuleBuildFailedException(result.getOperationError());
        }

    }

    /*
     * Rune list of rules in project
     * */
    public List<OperationStatusDetails> runRules(String projectId, List<String> ruleIds)
            throws LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        List<OperationStatusDetails> results;
        ArrayList<CompletableFuture<OperationStatusDetails>> ruleFuture = new ArrayList<>();

        for (String ruleId : ruleIds) {

            ruleFuture.add(asyncRuleService.runRule(projectId, ruleId));

        }
        results = ruleFuture.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return results;

    }

    /*
     * Stop rule
     * */
    public OperationStatusDetails stopRule(String projectId, String ruleId) {

        JEProject project = null;
        OperationStatusDetails result = new OperationStatusDetails(ruleId);

        UserDefinedRule rule = null;

        // Check rule exists
        try {
            project = getProject(projectId);
            rule = (UserDefinedRule) project.getRule(ruleId);
        } catch (Exception e) {
            LoggerUtils.logException(e);
            result.setOperationSucceeded(false);
            result.setOperationError(e.getMessage());
            return result;
        }

        try {
            result.setItemName(rule.getJobEngineElementName());

            if (rule.getStatus() != Status.STOPPED && rule.getStatus() != Status.STOPPING && rule.getStatus() != Status.NOT_BUILT) {

                rule.setStatus(Status.STOPPING);

                if (rule.isRunning()) {
                    if (rule.getSubRules() != null) {
                        for (String subRuleId : rule.getSubRules()) {

                            JEResponse response = JERunnerAPIHandler.deleteRule(projectId, subRuleId);

                            if (response.getCode() != ResponseCodes.CODE_OK) {
                                throw new JERunnerErrorException(JEMessages.FAILED_TO_DELETE_SUBRULE);
                            }
                        }
                    }
                    rule.setAdded(false);
                    rule.setRunning(false);
                }

                project.getRuleEngine().remove(ruleId);

                if (project.getRuleEngine()
                        .getBuiltRules()
                        .isEmpty()) {

                    JERunnerAPIHandler.shutDownRuleEngine(projectId);
                    project.getRuleEngine().setRunning(false);

                }

                RuleService.updateRuleStatus(rule);
                ruleRepository.save(rule);

            } else {
                result.setOperationSucceeded(false);
                result.setOperationError(JEMessages.RULE_ALREADY_STOPPED);
            }

            return result;

        } catch (JERunnerErrorException e) {
            LoggerUtils.logException(e);
            JELogger.error(JEMessages.FAILED_TO_STOP_RULE
                            + project.getRules()
                            .get(ruleId)
                            .getJobEngineElementName() + " : " + e.getMessage(),
                    CATEGORY, projectId, RULE, null);

            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.FAILED_TO_STOP_RULE + e.getMessage());

            return result;
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error("[rule = " + rule.getJobEngineElementName() + "] " + JEMessages.STATUS_UPDATE_FAILED + e.getMessage(),
                    CATEGORY, projectId, RULE, null);

            result.setOperationSucceeded(false);
            result.setOperationError(JEMessages.STATUS_UPDATE_FAILED + e.getMessage());

            return result;
        }

    }

    /*
     * @Async public List<CompletableFuture<OperationStatusDetails>>
     * compileRules1(String projectId, List<String> ruleIds) throws
     * LicenseNotActiveException, ProjectNotFoundException {
     * LicenseProperties.checkLicenseIsActive();
     *
     * System.out.println("----Compiling rules : "+ Instant.now() );
     * if(ruleIds==null) {
     *
     * ruleIds = Collections.list(getProject(projectId).getRules().keys()); }
     *
     * ArrayList<CompletableFuture<OperationStatusDetails>> ruleFuture = new
     * ArrayList<>(); for (String ruleId : ruleIds) {
     * ruleFuture.add(asyncRuleService.compileRule(projectId, ruleId, true));
     *
     * }
     *
     *
     * return ruleFuture;
     *
     * }
     */

    /*
     * Stop list of rules
     * */
    public List<OperationStatusDetails> stopRules(String projectId, List<String> ruleIds)
            throws ProjectNotFoundException, LicenseNotActiveException, ProjectLoadException {
        LicenseProperties.checkLicenseIsActive();

        if (ruleIds == null) {

            ruleIds = Collections.list(getProject(projectId).getRules()
                    .keys());
        }

        List<OperationStatusDetails> results = new ArrayList<>();
        for (String ruleId : ruleIds) {

            results.add(stopRule(projectId, ruleId));

        }

        return results;

    }

    /*
     * Compile list of rules
     * */
    public CompletableFuture<List<OperationStatusDetails>> compileRules(String projectId, List<String> ruleIds)
            throws LicenseNotActiveException, ProjectNotFoundException, ProjectLoadException {
        LicenseProperties.checkLicenseIsActive();
        List<OperationStatusDetails> results;

        if (ruleIds == null) {

            ruleIds = Collections.list(getProject(projectId).getRules()
                    .keys());
        }

        ArrayList<CompletableFuture<OperationStatusDetails>> ruleFuture = new ArrayList<>();
        for (String ruleId : ruleIds) {
            ruleFuture.add(asyncRuleService.compileRule(projectId, ruleId, true));

        }
        results = ruleFuture.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return CompletableFuture.completedFuture(results);

    }

	/*public JERule getJERule(String projectId, String ruleId)
			throws ProjectNotFoundException, RuleNotFoundException, LicenseNotActiveException, ProjectLoadException {
		LicenseProperties.checkLicenseIsActive();

		JEProject project = getProject(projectId);
		if (!project.ruleExists(ruleId)) {
			JELogger.error("[project = " + project.getProjectName() + "] [rule = "
					+ project.getRules().get(ruleId).getJobEngineElementName() + "]" + JEMessages.RULE_NOT_FOUND,
					CATEGORY, project.getProjectId(), RULE, ruleId);
			throw new RuleNotFoundException(projectId, ruleId);
		}
		JELogger.debug(
				"[project = " + project.getProjectName() + "] [rule = "
						+ project.getRules().get(ruleId).getJobEngineElementName() + "]" + JEMessages.LOADING_RULE,
				CATEGORY, project.getProjectId(), RULE, ruleId);
		return project.getRule(ruleId);
	}*/

    /*
     * Save rule
     * */
    public void saveRule(JERule rule) {
        ruleRepository.save(rule);

    }

    /*
     * Clear all rule data
     * */
    public void cleanUpHouse() {
        ruleRepository.deleteAll();
    }

    /**
     * Temporary method
     *
     * @param accountSID account SID from twilio platform
     * @param token      token from twilio platform
     * @return List<String> of users
     */
    public List<HashMap<String, String>> getTwilioVerifiedUsers(String accountSID, String token) {
        Twilio.init(accountSID, token);
        ResourceSet<OutgoingCallerId> outgoingCallerIds =
                OutgoingCallerId.reader()
                        .limit(100)
                        .read();
        List<HashMap<String, String>> users = new ArrayList<>();
        for (OutgoingCallerId record : outgoingCallerIds) {
            HashMap<String, String> user = new HashMap<>();
            user.put("name", record.getFriendlyName());
            user.put("phoneNumber", String.valueOf(record.getPhoneNumber()));
            users.add(user);
        }
        return users;
    }
    public String getSMSEagleContacts(Map<String, String> smsEagle) {
        try {
            HttpURLConnection conn;
            BufferedReader reader;
            String jsonPrettyPrintString;
            String line;
            String result = "";
            String baseUrl = smsEagle.get(SMS_URI) + "/http_api/contact_read?access_token=" + smsEagle.get("accountToken") + "&responsetype=xml";
            if (smsEagle.get("accountSID") != null) {
                baseUrl = smsEagle.get(SMS_URI) + "/http_api/contact_read?login=" + smsEagle.get("accountSID") + "&pass=" + smsEagle.get("accountToken") + "&responsetype=xml" ;
            }
            else {
                baseUrl = smsEagle.get(SMS_URI) + "/http_api/contact_read?access_token=" + smsEagle.get("accountToken") + "&responsetype=xml" ;
            }
            try {
                URL url = new URL(baseUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                reader.close();
                conn.disconnect();

                JSONObject xmlJSONObj = XML.toJSONObject(result);
                jsonPrettyPrintString = xmlJSONObj.toString();
                System.out.println(jsonPrettyPrintString);

            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return jsonPrettyPrintString;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    public String getSMSEagleGroups(Map<String, String> smsEagle) {
        try {
            HttpURLConnection conn;
            BufferedReader reader;
            String line;
            String result = "";
            String jsonPrettyPrintString;
            String  baseUrl;
            if (smsEagle.get(TWILIO_ACCOUNT_SID) != null) {
                baseUrl = smsEagle.get(SMS_URI) + "/http_api/group_read?login=" + smsEagle.get(TWILIO_ACCOUNT_SID) + "&pass=" + smsEagle.get(TWILIO_ACCOUNT_TOKEN) + "&responsetype=xml" ;
            }
            else {
                baseUrl = smsEagle.get(SMS_URI) + "/http_api/group_read?access_token=" + smsEagle.get(TWILIO_ACCOUNT_TOKEN) + "&responsetype=xml" ;
            }
            try {
                URL url = new URL(baseUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                reader.close();
                conn.disconnect();

                JSONObject xmlJSONObj = XML.toJSONObject(result);
                jsonPrettyPrintString = xmlJSONObj.toString();
                System.out.println(jsonPrettyPrintString);

            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return jsonPrettyPrintString;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * public List<OperationStatusDetails> compileRules3(String projectId,
     * List<String> ruleIds) throws LicenseNotActiveException,
     * ProjectNotFoundException { LicenseProperties.checkLicenseIsActive();
     * List<OperationStatusDetails> results ;
     *
     * System.out.println("----Compiling rules : "+ Instant.now() );
     * if(ruleIds==null) {
     *
     * ruleIds = Collections.list(getProject(projectId).getRules().keys()); }
     *
     * ArrayList<CompletableFuture<OperationStatusDetails>> ruleFuture = new
     * ArrayList<>(); for (String ruleId : ruleIds) {
     * ruleFuture.add(asyncRuleService.compileRule(projectId, ruleId, true));
     *
     * } results =
     * ruleFuture.stream().map(CompletableFuture::join).filter(Objects::nonNull)
     * .collect(Collectors.toList());
     *
     * return results;
     *
     * }
     */
}
