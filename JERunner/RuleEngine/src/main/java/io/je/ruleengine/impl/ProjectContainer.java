package io.je.ruleengine.impl;

import io.je.ruleengine.kie.KieSessionManagerInterface;
import io.je.ruleengine.listener.RuleListener;
import io.je.ruleengine.loader.RuleLoader;
import io.je.ruleengine.models.Rule;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.ruleutils.RuleIdManager;
import io.je.utilities.runtimeobject.JEObject;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import org.apache.commons.lang3.StringUtils;
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
	//private KieSessionManagerInterface kieManager;
	// This attribute is responsible for listening to the engine while it's active.
	private RuleListener ruleListener;

	private boolean isInitialised = false;
	//JEClassLoader loader = JEClassLoader.getInstance();
	ConcurrentHashMap<String, FactHandle> facts = new ConcurrentHashMap<>();

	/*
	 * Constructor
	 */
	public ProjectContainer(String projectId) {

		this.projectId = projectId;
		// Initialise kie configuration .
		kieServices = KieServices.Factory.get();
		kieFileSystem = kieServices.newKieFileSystem();
		kfsToCompile = kieServices.newKieFileSystem();
		ruleListener= new RuleListener(projectId);

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
		JELogger.debugWithoutPublish(JEMessages.BUILDING_PROJECT_CONTAINER,
				LogCategory.RUNTIME, projectId,
				LogSubModule.RULE, null);
		 //loader = new JEClassLoader(ProjectContainer.class.getClassLoader());
		// build kie environment
		if (!buildKie()) {
			JELogger.error(JEMessages.BUILDING_PROJECT_CONTAINER_FAILED,
					LogCategory.RUNTIME, projectId,
					LogSubModule.RULE, null);
			throw new RuleBuildFailedException(JEMessages.BUILDING_PROJECT_CONTAINER_FAILED);
		}
		JELogger.debugWithoutPublish(JEMessages.BUILDING_PROJECT_CONTAINER_SUCCESS,
				LogCategory.RUNTIME, projectId,
				LogSubModule.RULE, null);

	}

	/*
	 * This method fires until halt the kiesession of this project.
	 */
	public void fireRules() throws RulesNotFiredException, RuleBuildFailedException {

		JELogger.debugWithoutPublish("[projectId ="+projectId+"]" + JEMessages.FIRING_ALL_RULES,
				LogCategory.RUNTIME, projectId,
				LogSubModule.RULE, null);
		facts = new ConcurrentHashMap<String, FactHandle>();
		if(allRules == null || allRules.isEmpty())
		{
			JELogger.debugWithoutPublish("[projectId ="+projectId+"] " + JEMessages.NO_RULES,
					LogCategory.RUNTIME, projectId,
					LogSubModule.RULE, null);

		}
		
		// build project if not already built
		if (buildStatus == BuildStatus.UNBUILT) {
			buildProject();
		}

		// check that project is not already running
		if (status == Status.RUNNING) {
			stopRuleExecution(false,false);
			
		}

		// fire rules
		Thread t1 = null;
		try {
			if (kieSession == null) {
				kieSession = kieBase.newKieSession();
			}
			Runnable runnable = () -> { 
				try {
				kieSession.addEventListener(ruleListener);
			//	Thread.currentThread().setContextClassLoader(loader);
				kieSession.fireUntilHalt();
				}catch(Exception e)
				{
					e.printStackTrace();
					//fatal : Runtime Executions
					String ruleId= RuleIdManager.getRuleIdFromErrorMsg(e.getMessage());
					e.printStackTrace();
					JELogger.error(JEMessages.RULE_EXECUTION_ERROR +StringUtils.substringBefore(e.getMessage(), " in ") ,  LogCategory.RUNTIME,
                              projectId, LogSubModule.RULE, ruleId);
					try {
						fireRules();
					} catch (RulesNotFiredException | RuleBuildFailedException  e1) {
						JELogger.error(JEMessages.FAILED_TO_FIRE_RULES,  LogCategory.RUNTIME,
								projectId, LogSubModule.RULE, ruleId);
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
			JELogger.error(JEMessages.FAILED_TO_FIRE_RULES+" : "+e.getMessage(),  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);
			//TODO Add message to exception
			e.printStackTrace();
			throw new RulesNotFiredException("");

		}

	}
	
	



	/*
	 * This method stops the rule execution
	 */
	public boolean stopRuleExecution(boolean destroySession,boolean removeAllRules) {
		JELogger.debugWithoutPublish(JEMessages.STOPPING_PROJECT_CONTAINER,  LogCategory.RUNTIME,
				projectId, LogSubModule.RULE, null);
		//destroySession=false;
		try {

			if(kieSession!=null)
			{
				kieSession.halt();
				status = Status.STOPPED;
				if(destroySession)
				{
					kieSession.dispose();
					kieSession.destroy();
					kieSession=null;
					facts.clear();
				}
				if(removeAllRules)
				{
					allRules.clear();
					deleteAllRulesFromKieFileSystem();
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JELogger.error(JEMessages.STOPPING_PROJECT_CONTAINER_FAILED,  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);
			return false;
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
		JELogger.debugWithoutPublish("[projectId ="+projectId+"]" + JEMessages.BUILDING_PROJECT,  LogCategory.RUNTIME,
				projectId, LogSubModule.RULE, null);
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
			kieServices.newKieBuilder(kieFileSystem, JEClassLoader.getInstance()).buildAll(null);
		} catch (Exception e) {
			//e.printStackTrace();
			JELogger.error("Error creating kieBuilder \n " + Arrays.toString(e.getStackTrace()),  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);
			return false;
		}
		if (!isInitialised) {

			return initKieBase();

		} else {
			JELogger.debugWithoutPublish("[projectId ="+projectId+"]"+JEMessages.KIE_BUILT,  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);
			return true;
		}

	}

	private boolean initKieBase() {
		if (!isInitialised) {
			JELogger.debugWithoutPublish("[projectId ="+projectId+"]" + JEMessages.KIE_INIT,  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);
			if (releaseId != null) {

				// create container
				try {
					kieContainer = kieServices.newKieContainer(releaseId, JEClassLoader.getInstance());
					kScanner = kieServices.newKieScanner(kieContainer);
					kieBase = kieContainer.getKieBase("kie-base");
					Thread.currentThread().setContextClassLoader(JEClassLoader.getInstance());


				} catch (Exception e) {
					JELogger.error("Error creating kieBase \n " + Arrays.toString(e.getStackTrace()),  LogCategory.RUNTIME,
							projectId, LogSubModule.RULE, null);
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
			releaseId = kieServices.newReleaseId("io.je", "ruleengine" + projectId, getReleaseVer());

			// generate pom file
			kieFileSystem.generateAndWritePomXML(releaseId);
		} catch (Exception e) {
			JELogger.error(JEMessages.UNEXPECTED_ERROR + "\n" + Arrays.toString(e.getStackTrace()),  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);
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
			String ruleName = generateResourceName(ResourceType.DRL, rule.getJobEngineElementName());
			JELogger.trace(">>> adding "+ruleName ,  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);		
			kieFileSystem.write(ruleName, rule.getContent());

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
			String ruleName = generateResourceName(ResourceType.DRL, rule.getJobEngineElementName());
			JELogger.trace("deleting "+ruleName ,  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);	
			

			if(kieFileSystem.read(ruleName)!=null)
			{
				kieFileSystem.delete(ruleName);
			}

		} catch (Exception e) {
			
			JELogger.error(JEMessages.FAILED_TO_DELETE_RULE,  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);
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
			JELogger.debug("release Id = "+releaseId,  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);
			kieFileSystem.generateAndWritePomXML(releaseId);
			kieServices.newKieBuilder(kieFileSystem, JEClassLoader.getInstance()).buildAll();
			if(kieContainer==null)
			{
				kieContainer = kieServices.newKieContainer(releaseId, JEClassLoader.getInstance());
			}
			//Thread.currentThread().setContextClassLoader(JEClassLoader.getInstance());

			kieContainer.updateToVersion(releaseId);

			if(kScanner==null)
			{
				 kScanner = kieServices.newKieScanner(kieContainer);
			}
			kScanner.scanNow();

		} catch (Exception e) {
			e.printStackTrace();
			JELogger.error(JEMessages.UNEXPECTED_ERROR + "\n" + Arrays.toString(e.getStackTrace()),  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);
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

		JELogger.debugWithoutPublish("[projectId ="+projectId+"] " + JEMessages.ADDING_RULE+ " ["+rule.getJobEngineElementName()+"]",  LogCategory.RUNTIME,
				projectId, LogSubModule.RULE, rule.getJobEngineElementID());
		// check if rule already exists
		if (ruleExists(rule)) {
			throw new RuleAlreadyExistsException(JEMessages.RULE_EXISTS);
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
		JELogger.debugWithoutPublish("[projectId ="+projectId+"] " + JEMessages.UPDATING_RULE+ " ["+rule.getJobEngineElementName()+"]",  LogCategory.RUNTIME,
				projectId, LogSubModule.RULE, rule.getJobEngineElementID());
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
			JELogger.error(JEMessages.RULE_UPDATE_FAIL + "\n" + Arrays.toString(e.getStackTrace()),  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, rule.getJobEngineElementID());
			return false;
		}

		
		return true;

	}

	/*
	 * delete rule from engine
	 */
	public void deleteRule(String ruleId) throws DeleteRuleException {
		JELogger.debugWithoutPublish("[projectId ="+projectId+"] "+JEMessages.DELETING_RULE+" [id : "+ruleId+"]..",  LogCategory.RUNTIME,
				projectId, LogSubModule.RULE, ruleId);
		// check that rule exists
		if (!ruleExists(ruleId)) {
				return;
		}
		// if project is running, update container without interrupting project
			try {
				deleteRuleFromKieFileSystem(allRules.get(ruleId));
				//deleteAllRulesFromKieFileSystem();
				//addAllRulesToKieFileSystem();
		
				updateContainer();
			} catch (Exception e) {
				JELogger.error(JEMessages.RULE_DELETE_FAIL + "\n" + Arrays.toString(e.getStackTrace()),  LogCategory.RUNTIME,
						projectId, LogSubModule.RULE, ruleId);
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

		// load rule content from rule path

		JELogger.debugWithoutPublish("[projectId ="+projectId+"]"+
						JEMessages.COMPILING_RULE+" ["+rule.getJobEngineElementName()+"]..",
				LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(),
				LogSubModule.RULE,rule.getJobEngineElementID());
		RuleLoader.loadRuleContent(rule);
		String filename = generateResourceName(ResourceType.DRL, rule.getJobEngineElementName());
		kfsToCompile.write(filename, rule.getContent());
		//JEClassLoader loader = new JEClassLoader(ProjectContainer.class.getClassLoader());
		/*try {
			Class c = loader.loadClass("classes.Car");
			for(Field f: c.getDeclaredFields()) {
				JELogger.info("field name in loaded class in project container = " + f.getName());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}*/
		//Thread.currentThread().setContextClassLoader( loader );
		KieBuilder kieBuilder = kieServices.newKieBuilder(kfsToCompile, JEClassLoader.getInstance()).buildAll();

		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			JELogger.error(results.getMessages().toString(),  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, rule.getJobEngineElementID());
			throw new RuleCompilationException(JEMessages.RULE_CONTAINS_ERRORS,
					results.getMessages().get(0).getText());
		}
		kfsToCompile.delete(filename);

	}

	/*
	 * this method compiles all the rules in this project container
	 */
	public boolean compileAllRules() {
		JELogger.debugWithoutPublish("[projectId ="+projectId+"]"+JEMessages.COMPILING_RULES,
				LogCategory.DESIGN_MODE, projectId,
				LogSubModule.RULE,null);
		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem, JEClassLoader.getInstance()).buildAll(null);
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			JELogger.error(results.getMessages().toString(),  LogCategory.RUNTIME,
					projectId, LogSubModule.RULE, null);
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
					//ClassLoader t = JEClassLoader.getInstance(); //io.je.utilities.classloader.JEClassLoader@733aa287
					//ClassLoader test = fact.getClass().getClassLoader(); //io.je.utilities.classloader.JEClassLoader@41ee5f60
					JELogger.trace(JEClassLoader.getInstance().toString(), LogCategory.RUNTIME, projectId, LogSubModule.RULE, fact.getJobEngineElementID());
					JELogger.trace(fact.getClass().getClassLoader().toString(), LogCategory.RUNTIME, projectId, LogSubModule.RULE, fact.getJobEngineElementID());

					synchronized(facts)
					{
						JELogger.debug("[projectId ="+projectId+"] [factId :"+fact.getJobEngineElementID()+"]" + JEMessages.UPDATING_FACT,
								LogCategory.DESIGN_MODE, projectId,
								LogSubModule.RULE,fact.getJobEngineElementID());
						if (facts.containsKey(fact.getJobEngineElementID())) {
							kieSession.update(facts.get(fact.getJobEngineElementID()), fact); 
							

						} else {
							facts.put(fact.getJobEngineElementID(), kieSession.insert(fact));

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					JELogger.error("[factId ="
									+ fact.getJobEngineElementID() + "]"+ JEMessages.FAILED_TO_UPDATE_FACT + ": " + e.getMessage() + "\n" ,  LogCategory.RUNTIME,
							projectId, LogSubModule.RULE, fact.getJobEngineElementID());

				}

			}
			
		}

	}

	public void retractFact(String factId) {
		try {
			//kieSession.delete(facts.get(factId));

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public boolean updateFact(JEObject fact) {
		// TODO Auto-generated method stub
		return false;
	}

	public Rule getRule(String ruleId) {
		return allRules.get(ruleId);
		
	}

	/*public void setClassLoader(JEClassLoader loader) {
		this.loader = loader;
		Thread.currentThread().setContextClassLoader(loader);
		updateContainer();

	}*/
}
