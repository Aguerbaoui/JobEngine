package io.je.ruleengine.impl;

import io.je.ruleengine.control.PersistenceMap;
import io.je.ruleengine.data.DataModelListener;
import io.je.ruleengine.loader.RuleLoader;
import io.je.ruleengine.models.Rule;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.ruleutils.IdManager;
import io.je.utilities.runtimeobject.JEObject;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.je.ruleengine.data.DataModelListener.requestInitialValue;
import static org.kie.api.builder.Message.Level.ERROR;

enum Status {
    RUNNING, STOPPED,
}

enum BuildStatus {
    BUILT, UNBUILT,
}

/**
 * The Rule Engine supports multiple projects. Each project is represented by a
 * project container and is defined by one kie container and has its own rules
 * and facts. This class is implemented using Drools Rule Engine.
 */
public class ProjectContainer {

    // This is where all the compiled rules are saved.


    Map<String, Rule> allRules = new ConcurrentHashMap<>();

    // private boolean isInitialised = false;
    // JEClassLoader loader = JEClassLoader.getInstance();
    ConcurrentHashMap<String, FactHandle> facts = new ConcurrentHashMap<>();
    private String projectId;

    // A project can be either running, or stopped.
    private Status status = Status.STOPPED;

    /*
     * ------------------- kie configuration -------------------
     */
    // this parameter indicates whether the project has been built.
    private BuildStatus buildStatus = BuildStatus.UNBUILT;

    // The KieServices is a thread-safe singleton acting as a hub giving access
    // to the other services provided by Kie.
    private KieServices kieServices;

    // KieFileSystem is an in memory file system used to programmatically define
    // the resources composing a KieModule.
    private KieFileSystem kieFileSystem;

    // This second kieFileSystem instance is used to compile rules without
    // altering the original kieFileSystem.
    private KieFileSystem kfsToCompile;

    // A KieModule is a container of all the resources necessary to define a set of
    // KieBases
    private KieModuleModel kproj;

    // The KieContainer Holds all the knowledge. Each project container is defined
    // by a Kie Container.
    private KieContainer kieContainer;

    /*
    // The KScanner is used to automatically discover if there are new releases for
    // a given KieModule
    private KieScanner kScanner;
    */

    // This represents the project container's version. It is updated whenever the
    // project components are altered
    private ReleaseId releaseId;

    // A repository of all the application's knowledge definitions
    private KieBase kieBase;

    // We interact with the engine through a KieSession.
    private KieSession kieSession;

    private int releaseVersion = 1;

    // private KieSessionManagerInterface kieManager;

    // This attribute is responsible for listening to the engine while it's active.
    //private RuleListener ruleListener;

    private boolean reloadContainer = false;

    /*
     * Constructor
     */
    public ProjectContainer(String projectId) {

        this.projectId = projectId;

        // Initialise kie configuration
        kieServices = KieServices.Factory.get();

        kieFileSystem = kieServices.newKieFileSystem();

        kfsToCompile = kieServices.newKieFileSystem();

        //ruleListener = new RuleListener(projectId);

        // createKModule
        createKModule();

    }

    void logError(Exception exp, String message) {
        logError(exp, message, null);
    }

    // FIXME factorize or remove if no more needed (debug / dev usage / customer support)
    void logError(Exception exp, String message, String objectId) {
        JELogger.logException(exp);
        JELogger.error(message, LogCategory.RUNTIME, projectId, LogSubModule.RULE, objectId);
    }

    public void resetContainer() {
        JELogger.debug(JEMessages.RELOADING_PROJECT_CONTAINER, LogCategory.RUNTIME, projectId, LogSubModule.RULE,
                null);
    /*    if (kieContainer != null && reloadContainer) {
            initKieBase();
            if (status == Status.RUNNING) {
                stopRuleExecution(true, true);
                try {
                    fireRules();
                } catch (RulesNotFiredException | RuleBuildFailedException e) {
                    e.printStackTrace();
                    JELogger.error(JEMessages.FAILED_TO_FIRE_RULES, LogCategory.RUNTIME, projectId, LogSubModule.RULE,
                            null);
                }
            }
        }*/

        stopRules(true, true);

        try {
            startRules();
        } catch (RulesNotFiredException | RuleBuildFailedException exp) {
            logError(exp, JEMessages.FAILED_TO_FIRE_RULES);
        }

        reloadContainer = false;
        JELogger.trace("Reloaded Rule Engine.");
    }


