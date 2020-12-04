package io.je.ruleengine.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import io.je.ruleengine.kie.KieSessionManagerInterface;
import io.je.ruleengine.listener.RuleListener;
import io.je.ruleengine.loader.RuleLoader;
import io.je.ruleengine.models.Rule;
import io.je.ruleengine.utils.LogConstants;
import io.je.utilities.exceptions.ProjectAlreadyRunningException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleCompilationException;
import io.je.utilities.exceptions.RuleEngineBuildFailedException;
import io.je.utilities.exceptions.RulesNotFiredException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.JEObject;

/*
 * The Rule Engine supports multiple projects.
 * Each project is represented by a project container and is defined by one kie container
 * and has its own rules and facts.
 * This class is implemented using Drools Rule Engine.
 */
public class ProjectContainer  {

	private String projectID; 
	
	// A project can be either running, or stopped.
	private Status status = Status.STOPPED;
	
	//this parameter indicates whether the project has been built.
	private BuildStatus buildStatus = BuildStatus.UNBUILT;

	/* -------------------
	 * kie configuration
	 *-------------------*/
	
	//The KieServices is a thread-safe singleton acting as a hub giving access 
	//to the otherServices provided by Kie.
	private KieServices kieServices;
	
	//KieFileSystem is an in memory file system used to programmatically define
	//the resources composing a KieModule.
	private KieFileSystem kieFileSystem;
	
	// This second kieFileSystem instance is used to compile rules without
	//altering the original kieFileSystem.
	private KieFileSystem kfsToCompile;
	
	//A KieModule is a container of all the resources necessary to define a set of KieBases 
	private KieModuleModel kproj;
	
	//The KieContainer Holds all the knowledge. Each project container is defined 
	//by a Kie Container.
	private KieContainer kieContainer;
	
	//This represents the project container's version. It is updated whenever the project
	//components are altered 
	private ReleaseId releaseId;
	
	//The KScanner is used to automatically discover if there are new releases for 
	//a given KieModule 
	private KieScanner kScanner;
	
	// A repository of all the application's knowledge definitions
	private KieBase kieBase;
	
	// We interact with the engine through a KieSession. 
	private KieSession kieSession;
	
	
	private int releaseVersion = 1;
	private KieSessionManagerInterface kieManager;
	private ClassLoader classLoader;

	// This is where all the compiled rules are saved.
	Map<String, Rule> allRules = new HashMap<>();
	
	//This attribute is responsible for listening to the engine while it's active.
	private RuleListener ruleListener;

