package io.je.ruleengine.impl;

import io.je.ruleengine.kie.KieSessionManagerInterface;
import io.je.ruleengine.listener.RuleListener;
import io.je.ruleengine.loader.RuleLoader;
import io.je.ruleengine.models.Rule;
import io.je.utilities.constants.RuleEngineErrors;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.JEObject;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

enum Status {
	RUNNING, STOPPED,
}

enum BuildStatus {
	BUILT, UNBUILT,
}

/*
 * The Rule Engine supports multiple projects. Each project is represented by a
 * project container and is defined by one kie container and has its own rules
 * and facts. This class is implemented using Drools Rule Engine.
 */
public class ProjectContainer {

	// This is where all the compiled rules are saved.

	Map<String, Rule> allRules = new ConcurrentHashMap<>();

	private String projectID;
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
	// This represents the project container's version. It is updated whenever the
	// project
	// components are altered
	private ReleaseId releaseId;
	// The KScanner is used to automatically discover if there are new releases for
	// a given KieModule
	private KieScanner kScanner;
	// A repository of all the application's knowledge definitions
	private KieBase kieBase;
	// We interact with the engine through a KieSession.
	private KieSession kieSession;
	private int releaseVersion = 1;
	private KieSessionManagerInterface kieManager;
	private ClassLoader classLoader;
	// This attribute is responsible for listening to the engine while it's active.
	private RuleListener ruleListener;

	private boolean isInitialised = false;

	ConcurrentHashMap<String, FactHandle> facts = new ConcurrentHashMap<>();

	/*
	 * Constructor
	 */
	public ProjectContainer(String id) {

		projectID = id;
		// Initialise kie configuration .
		kieServices = KieServices.Factory.get();
		kieFileSystem = kieServices.newKieFileSystem();
		kfsToCompile = kieServices.newKieFileSystem();

		// createKModule
		createKModule();

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
		JELogger.info(ProjectContainer.class, RuleEngineErrors.buildingProjectContainer);

		// build kie environment
		if (!buildKie()) {
			JELogger.error(ProjectContainer.class, RuleEngineErrors.buildingProjectContainerFailed);
			throw new RuleBuildFailedException(RuleEngineErrors.buildingProjectContainerFailed);
		}

		JELogger.info(ProjectContainer.class, RuleEngineErrors.buildingProjectContainerSuccessful);

	}

	/*
	 * This method fires until halt the kiesession of this project.
	 */
	public void fireRules() throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {

		JELogger.debug(getClass(), "Rule Engine - [projectId ="+projectID+"] firing all rules..");
		facts = new ConcurrentHashMap<String, FactHandle>();
		if(allRules == null || allRules.isEmpty())
		{
			JELogger.warning(getClass(), "Rule Engine - [projectId ="+projectID+"]  This project has no rules ");

		}
		
		// build project if not already built
		if (buildStatus == BuildStatus.UNBUILT) {
			buildProject();
		}

		// check that project is not already running
		if (status == Status.RUNNING) {
			JELogger.error(ProjectContainer.class, RuleEngineErrors.projectAlreadyRunning);
			throw new ProjectAlreadyRunningException("");
		}

		// fire rules
		Thread t1 = null;
		try {
			if (kieSession == null) {
				kieSession = kieBase.newKieSession();
			}
			Runnable runnable = () -> { 
				try {
				kieSession.fireUntilHalt();
				}catch(Exception e)
				{
					//fatal : Runtime Executions
					JELogger.error(ProjectContainer.class, "RULE EXECUTION ERROR : " + e.getMessage());
					stopRuleExecution();
					kieSession.dispose();
					//TODO: empty event/fact handle list
					kieSession = kieBase.newKieSession();
					try {
						fireRules();
					} catch (RulesNotFiredException | RuleBuildFailedException | ProjectAlreadyRunningException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						JELogger.error(ProjectContainer.class, RuleEngineErrors.failedToFireRules);
					}
					
				}
				};
			t1 = new Thread(runnable);
			t1.start();
			status = Status.RUNNING;

		} catch (Exception e) {
			if (t1 != null) {
				kieSession.halt();
			}
			JELogger.error(ProjectContainer.class, RuleEngineErrors.failedToFireRules);
			throw new RulesNotFiredException(RuleEngineErrors.failedToFireRules);
		}

	}
	
	



