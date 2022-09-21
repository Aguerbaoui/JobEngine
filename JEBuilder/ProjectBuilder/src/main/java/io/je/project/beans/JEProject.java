package io.je.project.beans;

import blocks.WorkflowBlock;
import blocks.basic.SubProcessBlock;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.components.blocks.Block;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import models.JEWorkflow;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JEProject {

    // temp vars
    public Map<String, String> addedblockNames;
    public List<String> deletedBlockNames;
    /*
     * block names
     */
    Map<String, String> blockNames = new ConcurrentHashMap<>();
    // enum
    /*
     * block name counters
     */
    Map<String, Integer> blockNameCounters = new ConcurrentHashMap<>();
    /*
     * Project ID
     */
    @Id
    private String projectId;
    @Field("key")
    private String projectName;
    @Field("description")
    private String description;
    @Transient
    private RuleEngineSummary ruleEngine; // true -> rule engine running , false -> rule engine stopped TODO: switch to
    /*
     * Configuration path
     */
    private String configurationPath;
    /*
     * Rules in a project
     */
    @Transient
    private ConcurrentHashMap<String, JERule> rules = new ConcurrentHashMap<>();
    /*
     * Workflows in a project
     */
    @Transient
    private ConcurrentHashMap<String, JEWorkflow> workflows = new ConcurrentHashMap<>();
    /*
     * Events in a project
     */
    @Transient
    private ConcurrentHashMap<String, JEEvent> events = new ConcurrentHashMap<>();
    /*
     * Variables in a project
     */
    @Transient
    private ConcurrentHashMap<String, JEVariable> variables = new ConcurrentHashMap<>();
    private boolean autoReload = false;
    private boolean isRunning = false;
    private boolean isBuilt = false;
    private boolean unsaved;

    /*
     * Constructor
     */
    public JEProject(String projectId) {
        ruleEngine = new RuleEngineSummary();
        rules = new ConcurrentHashMap<>();
        workflows = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
        variables = new ConcurrentHashMap<>();
        this.projectId = projectId;
        isBuilt = false;
        autoReload = false;
        addedblockNames = new ConcurrentHashMap<>();
        deletedBlockNames = new ArrayList<String>();


    }

    private JEProject() {
        ruleEngine = new RuleEngineSummary();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getBlockNames() {
        return blockNames;
    }

    public void setBlockNames(Map<String, String> blockNames) {
        this.blockNames = blockNames;
    }

    public void addBlockName(String blockId, String blockName) {

        blockNames.put(blockId, blockName);
        addedblockNames.put(blockId, blockName);

    }

    public boolean isAutoReload() {
        return autoReload;
    }

    public void setAutoReload(boolean autoReload) {
        this.autoReload = autoReload;
    }

    /*
     * generate a unique block name from a block name base ( example : script =>
     * script44 )
     */
    public String generateUniqueBlockName(String blockNameBase) {
        if (blockNameBase != null) {
            String blockName = blockNameBase.replaceAll("\\s+", "");
            if (!blockNameCounters.containsKey(blockName)) {
                blockNameCounters.put(blockName, 0);
            }
            int counter = blockNameCounters.get(blockName);
            while (blockNameExists(blockName + counter)) {
                counter++;
            }
            blockNameCounters.put(blockName, counter + 1);
            return blockName + counter;
        }
        return "";
    }

    public boolean blockNameExists(String blockName) {
        return blockNames.containsValue(blockName);

    }

    /********************************************************
     * PROJECT
     **********************************************************************/

    /*
     * Get project Id
     */
    public String getProjectId() {
        return projectId;
    }

    /*
     * Set project id
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getConfigurationPath() {
        if (configurationPath == null || configurationPath.isEmpty()) {
            // TODO: use a default path in sioth config
            configurationPath = ConfigurationConstants.PROJECTS_PATH + projectName;
        }
        return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
        this.configurationPath = configurationPath;
    }

    public boolean isBuilt() {
        /*
         * for(JERule rule : this.getRules().values()) { if(!rule.isBuilt()) {
         * isBuilt=false; break;
         *
         * } }
         *
         * for(JEWorkflow workflow: workflows.values()) {
         * if(!workflow.getStatus().equals(JEWorkflow.BUILT)) { isBuilt=false; break; }
         * }
         */
        return isBuilt;
    }

    public void setBuilt(boolean isBuilt) {
        this.isBuilt = isBuilt;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    /********************************************************
     * RULES
     **********************************************************************/

    /*
     * Get project rules
     */
    public ConcurrentHashMap<String, JERule> getRules() {
        return rules;
    }

    /*
     * Set project rules
     */
    public void setRules(ConcurrentHashMap<String, JERule> rules) {
        isBuilt = false;
        if (rules != null) {
            this.rules = rules;
        }
    }

    public boolean ruleExists(String ruleId) {
        return rules.containsKey(ruleId);
    }

    public JERule getRule(String ruleId) {
        return rules.get(ruleId);
    }

    /*
     * add a new rule to project
     */
    public void addRule(JERule rule) throws RuleAlreadyExistsException {
        if (rules.containsKey(rule.getJobEngineElementID())) {
            throw new RuleAlreadyExistsException(JEMessages.RULE_EXISTS);
        }
        this.rules.put(rule.getJobEngineElementID(), rule);
        isBuilt = false;

    }

    /*
     * update rule to project
     */
    public void updateRule(JERule rule) throws RuleNotFoundException {
        if (!rules.containsKey(rule.getJobEngineElementID())) {
            throw new RuleNotFoundException(projectId, rule.getJobEngineElementID());
        }
        rules.put(rule.getJobEngineElementID(), rule);
        rule.setJeObjectLastUpdate(Instant.now());
        isBuilt = false;

    }

    /*
     * Add a block to a rule
     */
    public void addBlockToRule(Block block) {
        ((UserDefinedRule) rules.get(block.getJobEngineElementID())).addBlock(block);
        isBuilt = false;

    }

    /*
     * update Block
     */
    public void updateRuleBlock(Block block) {
        ((UserDefinedRule) rules.get(block.getJobEngineElementID())).updateBlock(block);
        isBuilt = false;

    }

    public void deleteRuleBlock(String ruleId, String blockId) {
        ((UserDefinedRule) rules.get(ruleId)).deleteBlock(blockId);
        rules.get(ruleId).setJeObjectLastUpdate(Instant.now());
        isBuilt = false;

    }

    /*
     * delete Block
     */

    /*
     * delete rule
     */
    public void deleteRule(String ruleId) throws RuleNotFoundException {
        if (!rules.containsKey(ruleId)) {
            throw new RuleNotFoundException(projectId, ruleId);
        }
        // TODO: delete file
        rules.remove(ruleId);
        ruleEngine.remove(ruleId);
        isBuilt = false;

    }

    /********************************************************
     * Workflows
     **********************************************************************/

    /*
     * Get all workflows
     */
    public ConcurrentHashMap<String, JEWorkflow> getWorkflows() {
        return workflows;
    }

    /*
     * Set all workflows
     */
    public void setWorkflows(ConcurrentHashMap<String, JEWorkflow> workflows) {
        isBuilt = false;
        this.workflows = workflows;
    }

    /*
     * Add a workflow
     */
    public void addWorkflow(JEWorkflow wf) {
        this.workflows.put(wf.getJobEngineElementID(), wf);
        isBuilt = false;

    }

    /*
     * Remove a workflow
     */
    public void removeWorkflow(String id) {
        workflows.remove(id);
        isBuilt = false;

    }

    /*
     * Checks if a workflow exists
     */
    public boolean workflowExists(String workflowId) {
        return getWorkflowByIdOrName(workflowId) != null;
    }

    /*
     * Get a workflow id
     */
    public JEWorkflow getWorkflowByIdOrName(String workflowId) {
        if (!workflows.containsKey(workflowId)) {
            // checking if workflow exists by name
            for (JEWorkflow wf : workflows.values()) {
                if (wf.getJobEngineElementName().equalsIgnoreCase(workflowId))
                    return wf;
            }
        }
        return workflows.get(workflowId);
    }

    /*
     * Add a block to a workflow
     */
    public void addBlockToWorkflow(WorkflowBlock block) {
        getWorkflowByIdOrName(block.getWorkflowId()).addBlock(block);
        isBuilt = false;

    }

    /*
     * Delete a workflow block
     */
    public void deleteWorkflowBlock(String workflowId, String blockId)
            throws InvalidSequenceFlowException, WorkflowBlockNotFoundException {
        getWorkflowByIdOrName(workflowId).deleteWorkflowBlock(blockId);
        removeBlockName(blockId);
        isBuilt = false;

    }

    public void removeBlockName(String blockId) {
        blockNames.remove(blockId);
        deletedBlockNames.add(blockId);
    }

    /*
     * Delete a workflow sequence flow
     */
    public void deleteWorkflowSequenceFlow(String workflowId, String sourceRef, String targetRef)
            throws InvalidSequenceFlowException {
        getWorkflowByIdOrName(workflowId).deleteSequenceFlow(sourceRef, targetRef);
        isBuilt = false;

    }

    /*
     * Add a workflow sequence flow
     */
    public void addWorkflowSequenceFlow(String workflowId, String sourceRef, String targetRef, String condition)
            throws WorkflowBlockNotFoundException {
        JEWorkflow wf = getWorkflowByIdOrName(workflowId);
        if (!wf.blockExists(sourceRef) || !wf.blockExists(targetRef)) {
            throw new WorkflowBlockNotFoundException(JEMessages.WORKFLOW_BLOCK_NOT_FOUND);
        }
        wf.addBlockFlow(sourceRef, targetRef, condition);
        isBuilt = false;

    }

    public boolean isWorkflowEnabled(String id) {
        return getWorkflowByIdOrName(id).isEnabled();
    }

    public void addEvent(JEEvent event) {
        events.put(event.getJobEngineElementID(), event);
    }

    public JEEvent getEvent(String eventId) throws EventException {
        if (!eventExists(eventId)) {
            throw new EventException(JEMessages.EVENT_NOT_FOUND);
        }
        return events.get(eventId);
    }

    /********************************************************
     * EVENTS
     **********************************************************************/

    public boolean eventExists(String eventId) {
        return events.containsKey(eventId);
    }

    public ConcurrentHashMap<String, JEEvent> getEvents() {
        return events;
    }

    public void setEvents(ConcurrentHashMap<String, JEEvent> events) {
        this.events = events;
    }

    /********************************************************
     * Variables
     **********************************************************************/

    public void addVariable(JEVariable var) {
        variables.put(var.getJobEngineElementID(), var);
    }

    public void removeVariable(String varId) {
        variables.remove(varId);
    }

    public ConcurrentHashMap<String, JEVariable> getVariables() {
        return variables;
    }

    public void setVariables(ConcurrentHashMap<String, JEVariable> variables) {
        this.variables = variables;
    }

    public JEVariable getVariable(String varId) throws VariableNotFoundException {
        if (!variableExists(varId)) {
            throw new VariableNotFoundException(JEMessages.VARIABLE_NOT_FOUND);
        }
        return variables.get(varId);
    }

    public boolean variableExists(String varId) {
        return variables.containsKey(varId);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public RuleEngineSummary getRuleEngine() {
        return ruleEngine;
    }

    public void setRuleEngine(RuleEngineSummary ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public Map<String, Integer> getBlockNameCounters() {
        return blockNameCounters;
    }

    public void setBlockNameCounters(Map<String, Integer> blockNameCounters) {
        this.blockNameCounters = blockNameCounters;
    }

    public JEWorkflow getStartupWorkflow() {
        for (JEWorkflow wf : workflows.values()) {
            if (wf.isOnProjectBoot())
                return wf;
        }
        return null;
    }

    public boolean workflowHasError(JEWorkflow wf) throws WorkflowStartBlockNotDefinedException, WorkflowStartBlockNotUniqueException {

        if (wf.getWorkflowStartBlock() == null || wf.getAllBlocks().isEmpty()) {
            wf.setHasErrors(true);
            return true;
        }

        for (WorkflowBlock b : wf.getAllBlocks().values()) {
            if (b instanceof SubProcessBlock) {
                for (JEWorkflow workflow : workflows.values()) {
                    if (workflow.getJobEngineElementName().equals(((SubProcessBlock) b).getSubWorkflowId())
                            && !workflow.isEnabled()) {
                        wf.setHasErrors(true);
                        return true;
                    }
                }
            }
        }
        wf.setHasErrors(false);
        return false;
    }


    public boolean isUnsaved() {
        return unsaved;
    }

    public void setUnsaved(boolean unsaved) {
        this.unsaved = unsaved;
    }

    public void clearTempLists() {
        this.addedblockNames.clear();
        this.deletedBlockNames.clear();
    }
}