    /*--------------------------------------------------------PROJECT METHODS ---------------------------------------------------------------*/

    /*
     * get project status
     */
    public Status getStatus() {
        return status;
    }

    /*
     * the build project method builds the kie environment needed to execute the
     * rules.
     */
    public void buildProject() throws RuleBuildFailedException {
        JELogger.debugWithoutPublish(JEMessages.BUILDING_PROJECT_CONTAINER, LogCategory.RUNTIME, projectId,
                LogSubModule.RULE, null);
        // loader = new JEClassLoader(ProjectContainer.class.getClassLoader());

        // build kie environment
        if (!buildKie()) {
            JELogger.error(JEMessages.BUILDING_PROJECT_CONTAINER_FAILED, LogCategory.RUNTIME, projectId,
                    LogSubModule.RULE, null);

            throw new RuleBuildFailedException(JEMessages.BUILDING_PROJECT_CONTAINER_FAILED);
        }

        JELogger.debugWithoutPublish(JEMessages.BUILDING_PROJECT_CONTAINER_SUCCESS, LogCategory.RUNTIME, projectId,
                LogSubModule.RULE, null);

    }

    /*
     * This method start rules : fires until halt the kiesession of this project.
     */
    public void startRules() throws RulesNotFiredException, RuleBuildFailedException {

        JELogger.debugWithoutPublish("[projectId =" + projectId + "] " + JEMessages.FIRING_ALL_RULES,
                LogCategory.RUNTIME, projectId, LogSubModule.RULE, null);
        facts = new ConcurrentHashMap<String, FactHandle>();
        if (allRules == null || allRules.isEmpty()) {
            JELogger.debugWithoutPublish("[projectId =" + projectId + "] " + JEMessages.NO_RULES, LogCategory.RUNTIME,
                    projectId, LogSubModule.RULE, null);

        }

        // build project if not already built
        if (buildStatus == BuildStatus.UNBUILT) {
            buildProject();
        }

        // check that project is not already running
        if (status == Status.RUNNING) {
            stopRules(false, false);
        }

        // fire rules
        Thread t1 = null;
        try {
            Runnable runnable = () -> {
                try {
                    if (ConfigurationConstants.isDev()) {
                        // https://docs.drools.org/7.68.0.Final/drools-docs/html_single/index.html#_event_model
                       /*
                        kieSession.addEventListener(new DebugAgendaEventListener());
                        kieSession.addEventListener(new DebugProcessEventListener());
                        kieSession.addEventListener(new DebugRuleRuntimeEventListener());

                        AgendaEventListener agendaEventListener = new AgendaEventListener(projectId);
                        kieSession.addEventListener(agendaEventListener);
                        */
                    }

                    // Thread.currentThread().setContextClassLoader(loader);

                    // FIXME Bug 72: java.lang.NullPointerException at org.drools.core.phreak.PhreakJoinNode.updateChildLeftTuple(PhreakJoinNode.java:463)
                    kieSession.fireUntilHalt();

                } catch (Exception exp) {
                    // fatal : Runtime Executions
                    String ruleId = IdManager.getRuleIdFromErrorMsg(exp.getMessage());

                    logError(exp, JEMessages.RULE_EXECUTION_ERROR + StringUtils.substringBefore(exp.getMessage(), " in "), ruleId);

                    try {
                        startRules();
                    } catch (RulesNotFiredException | RuleBuildFailedException exp1) {
                        logError(exp1, JEMessages.FAILED_TO_FIRE_RULES);
                    }

                }
            };
            t1 = new Thread(runnable);
            t1.start();
            status = Status.RUNNING;

            Set<String> topics = DataModelListener.getTopicsByProjectId(projectId);

            Thread thread = new Thread(() -> {

                for (String topic : topics) {

                    requestInitialValue(topic);

                }
            });

            thread.start();

        } catch (Exception exp) {
            if (t1 != null) {
                kieSession.halt();
            }

            logError(exp, JEMessages.FAILED_TO_FIRE_RULES);

            throw new RulesNotFiredException(JEMessages.FAILED_TO_FIRE_RULES);

        }

    }