	private void resetKieSession() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * This method stops the rule execution
	 */
	public boolean stopRuleExecution() {
		JELogger.info(RuleEngineErrors.stoppingProjectContainer);
		try {

			kieSession.halt();
			status = Status.STOPPED;

		} catch (Exception e) {
			JELogger.error(ProjectContainer.class, RuleEngineErrors.stoppingProjectContainerFailed);
		}
		return true;
	}

	/*
	 * ---------------------------------------------------------------
	 * --------------------------------------------------------KIE CONFIGURATION
	 * ---------------------------------------------------------------
	 */

	/*
	 * This method initialises the kie parameters. It is mainly responsible for
	 * creating the project's kie container.
	 */
	private boolean buildKie() {
		JELogger.debug(getClass(), "Rule Engine - [projectId ="+projectID+"] building kie ..");
		/*if (allRules.isEmpty()) {
			return false;
		}*/

		// TODO: only delete/re-add rule that have been modified
		// empty kie file system
		deleteAllRulesFromKieFileSystem();

		// add rules to kfs
		addAllRulesToKieFileSystem();

		// build all rules
		try {
			kieServices.newKieBuilder(kieFileSystem, null).buildAll(null);
		} catch (Exception e) {
			e.printStackTrace();
			JELogger.error(ProjectContainer.class, Arrays.toString(e.getStackTrace()));
			return false;
		}
		if (!isInitialised) {

			return initKieBase();

		} else {
			JELogger.debug(getClass(), "Rule Engine - [projectId ="+projectID+"] kie built.");
			return true;
		}

	}

	private boolean initKieBase() {
		if (!isInitialised) {
			JELogger.debug(getClass(), "Rule Engine - [projectId ="+projectID+"] Initialising kieBase");
			if (releaseId != null) {
				// create container
				try {
					kieContainer = kieServices.newKieContainer(releaseId, null);
					kScanner = kieServices.newKieScanner(kieContainer);
					kieBase = kieContainer.getKieBase("kie-base");

				} catch (Exception e) {
					e.printStackTrace();
					JELogger.error(ProjectContainer.class, Arrays.toString(e.getStackTrace()));
					JELogger.warning(getClass(), "Rule Engine - [projectId ="+projectID+"] failed to initialise kie base");
					return false;
				}

			} else {
				return false;
			}
			isInitialised = true;
			return true;
		} else {
			return true;
		}
		
	}

