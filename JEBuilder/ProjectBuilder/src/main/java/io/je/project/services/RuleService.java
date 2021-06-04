package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.rulebuilder.components.*;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.rulebuilder.models.ScriptRuleModel;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.files.JEFileUtils;
import io.je.utilities.logger.JELogger;
import io.je.utilities.ruleutils.RuleIdManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
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
            throws ProjectNotFoundException, RuleAlreadyExistsException, RuleNotAddedException, ConfigException {
    	ConfigurationService.checkConfig();
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

    public void deleteRule(String projectId, String ruleId) throws ProjectNotFoundException, RuleNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException, ConfigException {
    	ConfigurationService.checkConfig();
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JELogger.trace(getClass(), "[projectId = " + projectId + "] [ruleId = " + ruleId + "]" + JEMessages.DELETING_RULE);
        if (project.getRule(ruleId) instanceof UserDefinedRule) {
            UserDefinedRule rule = (UserDefinedRule) project.getRule(ruleId);
            if (rule.getSubRules() != null) {
                for (String subRuleId : rule.getSubRules()) {

                    JERunnerAPIHandler.deleteRule(projectId, subRuleId);

                }
            }
            removeAllRuleBlockNames(projectId,ruleId);
        } else {
            JERunnerAPIHandler.deleteRule(projectId, ruleId);

        }
        project.deleteRule(ruleId);

    }

    /*
     * update rule : update rule attributes
     */

    public void updateRule(String projectId, RuleModel ruleModel)
            throws ProjectNotFoundException, ConfigException {
    	ConfigurationService.checkConfig();
    	JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        UserDefinedRule ruleToUpdate = (UserDefinedRule) project.getRule(ruleModel.getRuleId());
        ruleToUpdate.setJeObjectLastUpdate(LocalDateTime.now());

        JELogger.trace(getClass(), "[projectId = " + projectId + "] [ruleId = " + ruleModel.getRuleId() + "]" + JEMessages.DELETING_RULE);
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
    public String addBlockToRule(BlockModel blockModel) throws AddRuleBlockException, ProjectNotFoundException,
            RuleNotFoundException, DataDefinitionUnreachableException, JERunnerErrorException, AddClassException,
            ClassLoadException, IOException, InterruptedException, ExecutionException, ConfigException {
    	ConfigurationService.checkConfig();
        if (blockModel.getProjectId() == null) {
            throw new AddRuleBlockException(JEMessages.BLOCK_PROJECT_ID_NULL);
        }

        if (blockModel.getRuleId() == null) {
            throw new AddRuleBlockException(JEMessages.BLOCK_RULE_ID_NULL);
        }

        JEProject project = ProjectService.getProjectById(blockModel.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.ruleExists(blockModel.getRuleId())) {
            JELogger.error(getClass(), JEMessages.RULE_NOT_FOUND + " [ " + blockModel.getRuleId() + "]");
            throw new RuleNotFoundException(blockModel.getProjectId(), blockModel.getRuleId());
        }
        verifyBlockFormatIsValid(blockModel);
        
        
        
        UserDefinedRule rule = (UserDefinedRule) project.getRule(blockModel.getRuleId());
        
        //check if block already exists
        boolean blockExists = rule.containsBlock(blockModel.getBlockId());
        if(blockExists)
        {
        	throw new AddRuleBlockException("A block with this id already exists");
        }
       
         JELogger.trace(getClass(), JEMessages.ADDING_BLOCK + blockModel.getBlockName() + " to rule [id : " + blockModel.getRuleId() + "]");

         String generatedBlockName = project.generateUniqueBlockName(blockModel.getBlockName());
         blockModel.setBlockName(generatedBlockName);

        //create block
        Block block = BlockGenerator.createBlock(blockModel);
        block.setInputBlockIds(blockModel.getInputBlocksIds());
        block.setOutputBlockIds(blockModel.getOutputBlocksIds()); 
        
        //add block to rule
        rule.addBlock(block);
        rule.setJeObjectLastUpdate(LocalDateTime.now());
        
        
        // retrieve topic names from getter blocks
        if (blockModel.getOperationId() == 4002 && blockModel.getBlockConfiguration() != null
                && blockModel.getBlockConfiguration().getClassId() != null) {
          
            String classId =  blockModel.getBlockConfiguration().getClassId();
            String workspaceId=blockModel.getBlockConfiguration().getWorkspaceId();
            
         
          
        	   rule.addTopic(classId);
           
            classService.addClass(workspaceId,classId);
        }
        
        project.addBlockName(blockModel.getBlockId(), generatedBlockName);
        project.setBuilt(false);
        return generatedBlockName;


    }
    
    /*
     * update rule : update block in rule
     */
    public void updateBlockInRule(BlockModel blockModel) throws AddRuleBlockException, ProjectNotFoundException,
            RuleNotFoundException, DataDefinitionUnreachableException, JERunnerErrorException, AddClassException,
            ClassLoadException, IOException, InterruptedException, ExecutionException, ConfigException, RuleBlockNotFoundException {
    	ConfigurationService.checkConfig();
        
    	//check project id is not null
    	if (blockModel.getProjectId() == null) {
            throw new AddRuleBlockException(JEMessages.BLOCK_PROJECT_ID_NULL);
        }

    	
    	//check rule is is not null
        if (blockModel.getRuleId() == null) {
            throw new AddRuleBlockException(JEMessages.BLOCK_RULE_ID_NULL);
        }

        JEProject project = ProjectService.getProjectById(blockModel.getProjectId());
       
        //check project exists
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        //check rule exists    
        } else if (!project.ruleExists(blockModel.getRuleId())) {
            JELogger.error(getClass(), JEMessages.RULE_NOT_FOUND + " [ " + blockModel.getRuleId() + "]");
            throw new RuleNotFoundException(blockModel.getProjectId(), blockModel.getRuleId());        }
        
        //check block exists
        UserDefinedRule rule = (UserDefinedRule) project.getRule(blockModel.getRuleId());
        boolean blockExists = rule.containsBlock(blockModel.getBlockId());

         if(!blockExists)
        {
            throw new RuleBlockNotFoundException(JEMessages.RULE_NOT_FOUND + " [ " + blockModel.getRuleId() + "]");

        }
        verifyBlockFormatIsValid(blockModel);

        
        
        //check if block already exists
        Block oldblock = rule.getBlocks().getBlock(blockModel.getBlockId());

        JELogger.trace(getClass(), JEMessages.UPDATING_BLOCK + blockModel.getBlockName() + " in rule [id : " + blockModel.getRuleId() + "]");


        //create block
        Block block = BlockGenerator.createBlock(blockModel);
        block.setInputBlockIds(blockModel.getInputBlocksIds());
        block.setOutputBlockIds(blockModel.getOutputBlocksIds()); 
        
        //check block name is valid
        if(!oldblock.getBlockName().equals(block.getBlockName()) )
        {
        	if(project.blockNameExists(block.getBlockName()))
        	{
        		throw new AddRuleBlockException("Block name can't be updated because it already exists");
        	}
        	else {
        		project.removeBlockName(block.getJobEngineElementID());
        		project.addBlockName(blockModel.getBlockId(), block.getBlockName());
        	}
        }
        
        //add block to rule
        rule.addBlock(block);
        rule.setJeObjectLastUpdate(LocalDateTime.now());
        
        
        // retrieve topic names from getter blocks
        if (blockModel.getOperationId() == 4002 && blockModel.getBlockConfiguration() != null
                && blockModel.getBlockConfiguration().getClassId() != null) {
          
            String classId =  blockModel.getBlockConfiguration().getClassId();
            String workspaceId=blockModel.getBlockConfiguration().getWorkspaceId();

        	 rule.updateTopic(((AttributeGetterBlock)oldblock).getClassId(), classId);

            classService.addClass(workspaceId,classId);
        }
        project.setBuilt(false);


    }

    /*
     * delete block
     */

    public void deleteBlock(String projectId, String ruleId, String blockId) throws ProjectNotFoundException,
            RuleNotFoundException, RuleBlockNotFoundException, ConfigException {
    	ConfigurationService.checkConfig();
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.ruleExists(ruleId)) {
        	throw new RuleNotFoundException(projectId, ruleId);
        	}
        JELogger.trace(getClass(), JEMessages.DELETING_BLOCK + blockId + " in rule [id : " + ruleId + ") in project id = " + projectId);
        project.deleteRuleBlock(ruleId, blockId);
        project.removeBlockName(blockId);
    }

    @Async
    public CompletableFuture<Void> buildRules(String projectId)
            throws ProjectNotFoundException, RuleBuildFailedException, JERunnerErrorException, IOException,
            RuleNotFoundException, InterruptedException, ExecutionException, ConfigException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        JELogger.trace("[projectId = " + projectId + "]" + JEMessages.BUILDING_RULES);
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

        String rulePrefix = RuleIdManager.generateSubRulePrefix(ruleId);
        JEFileUtils.deleteFilesInPathByPrefix(project.getConfigurationPath(), rulePrefix);
        JELogger.trace(JEMessages.DELETING_RULE_RUNNER);
       if( project.getRule(ruleId) instanceof UserDefinedRule)
       {
    	   UserDefinedRule rule = (UserDefinedRule ) project.getRule(ruleId);
    	   for (String subRuleId : rule.getSubRules())
    	   {
    		   JERunnerAPIHandler.deleteRule(project.getProjectId(), subRuleId);
    	   }
       }

    }




    /*
     * Retrieve list of all rules that exist in a project.
     */

    public Collection<RuleModel> getAllRules(String projectId) throws ProjectNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        List<RuleModel> rules = new ArrayList<>();
        JELogger.trace("[projectId = " + projectId + "]" + JEMessages.LOADING_RULES);
        for (JERule rule : project.getRules().values()) {

        	   rules.add(new RuleModel(rule));

        }
        return rules;
    }

    public RuleModel getRule(String projectId, String ruleId) throws ProjectNotFoundException, RuleNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException("["+ projectId + "] "+JEMessages.PROJECT_NOT_FOUND );

        } else if (!project.ruleExists(ruleId)) {
        	throw new RuleNotFoundException(projectId, ruleId);        }
        JELogger.trace("[projectId = " + projectId + "] [ruleId = " + ruleId + "]" + JEMessages.LOADING_RULE);
        return new RuleModel(project.getRules().get(ruleId));
    }

    /*
     * add scripted rule
     */

    public void addScriptedRule(String projectId, ScriptRuleModel ruleModel)
            throws ProjectNotFoundException, RuleAlreadyExistsException, ConfigException {
    	ConfigurationService.checkConfig();
        ScriptedRule rule = new ScriptedRule(projectId, ruleModel.getRuleId(), ruleModel.getScript(),
                ruleModel.getRuleName());
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JELogger.trace("[projectId = " + projectId+"]" + JEMessages.ADDING_SCRIPTED_RULE );
        project.addRule(rule);

    }

    /*
     * update scripted rule
     *
     */

    public void updateScriptedRule(String projectId, ScriptRuleModel ruleModel)
            throws ProjectNotFoundException, RuleNotFoundException, ConfigException {
    	ConfigurationService.checkConfig();
        ScriptedRule rule = new ScriptedRule(projectId, ruleModel.getRuleId(), ruleModel.getScript(),
                ruleModel.getRuleName());
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JELogger.trace("[projectId = " + projectId+"]" + JEMessages.UPDATING_SCRIPTED_RULE );
        project.updateRule(rule);

    }

    public void saveRuleFrontConfig(String projectId, String ruleId, String config)
            throws ProjectNotFoundException, RuleNotFoundException {
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.ruleExists(ruleId)) {
        	throw new RuleNotFoundException(projectId, ruleId);        }
        JELogger.debug("[projectId = " + projectId + "] [ruleId = " + ruleId + "]"+JEMessages.FRONT_CONFIG);
        project.getRule(ruleId).setRuleFrontConfig(config);

    }

    public void verifyBlockFormatIsValid(BlockModel blockModel) throws AddRuleBlockException {
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
     * deletes multiple rules in a project using their id.
     * returns nothing if rules were deleted successfully
     * if some rules were not deleted, throws exception with map [ key: rule that was not deleted , value : cause of the deletion failure ]
     */
    public void deleteRules(String projectId, List<String> ruleIds) throws ProjectNotFoundException, RuleDeletionException, ConfigException {
    	ConfigurationService.checkConfig();
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        HashMap<String, String> undeletedRules = new HashMap<String, String>();
        JELogger.trace(JEMessages.DELETING_RULES + ruleIds + " In project id = " + projectId);
        for (String ruleId : ruleIds) {
            if (project.ruleExists(ruleId)) {
                try {
                    JELogger.trace(getClass(), "[projectId = " + projectId + "] [ruleId = " + ruleId + "]"+JEMessages.DELETING_RULE);
                    if (project.getRule(ruleId) instanceof UserDefinedRule) {
                        UserDefinedRule rule = (UserDefinedRule) project.getRule(ruleId);
                        if (rule.getSubRules() != null) {
                            for (String subRuleId : rule.getSubRules()) {
                                JERunnerAPIHandler.deleteRule(projectId, subRuleId);
                            }
                            removeAllRuleBlockNames(projectId,ruleId);
                        }
                    } else {
                        JERunnerAPIHandler.deleteRule(projectId, ruleId);

                    }
                    
                    project.deleteRule(ruleId);
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
    public void buildRule(String projectId, String ruleId) throws ProjectNotFoundException, RuleNotFoundException,
            RuleBuildFailedException, JERunnerErrorException, IOException, InterruptedException, ExecutionException, ConfigException {
    	ConfigurationService.checkConfig();
        JEProject project = ProjectService.getProjectById(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        } else if (!project.ruleExists(ruleId)) {
        	throw new RuleNotFoundException(projectId, ruleId);        }
        JELogger.trace(getClass(), "[projectId = " + projectId + "] [ruleId = " + ruleId + "]"+JEMessages.BUILDING_RULE);
        cleanUpRule(project, ruleId);
        RuleBuilder.buildRule(project.getRule(ruleId), project.getConfigurationPath());
        
    }
    
    private void removeAllRuleBlockNames(String projectId,String ruleId)
    {
    	JEProject project = ProjectService.getProjectById(projectId);
    	Enumeration<String> blockIds =((UserDefinedRule) project.getRule(ruleId)).getBlocks().getAllBlockIds();
    	while(blockIds.hasMoreElements())
    	{
    		project.removeBlockName(blockIds.nextElement());
    	}
    }

}