    /*
     * This method stops the rule engine
     */
    public boolean stopRules(boolean destroySession, boolean removeAllRules) {

        JELogger.debugWithoutPublish(JEMessages.STOPPING_PROJECT_CONTAINER
                        + " , destroy session : " + Boolean.toString(destroySession)
                        + " , remove all project rules : " + Boolean.toString(removeAllRules), // FIXME msg
                LogCategory.RUNTIME, projectId, LogSubModule.RULE, null);

        Set<String> topics = DataModelListener.getTopicsByProjectId(projectId);

        DataModelListener.stopListening(topics);

        // TODO : Add more control for stopping rules / catching exceptions (case rule stopped but still firing, ex : Issue 14962)
        // destroySession=false;
        try {

            if (kieSession != null) {
                kieSession.halt();
                status = Status.STOPPED;
                if (destroySession) {
                    kieSession.dispose();
                    kieSession.destroy();
                    kieSession = null;
                    facts.clear();
                }
                if (removeAllRules) {
                    allRules.clear();
                    return deleteAllRulesFromKieFileSystem();
                }

            }

        } catch (Exception exp) {
            logError(exp, JEMessages.STOPPING_PROJECT_CONTAINER_FAILED);
            return false;
        }
        return true;
    }

    /*
     * ---------------------------------------------------------------
     * --------------------  KIE CONFIGURATION  ----------------------
     * ---------------------------------------------------------------
     */

    /*
     * This method initialises the kie parameters. It is mainly responsible for
     * creating the project's kie container.
     */
    private boolean buildKie() {
        JELogger.debugWithoutPublish("[projectId =" + projectId + "] " + JEMessages.BUILDING_PROJECT,
                LogCategory.RUNTIME, projectId, LogSubModule.RULE, null);
        /*
         * if (allRules.isEmpty()) { return false; }
         */

        // TODO: only delete/re-add rule that have been modified
        // empty kie file system
        if (!deleteAllRulesFromKieFileSystem()) {
            return false;
        }

        // add rules to kfs
        if (!addAllRulesToKieFileSystem()) {
            return false;
        }
        ;

        // build all rules
        try {
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem, JEClassLoader.getDataModelInstance())
                    .buildAll(null);

            Results results = kieBuilder.getResults();

            JELogger.debug(getKieBuilderMessages(results.getMessages()));

        } catch (Exception exp) {
            logError(exp, "Error creating kieBuilder");
            return false;
        }