	/*
	 * this method is responsible for creating the kieModule
	 */
	private void createKModule() {

		try {

			// get new kie Module
			kproj = kieServices.newKieModuleModel();

			// add kie base model
			KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("kie-base").setDefault(true)
					.setEqualsBehavior(EqualityBehaviorOption.IDENTITY)
					.setEventProcessingMode(EventProcessingOption.STREAM);

			// add kie session model
			kieBaseModel1.newKieSessionModel("kie-session").setDefault(true)
					.setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get("realtime"));
			kieFileSystem.writeKModuleXML(kproj.toXML());

			// set releaseId
			releaseId = kieServices.newReleaseId("io.je", "ruleengine" + projectID, getReleaseVer());

			// generate pom file
			kieFileSystem.generateAndWritePomXML(releaseId);
		} catch (Exception e) {
			JELogger.error(ProjectContainer.class, RuleEngineErrors.unexpectedError + e.getMessage());
		}

	}

	/*
	 * add all rules to kie file system
	 */
	private boolean addAllRulesToKieFileSystem() {

		for (Rule rule : allRules.values()) {
			addRuleToKieFileSystem(rule);

		}
		return true;
	}

	/*
	 * add a rule to kieFileSystem
	 */
	private boolean addRuleToKieFileSystem(Rule rule) {

		try {
			kieFileSystem.write(generateResourceName(ResourceType.DRL, rule.getName()), rule.getContent());

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/*
	 * delete rule from kie file system
	 */
	private boolean deleteRuleFromKieFileSystem(Rule rule) {

		try {
			kieFileSystem.delete(generateResourceName(ResourceType.DRL, rule.getName()));

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/*
	 * delete all rules from kie file system
	 */
	private boolean deleteAllRulesFromKieFileSystem() {

		for (Rule rule : allRules.values()) {
			deleteRuleFromKieFileSystem(rule);

		}
		return true;
	}

	/*
	 * generate a new release version
	 */
	private String getReleaseVer() {
		return "0.0." + releaseVersion++;
	}

	/*
	 * update the kie container
	 */
	public boolean updateContainer() {
		try {
			releaseId = kieServices.newReleaseId("io.je", "ruleengine", getReleaseVer());
			kieFileSystem.generateAndWritePomXML(releaseId);
			kieServices.newKieBuilder(kieFileSystem, classLoader).buildAll(null);
			kieContainer.updateToVersion(releaseId);
			kScanner.scanNow();
		} catch (Exception e) {
			//JELogger.error(ProjectContainer.class, RuleEngineErrors.FailedToUpdateContainer + e.getMessage());
		}

		return true;
	}

	/*
	 * ---------------------------------------------------------------
	 * -------------------------------------------------------- RULE MANAGEMENT
	 * ---------------------------------------------------------------
	 */

	/*
	 * this method checks if a rule with an identical id already exists
	 */
	public boolean ruleExists(Rule rule) {
		return allRules.containsKey(rule.getJobEngineElementID());
	}

	/*
	 * this method checks if a rule with an identical id already exists
	 */
	public boolean ruleExists(String ruleID) {
		return allRules.containsKey(ruleID);
	}

	/*
	 * This method adds a rule to a project container
	 */
	public void addRule(Rule rule)
			throws RuleCompilationException, RuleAlreadyExistsException, JEFileNotFoundException {
		
		JELogger.debug(getClass(), "Rule Engine - [projectId ="+projectID+"] adding rule ["+rule.getName()+"]");

		// check if rule already exists
		if (ruleExists(rule)) {
			throw new RuleAlreadyExistsException(RuleEngineErrors.RULE_EXISTS);
		}

		// compile rule
		compileRule(rule);
		allRules.put(rule.getJobEngineElementID(), rule);
		addRuleToKieFileSystem(rule);
		updateContainer();

		// if project is running
		if (status != Status.RUNNING) {
			buildStatus = BuildStatus.UNBUILT;
		}
	}

	/*
	 * update rule in engine if rule doesn't already exist => rule will be added if
	 * rule exists => rule will be updates
	 */
	public boolean updateRule(Rule rule) throws RuleCompilationException, JEFileNotFoundException {
		JELogger.debug(getClass(), "Rule Engine - [projectId ="+projectID+"] updating rule ["+rule.getName()+"]..");

		// compile rule
		compileRule(rule);
		
		if (status != Status.RUNNING) {
			buildStatus = BuildStatus.UNBUILT;
		}

		// check that rule exists and add it if not
		if (!ruleExists(rule)) {
			allRules.put(rule.getJobEngineElementID(), rule);
			addRuleToKieFileSystem(rule);
			updateContainer();
			return true;
		}

		// update rule in map
		allRules.put(rule.getJobEngineElementID(), rule);

		// if project is running, update container without interrupting project

		try {
			deleteRuleFromKieFileSystem(rule);
			addRuleToKieFileSystem(rule);
			updateContainer();
		} catch (Exception e) {
			JELogger.error(ProjectContainer.class, RuleEngineErrors.failedToUpdateRule + e.getMessage());
			return false;
		}

		
		return true;

	}

	/*
	 * delete rule from engine
	 */
	public void deleteRule(String ruleID) throws DeleteRuleException {
		JELogger.debug(getClass(), "Rule Engine - [projectId ="+projectID+"] deleting rule [id : "+ruleID+"]..");

		// check that rule exists
		if (!ruleExists(ruleID)) {
			return;
		}

		// update rule in map
		Rule rule = allRules.get(ruleID);
		allRules.remove(ruleID);
		// if project is running, update container without interrupting project
			try {
				deleteRuleFromKieFileSystem(rule);
				updateContainer();
			} catch (Exception e) {
				JELogger.error(ProjectContainer.class, RuleEngineErrors.failedToDeleteRule + e.getMessage());
				throw new DeleteRuleException(RuleEngineErrors.failedToDeleteRule);
			}

			if (status != Status.RUNNING) {

			buildStatus = BuildStatus.UNBUILT;
		}

	}

	/*
	 * this method compiles drl files and checks for errors in them
	 */
	public void compileRule(Rule rule) throws RuleCompilationException, JEFileNotFoundException {

		// load rule content from rule path
		JELogger.trace( "Rule Engine - [projectId ="+projectID+"] compiling rule ["+rule.getName()+"]..");
		RuleLoader.loadRuleContent(rule);
		String filename = generateResourceName(ResourceType.DRL, rule.getName());
		kfsToCompile.write(filename, rule.getContent());
		KieBuilder kieBuilder = kieServices.newKieBuilder(kfsToCompile, null).buildAll(null);
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			JELogger.error(ProjectContainer.class, results.getMessages().toString());
			throw new RuleCompilationException(RuleEngineErrors.RULE_CONTAINS_ERRORS,
					results.getMessages().get(0).getText());
		}
		kfsToCompile.delete(filename);

	}

	/*
	 * this method compiles all the rules in this project container
	 */
	public boolean compileAllRules() {
		JELogger.debug(getClass(), "Rule Engine - [projectId ="+projectID+"] compiling all rules..");

		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem, null).buildAll(null);
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			JELogger.error(ProjectContainer.class, results.getMessages().toString());
			return false;
		}

		return true;

	}

	/*
	 * generate the rule's internal path for the rule to be added to KFS
	 */
	private String generateResourceName(ResourceType type, String ruleName) {
		return "src/main/resources/" + ruleName + "." + type.getDefaultExtension();

	}

	/*
	 * add a list of rules
	 */
	public void addRules(List<Rule> rules) throws RuleCompilationException, RuleAlreadyExistsException,
			FileNotFoundException, JEFileNotFoundException {
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
	 * -------------------------------------------------------- FACT MANAGEMENT
	 * ---------------------------------------------------------------
	 */
	public void insertFact(JEObject fact) {
		if (status == Status.RUNNING) {
			// JELogger.info(String.valueOf(fact.getJeObjectLastUpdate().until(LocalDateTime.now(),
			// ChronoUnit.MILLIS)));
			// kieSession.insert(fact);
			synchronized (kieSession) {
				try {
					synchronized(facts)
					{
						JELogger.debug(getClass(), "Rule Engine - [projectId ="+projectID+"] updating fact [factId :"+fact.getJobEngineElementID()+"]..");

						if (facts.containsKey(fact.getJobEngineElementID())) {
							kieSession.update(facts.get(fact.getJobEngineElementID()), fact);


						} else {
							facts.put(fact.getJobEngineElementID(), kieSession.insert(fact));
							// JELogger.info(ProjectContainer.class, " inserting fact ");

						}
					}
				
					// JELogger.info(ProjectContainer.class, " inserting fact ");
				} catch (Exception e) {
					e.printStackTrace();
					JELogger.error(ProjectContainer.class, Arrays.toString(e.getStackTrace()));
					JELogger.error(ProjectContainer.class, " failed to insert fact into working memory [factId ="
							+ fact.getJobEngineElementID() + "]: " + e.getMessage());

				}

			}
			
		}

	}

	public boolean retractFact(JEObject fact) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean updateFact(JEObject fact) {
		// TODO Auto-generated method stub
		return false;
	}

	public Rule getRule(String ruleId) {
		return allRules.get(ruleId);
		
	}

}