	/*
	 * Constructor 
	 */
	public ProjectContainer(String id) {

		projectID = id;		
		//Initialise kie configuration .
		kieServices = KieServices.Factory.get();
		kieFileSystem = kieServices.newKieFileSystem();
		kfsToCompile = kieServices.newKieFileSystem();
		
		//createKModule
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
	public void buildProject() throws RuleEngineBuildFailedException {
		JELogger.info(LogConstants.buildingProjectContainer);
		
		//build kie environment
		if (!buildKie()) {
			JELogger.error(LogConstants.buildingProjectContainerFailed);
			throw new RuleEngineBuildFailedException("200", LogConstants.buildingProjectContainerFailed);
		}
		
		JELogger.info(LogConstants.buildingProjectContainerSuccessful);
		
		//set build status to built
		buildStatus = BuildStatus.BUILT;
	}

	
	/*
	 * This method fires until halt the kiesession of this project. 
	 */
	public void fireRules() throws RulesNotFiredException, RuleEngineBuildFailedException, ProjectAlreadyRunningException {

		// build project if not already built
		if (buildStatus == BuildStatus.UNBUILT) {
			buildProject();
		}
		
		//check that project is not already running
		if(status == Status.RUNNING)
		{
			JELogger.error(LogConstants.projectAlreadyRunning);
			throw new ProjectAlreadyRunningException("200","");
		}
		
		//fire rules
		try {
			if(kieSession == null)
			{
				kieSession = kieBase.newKieSession();
			}
			Runnable runnable = () ->  kieSession.fireUntilHalt();
			new Thread(runnable).start();
			status = Status.RUNNING;

		} catch (Exception e) {
			JELogger.error(LogConstants.failedToFireRules);
			throw new RulesNotFiredException("200", "");
		}


	}

	
	/*
	 * This method stops the rule execution
	 */
	public boolean stopRuleExecution() {
			JELogger.info(LogConstants.stoppingProjectContainer);
		try {

			kieSession.halt();
			status = Status.STOPPED;

		} catch (Exception e) {
			JELogger.error(LogConstants.stoppingProjectContainerFailed);
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

		if (allRules.isEmpty()) {
			return false;
		}

		// add rules to kfs
		addAllRulesToKieFileSystem();

		// build all rules
		try {
			classLoader = Thread.currentThread().getContextClassLoader().getClass().getClassLoader();
			kieServices.newKieBuilder(kieFileSystem, classLoader).buildAll(null);
		} catch (Exception e) {
			return false;
		}

		return initKieBase();

	}

	private boolean initKieBase() {
		if (releaseId != null) {
			// create container
			try {
				kieContainer = kieServices.newKieContainer(releaseId, classLoader);
				kScanner = kieServices.newKieScanner(kieContainer);
				kieBase = kieContainer.getKieBase("kie-base");

			} catch (Exception e) {
				return false;
			}

		} else {
			return false;
		}
		return true;
	}

	/*
	 * this method is responsible for creating the kieModule
	 */
	private void createKModule() {
		
		try {
			
		
		//get new kie Module
		kproj = kieServices.newKieModuleModel();

		//add kie base model 
		KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("kie-base").setDefault(true)
				.setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
				.setEventProcessingMode(EventProcessingOption.STREAM);

		//add kie session model
		kieBaseModel1.newKieSessionModel("kie-session").setDefault(true)
				.setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get("realtime"));
		kieFileSystem.writeKModuleXML(kproj.toXML());

		// set releaseId
		releaseId = kieServices.newReleaseId("io.je", "ruleengine", getReleaseVer());
		
		//generate pom file
		kieFileSystem.generateAndWritePomXML(releaseId);
		}
		catch (Exception e) {
			JELogger.error(LogConstants.unexpectedError + e.getMessage());
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
	 * delete rule  from kie file system
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
		}catch (Exception e) {
			JELogger.error(LogConstants.failedToUpdateContainer + e.getMessage());
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
	public void addRule(Rule rule) throws RuleCompilationException, RuleAlreadyExistsException {

		// check if rule already exists
		if (ruleExists(rule)) {
			throw new RuleAlreadyExistsException ("200","");
		}
		
		// compile rule
		compileRule(rule);
		allRules.put(rule.getName(), rule);

		// if project is running
		if (status == Status.RUNNING) {
			addRuleToKieFileSystem(rule);
			updateContainer();
		}

		

	}

	/*
	 * update rule in engine
	 */
	public boolean updateRule(Rule rule) throws RuleCompilationException, RuleAlreadyExistsException {

		//check that rule exists and add it if not
		if (!ruleExists(rule)) {
			addRule(rule);
			return true;
		}		
		// compile rule
		compileRule(rule);
		
		// update rule in map
		allRules.put(rule.getJobEngineElementID(), rule);
		
		//if project is running, update container without interrupting project
		if (status == Status.RUNNING) {
			try
			{
				deleteRuleFromKieFileSystem(rule);
				addRuleToKieFileSystem(rule);
				updateContainer();
			}catch (Exception e) {
				JELogger.error(LogConstants.failedToUpdateRule + e.getMessage());
				return false;
			}
			
		}
		return true;

		
	}

	/*
	 * delete rule from engine
	 */
	public boolean deleteRule(String ruleID)  {

		//check that rule exists 
		if (!ruleExists(ruleID)) {
			return true;
		}		
		
		// update rule in map
		Rule rule = allRules.get(ruleID);
		allRules.remove(ruleID);		
		//if project is running, update container without interrupting project
		if (status == Status.RUNNING) {
			try
			{
				deleteRuleFromKieFileSystem(rule);
				updateContainer();
			}catch (Exception e) {
				JELogger.error(LogConstants.failedToDeleteRule + e.getMessage());
				return false;
			}
			
		}
		return true;

		
	}
	
	
	/*
	 * this method compiles drl files and checks for errors in them
	 */
	public void compileRule(Rule rule) throws RuleCompilationException {

		// load rule content from rule path
		try {
			if (!RuleLoader.loadRuleContent(rule)) {

				throw new RuleCompilationException("", "");
			}
			String filename = generateResourceName(ResourceType.DRL, rule.getName());
			kfsToCompile.write(filename, rule.getContent());
			KieBuilder kieBuilder = kieServices.newKieBuilder(kfsToCompile, null).buildAll(null);
			Results results = kieBuilder.getResults();
			if (results.hasMessages(Message.Level.ERROR)) {
				JELogger.error(results.getMessages().toString());
				throw new RuleCompilationException("", "");
			}
			kfsToCompile.delete(filename);
		} catch (Exception e) {
			JELogger.error(LogConstants.ruleCompilationError + e.getMessage() );

		}

	}

	/*
	 * this method compiles all the rules in this project container
	 */
	public boolean compileAllRules() {
		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem, null).buildAll(null);
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			JELogger.error(results.getMessages().toString());
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
	public void addRules(List<Rule> rules) throws RuleCompilationException, RuleAlreadyExistsException {
		for(Rule rule : rules)
		{
			addRule(rule);
		}
		
	}

	/* 
	 * compile a list of rules
	 */
	public void compileRules(List<Rule> rules) {
		for(Rule rule : rules)
		{
			//TODO: implement method to compile a list of rules with 1 kie builder instance
		}
		
		
	}
	/* 
	 * delete a list of rules
	 */
	public void deleteRules(List<String> rulesIDs) {
		for(String rule : rulesIDs)
		{
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
			kieSession.insert(fact);
			JELogger.info("inserting fact");
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
}

enum Status {
	RUNNING, STOPPED,
}

enum BuildStatus {
	BUILT, UNBUILT,
}