        return initKieBaseAndSession();

    }

    private boolean initKieBaseAndSession() {

        JELogger.debugWithoutPublish("[projectId =" + projectId + "] " + JEMessages.KIE_INIT, LogCategory.RUNTIME,
                projectId, LogSubModule.RULE, null);

        if (releaseId != null) {

            // create container
            try {
                /*
                FIXME Bug 79: Error creating kieBase org.drools.compiler.kie.builder.impl.KieServicesImpl.newKieContainer(KieServicesImpl.java:190)

                FIXME Bug 110: java.lang.RuntimeException: Cannot find KieModule: io.je:ruleengine
                */

                kieContainer = kieServices.newKieContainer(releaseId, JEClassLoader.getDataModelInstance());

                kieBase = kieContainer.getKieBase("kie-base");

                kieSession = kieBase.newKieSession(kieContainer.getKieSessionConfiguration("kie-session"), kieServices.newEnvironment());

                JELogger.debug("KIE CONTAINER : " + kieContainer.getClassLoader().toString());

                //kScanner = kieServices.newKieScanner(kieContainer);

                JEClassLoader.setCurrentRuleEngineClassLoader(JEClassLoader.getDataModelInstance());

                Thread.currentThread().setContextClassLoader(JEClassLoader.getDataModelInstance());

            } catch (Exception exp) {
                logError(exp, "Error creating kieBase");
                return false;
            }

        } else {
            return false;
        }

        return true;
    }

    /*
     * This method is responsible for creating the kieModule
     */
    private void createKModule() {

        try {

            // get new kie Module
            kproj = kieServices.newKieModuleModel();

            // add kie base model
            KieBaseModel kieBaseModel = kproj.newKieBaseModel("kie-base")
                    .setDefault(true)
                    .setEqualsBehavior(EqualityBehaviorOption.IDENTITY)
                    .setEventProcessingMode(EventProcessingOption.STREAM)
                    // For cancel match option
                    .setDeclarativeAgenda(DeclarativeAgendaOption.ENABLED)
                    .setMutability(KieBaseMutabilityOption.ALLOWED);

            // add kie session model
            KieSessionModel kieSessionModel = kieBaseModel.newKieSessionModel("kie-session")
                    .setDefault(true)
                    .setType(KieSessionModel.KieSessionType.STATEFUL)
                    .setClockType(ClockTypeOption.get("realtime"))
                    .setDirectFiring(true);

            kieFileSystem.writeKModuleXML(kproj.toXML());

            // set releaseId
            releaseId = kieServices.newReleaseId("io.je", "ruleengine" + projectId, getReleaseVer());

            // generate pom file
            kieFileSystem.generateAndWritePomXML(releaseId);

        } catch (Exception exp) {
            logError(exp, JEMessages.UNEXPECTED_ERROR);
        }

    }

    /**
     * add all rules to kie file system
     */
    private boolean addAllRulesToKieFileSystem() {
        boolean result = true;
        for (Rule rule : allRules.values()) {

            if (!addRuleToKieFileSystem(rule)) {
                result = false;
            }

        }
        return result;
    }

    /*
     * add a rule to kieFileSystem
     */
    private boolean addRuleToKieFileSystem(Rule rule) {

        if (rule == null || rule.getJobEngineElementID() == null) return false;

        try {
            String drlName = generateResourceName(ResourceType.DRL, rule.getJobEngineElementID());

            JELogger.trace(">>> adding ", LogCategory.RUNTIME, projectId, LogSubModule.RULE, drlName);

            kieFileSystem.write(drlName, rule.getContent());

        } catch (Exception exp) {
            logError(exp, JEMessages.FAILED_TO_ADD_RULE + rule.getJobEngineElementID());
            return false;
        }

        return true;
    }

    /**
     * delete rule from kie file system
     */
    private boolean deleteRuleFromKieFileSystem(Rule rule) {

        if (rule == null || rule.getJobEngineElementID() == null) return false;

        try {
            String drlName = generateResourceName(ResourceType.DRL, rule.getJobEngineElementID());

            JELogger.trace(">>> deleting : " + drlName, LogCategory.RUNTIME, projectId, LogSubModule.RULE, drlName);

            if (kieFileSystem.read(drlName) != null) {
                kieFileSystem.delete(drlName);
            }

        } catch (Exception exp) {
            logError(exp, JEMessages.FAILED_TO_DELETE_RULE, rule.getJobEngineElementID());
            return false;
        }

        return true;
    }

    /**
     * delete all rules from kie file system
     */
    private boolean deleteAllRulesFromKieFileSystem() {
        boolean result = true;
        for (Rule rule : allRules.values()) {

            if (!deleteRuleFromKieFileSystem(rule)) {
                result = false;
            }

        }
        return result;
    }

    /*
     * generate a new release version
     */
    private String getReleaseVer() {
        return "0.0." + releaseVersion++;
    }

    /**
     * update the kie container without "halting" the "kiesession" when there are rules running in the project
     */
    // FIXME check return value when called
    public boolean updateContainer() {
        try {
            releaseId = kieServices.newReleaseId("io.je", "ruleengine", getReleaseVer());

            JELogger.debug("release Id = " + releaseId, LogCategory.RUNTIME, projectId, LogSubModule.RULE, null);

            kieFileSystem.generateAndWritePomXML(releaseId);

            // FIXME kieServices newKieBuilder does not return in debug mode and CPU 100% after a while
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem, JEClassLoader.getDataModelInstance())
                    .buildAll(null);

            Results results = kieBuilder.getResults();

            JELogger.debug(getKieBuilderMessages(results.getMessages()));

            if (kieContainer == null) {
                kieContainer = kieServices.newKieContainer(releaseId, JEClassLoader.getDataModelInstance());
                JEClassLoader.setCurrentRuleEngineClassLoader(JEClassLoader.getDataModelInstance());
            }
            // Thread.currentThread().setContextClassLoader(JEClassLoader.getInstance());

            kieContainer.updateToVersion(releaseId);

            /*
            if (kScanner == null) {
                kScanner = kieServices.newKieScanner(kieContainer);
            }
            //kScanner.scanNow(); removed because it consults the internet
            */

        } catch (Exception exp) {
            logError(exp, JEMessages.UNEXPECTED_ERROR);
            return false;
        }

        return true;
    }

    /*
     * ---------------------------------------------------------------
     * ---------------------  RULE MANAGEMENT  -----------------------
     * ---------------------------------------------------------------
     */

    /*
     * This method checks if a rule with an identical id already exists
     */
    public boolean ruleExists(Rule rule) {
        return allRules.containsKey(rule.getJobEngineElementID());
    }

    /*
     * This method checks if a rule with an identical id already exists
     */
    public boolean ruleExists(String ruleID) {
        return allRules.containsKey(ruleID);
    }

    /*
     * This method adds a rule to a project container
     */
    public void addRule(Rule rule)
            throws RuleCompilationException, RuleAlreadyExistsException, JEFileNotFoundException {

        JELogger.debugWithoutPublish("[projectId =" + projectId + "] " + JEMessages.ADDING_RULE + " [ruleId ="
                        + rule.getJobEngineElementID() + "]", LogCategory.RUNTIME, projectId, LogSubModule.RULE,
                rule.getJobEngineElementID());

        // check if rule already exists
        if (ruleExists(rule)) {
            throw new RuleAlreadyExistsException(JEMessages.RULE_EXISTS);
        }

        // compile rule
        compileRule(rule);

        if (addRuleToKieFileSystem(rule)) {
            allRules.put(rule.getJobEngineElementID(), rule);
        }

        updateContainer();

        // FIXME if project is running
        if (status != Status.RUNNING) {
            buildStatus = BuildStatus.UNBUILT;
        }
    }

    /*
     * update rule in engine if rule doesn't already exist => rule will be added if
     * rule exists => rule will be updates
     */
    public boolean updateRule(Rule rule) throws RuleCompilationException, JEFileNotFoundException {
        try {
            JELogger.debugWithoutPublish("[projectId =" + projectId + "] " + JEMessages.UPDATING_RULE + " [ruleId ="
                            + rule.getJobEngineElementID() + "]", LogCategory.RUNTIME, projectId, LogSubModule.RULE,
                    rule.getJobEngineElementID());
            // compile rule
            compileRule(rule);

            if (status != Status.RUNNING) {
                buildStatus = BuildStatus.UNBUILT;
            }

            // check that rule exists and add it if not
            if (!ruleExists(rule)) {
                allRules.put(rule.getJobEngineElementID(), rule);
                if (!addRuleToKieFileSystem(rule)) {
                    return false;
                }
                updateContainer();
                return true;
            }

            // update rule in map
            allRules.put(rule.getJobEngineElementID(), rule);

            if (!deleteRuleFromKieFileSystem(rule)) {
                return false;
            }

            if (!addRuleToKieFileSystem(rule)) {
                return false;
            }

            // if project is running, update container without interrupting project
            updateContainer();

        } catch (Exception exp) {
            logError(exp, JEMessages.RULE_UPDATE_FAIL, rule.getJobEngineElementID());
            return false;
        }

        return true;

    }

    /*
     * delete rule from engine
     */
    public void deleteRule(String ruleId) throws DeleteRuleException {
        try {
            JELogger.debugWithoutPublish(
                    "[projectId = " + projectId + "] [ruleId = " + ruleId + "] " + JEMessages.DELETING_RULE,
                    LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);
            // check that rule exists
            if (!ruleExists(ruleId)) {
                return;
            }
            // if project is running, update container without interrupting project
            long startTime = System.nanoTime();

            // FIXME check returned value
            deleteRuleFromKieFileSystem(allRules.get(ruleId));
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; //divide by 1000000 to get milliseconds.
            JELogger.debug("deleteRuleFromKieFileSystem : duration : " + duration + " (ms)");

            updateContainer();

        } catch (Exception exp) {

            logError(exp, JEMessages.RULE_DELETE_FAIL, ruleId);

            throw new DeleteRuleException(JEMessages.RULE_DELETE_FAIL);

        }
        allRules.remove(ruleId);

        if (status != Status.RUNNING) {
            buildStatus = BuildStatus.UNBUILT;
        }

    }

    /*
     * this method compiles drl files and checks for errors in them
     */
    public void compileRule(Rule rule) throws RuleCompilationException, JEFileNotFoundException {

        JELogger.debugWithoutPublish(
                "[projectId =" + projectId + "] " + JEMessages.COMPILING_RULE + " Id : " + rule.getJobEngineElementID(),
                LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(), LogSubModule.RULE, rule.getJobEngineElementID());

        // Reset rule persistence
        if (PersistenceMap.getRulesPersistenceMap().get(rule.getJobEngineElementID()) != null) {
            PersistenceMap.getRulesPersistenceMap().put(rule.getJobEngineElementID(), null);
        }

        // Load rule content from rule path
        RuleLoader.loadRuleContent(rule);

        String filename = generateResourceName(ResourceType.DRL, rule.getJobEngineElementID());

        kfsToCompile.write(filename, rule.getContent());

        // JEClassLoader loader = new
        // JEClassLoader(ProjectContainer.class.getClassLoader());
        /*
         * try { Class c = loader.loadClass("classes.Car"); for(Field f:
         * c.getDeclaredFields()) {
         * JELogger.info("field name in loaded class in project container = " +
         * f.getName()); } } catch (ClassNotFoundException e) { e.printStackTrace(); }
         */
        // Thread.currentThread().setContextClassLoader( loader );

        // FIXME Bug 69: Rule does not fire from the first run, should restart
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfsToCompile, JEClassLoader.getDataModelInstance())
                .buildAll(null);

        Results results = kieBuilder.getResults();

        JELogger.debug(getKieBuilderMessages(results.getMessages()));

        kfsToCompile.delete(filename);

        if (results.hasMessages(ERROR)) {
            JELogger.error(getKieBuilderMessages(results.getMessages(ERROR)), LogCategory.RUNTIME, projectId, LogSubModule.RULE,
                    IdManager.retrieveIdFromSubRuleName(rule.getJobEngineElementID()));
            throw new RuleCompilationException(JEMessages.RULE_CONTAINS_ERRORS, getKieBuilderMessages(results.getMessages(ERROR)));
        }

    }

    public String getKieBuilderMessages(List<Message> messageList) {
        String result = "Kie Builder Messages :";
        for (Message message : messageList) {
            result += " { Id : " + message.getId();
            result += " } , { Level : " + message.getLevel();
            result += " } , { Text : " + message.getText();
            result += " } , { Path : " + message.getPath();
            result += " } , { Line : " + message.getLine();
            result += " } , { Column : " + message.getColumn();
            result += " }";
        }
        return result;
    }

    /*
     * this method compiles all the rules in this project container
     */
    public boolean compileAllRules() {
        JELogger.debugWithoutPublish("[projectId =" + projectId + "]" + JEMessages.COMPILING_ALL_RULES,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.RULE, null);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem, JEClassLoader.getDataModelInstance())
                .buildAll(null);

        Results results = kieBuilder.getResults();

        JELogger.debug(getKieBuilderMessages(results.getMessages()));

        if (results.hasMessages(ERROR)) {

            JELogger.error(getKieBuilderMessages(results.getMessages(ERROR)), LogCategory.RUNTIME, projectId, LogSubModule.RULE, null);

            return false;
        }

        return true;

    }

    /*
     * generate the rule's internal path for the rule to be added to KFS
     */
    private String generateResourceName(ResourceType type, String ruleId) {
        return "src/main/resources/" + ruleId + "." + type.getDefaultExtension();

    }

    /*
     * add a list of rules
     */
    public void addRules(List<Rule> rules) throws RuleCompilationException, RuleAlreadyExistsException,
            JEFileNotFoundException {
        for (Rule rule : rules) {
            addRule(rule);
        }

    }

    /*
     * compile a list of rules
     */
    public void compileRules(List<Rule> rules) {
        for (Rule rule : rules) {
            // TODO: implement method to compile a list of rules with 1 kie builder instance
        }

    }

    /*
     * delete a list of rules
     */
    public void deleteRules(List<String> rulesIDs) throws DeleteRuleException {
        for (String rule : rulesIDs) {
            deleteRule(rule);
        }

    }

    public boolean disableRule(String ruleID) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean enableRule(String ruleID) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * ---------------------------------------------------------------
     * ---------------------  FACT MANAGEMENT  -----------------------
     * ---------------------------------------------------------------
     */
    public void insertFact(JEObject fact) {
        if (status == Status.RUNNING) {
            // JELogger.info(String.valueOf(fact.getJeObjectLastUpdate().until(LocalDateTime.now(),
            // ChronoUnit.MILLIS)));
            // kieSession.insert(fact);
            // FIXME synchronized (kieSession) { Bug 662: Rule was running, but suddenly no more fire events (even with stop/build/start)
                try {
                    // ClassLoader t = JEClassLoader.getInstance();
                    // //io.je.utilities.classloader.JEClassLoader@733aa287
                    // ClassLoader test = fact.getClass().getClassLoader();
                    // //io.je.utilities.classloader.JEClassLoader@41ee5f60
					/*JELogger.trace(JEClassLoader.getInstance().toString(), LogCategory.RUNTIME, projectId,
							LogSubModule.RULE, fact.getJobEngineElementID());
					JELogger.trace(fact.getClass().getClassLoader().toString(), LogCategory.RUNTIME, projectId,
							LogSubModule.RULE, fact.getJobEngineElementID());
					JELogger.trace(kieContainer.getClassLoader().toString(), LogCategory.RUNTIME, projectId,
							LogSubModule.RULE, fact.getJobEngineElementID());
*/

                    // FIXME synchronized (facts) {  Bug 662: Rule was running, but suddenly no more fire events (even with stop/build/start)

                        String message = JEMessages.UPDATING_FACT + " [projectId = " + projectId
                                + " ] [factId : " + fact.getJobEngineElementID() + " ] : " + fact.toString();

                        LoggerUtils.trace(message);

					/*	JELogger.debug(message, LogCategory.DESIGN_MODE, projectId, LogSubModule.RULE, fact.getJobEngineElementID()); */

                        if (facts.containsKey(fact.getJobEngineElementID())) {
                            kieSession.update(facts.get(fact.getJobEngineElementID()), fact);

                        } else {
                            facts.put(fact.getJobEngineElementID(), kieSession.insert(fact));

                        }

                    //}

                } catch (Exception exp) {

                    logError(exp, "[factId =" + fact.getJobEngineElementID() + "] " + JEMessages.FAILED_TO_UPDATE_FACT,
                            fact.getJobEngineElementID());

                }

            //}

        } else {
            LoggerUtils.warn("Trying to insert fact : " + fact.toString() + ", but project is not running. Project Id : " + projectId);
        }

    }

    public void retractFact(String factId) {
        try {
            // FIXME
            kieSession.delete(facts.get(factId));

        } catch (Exception e) {
            LoggerUtils.logException(e);
        }
    }

    public boolean updateFact(JEObject fact) {
        // TODO Auto-generated method stub
        return false;
    }

    public Rule getRule(String ruleId) {
        return allRules.get(ruleId);

    }

    public boolean isReloadContainer() {
        return reloadContainer;
    }

    public void setReloadContainer(boolean reloadContainer) {
        this.reloadContainer = reloadContainer;
    }

    /*
     * public void setClassLoader(JEClassLoader loader) { this.loader = loader;
     * Thread.currentThread().setContextClassLoader(loader); updateContainer();
     *
     * }
     */
}
