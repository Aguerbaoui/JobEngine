package io.je.ruleengine.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
import org.kie.internal.io.ResourceFactory;

import io.je.ruleengine.interfaces.ProjectContainerInterface;
import io.je.ruleengine.kie.KieSessionManagerInterface;
import io.je.ruleengine.listener.RuleListener;
import io.je.ruleengine.loader.RuleLoader;
import io.je.ruleengine.models.Rule;
import io.je.ruleengine.utils.LogConstants;
import io.je.utilities.exceptions.RuleCompilationException;
import io.je.utilities.exceptions.RuleEngineBuildFailedException;
import io.je.utilities.exceptions.RulesNotFiredException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.JERuntimeObject;

/*
 * The Rule Engine supports multiple projects.
 * Each project is represented by a project container and is defined by 1 kie container and has its own rules and facts.
 */
public class ProjectContainer extends JERuntimeObject {

	// A project can be either running, or stopped.
	private Status status = Status.STOPPED;
	private BuildStatus buildStatus = BuildStatus.UNBUILT;

	// kie configuration
	private KieServices ks;
	private KieFileSystem kfs;
	KieFileSystem kfsToCompile = ks.newKieFileSystem();
	KieModuleModel kproj ;
	private KieContainer kieContainer;
	ReleaseId releaseId;
	KieScanner kScanner;
	KieBase kieBase;
	KieSession kieSession;
	private int releaseVersion = 1;
	private KieSessionManagerInterface kieManager;
	ClassLoader classLoader;

	// rules
	Map<String, Rule> allRules = new HashMap<>();
	private RuleListener ruleListener;

	/*
	 * Constructor : it requires a project Id
	 */
	public ProjectContainer(String id) {

		this.jobEngineProjectID = id;
		ks = KieServices.Factory.get();
		kfs = ks.newKieFileSystem();
		// create kmodule ( TODO: add config to kie config class )
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
	public boolean buildProject() throws RuleEngineBuildFailedException {
		JELogger.info(LogConstants.buildingProjectContainer);
		if (!buildKie()) {
			JELogger.error(LogConstants.buildingProjectContainerFailed);
			throw new RuleEngineBuildFailedException("200", LogConstants.buildingProjectContainerFailed);			
		}
		JELogger.info(LogConstants.buildingProjectContainerSuccessful);
		buildStatus = BuildStatus.BUILT;
		return true;
	}

	public boolean fireRules() throws RulesNotFiredException, RuleEngineBuildFailedException {

		// if project is not built
		if (buildStatus == BuildStatus.UNBUILT) {
			buildProject();
		}
		try {
			kieSession = kieBase.newKieSession();
			new Thread(new Runnable() {
				@Override
				public void run() {
					kieSession.fireUntilHalt();
				}
			}).start();
			status = Status.RUNNING;
			JELogger.error(LogConstants.failedToFireRules);

		} catch (Exception e) {
			throw new RulesNotFiredException("200", "");
		}
		
		return true;

	}

	public boolean stopRuleExecution() {
		
		try {

			kieSession.halt();
			status = Status.STOPPED;

		} catch (Exception e) {
			
		}
		return true;
	}

	/* --------------------------------------------------------KIE CONFIGURATION ---------------------------------------------------------------*/

	/*
	 * This method initialises the kie parameters. It is mainly responsible for
	 * creating the project's kie container.
	 */
	private boolean buildKie() {
		
		if(allRules.isEmpty())
		{
			return false;
		}
		
		// add rules to kfs
			addAllRulesToKieFileSystem();
		
		
		// build all rules
		try {
			classLoader = Thread.currentThread().getContextClassLoader().getClass().getClassLoader();
			ks.newKieBuilder(kfs, classLoader).buildAll(null);
		} catch (Exception e) {
			return false;
		}
		
		if(!initKieBase())
		{
			return false;
		}
		

		return true;

	}
	
	private boolean initKieBase() {
		if (releaseId != null) {
			// create container
			try {
				kieContainer = ks.newKieContainer(releaseId, classLoader);
				kScanner = ks.newKieScanner(kieContainer);
				kieBase = kieContainer.getKieBase("kie-base");

			} catch (Exception e) {
				return false;
			}

		} else {
			return false;
		}
		return true;
	}

	private void createKModule()
	{
	    kproj = ks.newKieModuleModel();

		KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("kie-base").setDefault(true)
				.setEqualsBehavior(EqualityBehaviorOption.IDENTITY)
				.setEventProcessingMode(EventProcessingOption.STREAM);

		kieBaseModel1.newKieSessionModel("kie-session" + jobEngineProjectID).setDefault(true)
				.setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get("realtime"));
		kfs.writeKModuleXML(kproj.toXML());


		// set releaseId
		releaseId = ks.newReleaseId("io.je", "ruleengine", getReleaseVer());
		kfs.generateAndWritePomXML(releaseId);
		
	}
	

	private boolean addAllRulesToKieFileSystem() {
		
		for (Rule rule : allRules.values()) {
			addRuleToKieFileSystem(rule);
			
		}
		return true;
	}

	private boolean addRuleToKieFileSystem(Rule rule) {

		try
		{
			kfs.write(generateResourceName(ResourceType.DRL, rule.getName()), rule.getContent());

		}
		catch (Exception e) {
			return false;
		}

		return true;
	}

	private String getReleaseVer() {
		return "0.0." + releaseVersion++;
	}

	public boolean  updateContainer() {

		return false;
	}

	/*
	 * -------------------------------------------------------- RULE MANAGEMENT
	 * ---------------------------------------------------------------
	 */

	public boolean ruleExists(Rule rule) {
		return allRules.containsKey(rule.getJobEngineElementID());
	}

	/*
	 * This method adds a rule to a project container
	 */
	public boolean  addRule(Rule rule) throws RuleCompilationException {


		
			// check if rule already exists
			if (allRules.containsKey(rule.getJobEngineElementID())) {
				return false;

			}
			// compile rule
			
			if (!compileRule(rule)) {
				
				allRules.put(rule.getName(), rule);
		
				// if project is running
				if (status == Status.RUNNING) {
					// upgradeKieConfig()
				}

			}
	
		return true;

	}

	public boolean  updateRule(Rule rule) throws RuleCompilationException {
		
		if (!ruleExists(rule)) {
			addRule(rule);
		}
		// check if project status = running
		// if project stopped:
		// reloadloadRuleContent
		// update rule in allRules

		return true;
	}

	public boolean  compileRule(Rule rule) throws RuleCompilationException{

		// load rule
		try {
			RuleLoader ruleLoader = new RuleLoader();
			if (!ruleLoader.loadRuleContent(rule)) {
	
				return false;
			}
		
			/*if (!ruleLoader.writeRule(rule.getContent(), rule.getName())) {
				
				return false;
			}
			*/
			
			String filename = generateResourceName(ResourceType.DRL, rule.getName());
			kfsToCompile.write(filename, rule.getContent());
			KieBuilder kieBuilder = ks.newKieBuilder(kfsToCompile, null).buildAll(null);
			Results results = kieBuilder.getResults();
			if (results.hasMessages(Message.Level.ERROR)) {
				JELogger.error(results.getMessages().toString());
				 throw new RuleCompilationException("","");
			}
			kfsToCompile.delete(filename);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return true;
	}

	public boolean  compileAllRules() {
		KieBuilder kieBuilder = ks.newKieBuilder(kfs, null).buildAll(null);
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			throw new RuntimeException(results.getMessages().toString());
		}

		return false;

	}

	private void loadRule(String fileName) {
		kfs.write(ResourceFactory.newFileResource(fileName));
	}

	private boolean writeRule(String rule, String filename) throws IOException {
		try (FileWriter fileWriter = new FileWriter(new File(filename));
				BufferedWriter writer = new BufferedWriter(fileWriter)) {
			writer.write(rule);
			writer.flush();
			writer.close();
			return true;
		} catch (IOException e) {
			JELogger.error("Problem writing file:  " + filename);
			throw e;
		}
	}

	private String generateResourceName(ResourceType type, String ruleName) {
		return "src/main/resources/" + ruleName + "." + type.getDefaultExtension();

	}

	public boolean  addRule(String rulePath, String ruleId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean  updateRule(String ruleId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean  deleteRule(String ruleId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean  insertFact(JERuntimeObject fact) {
		if (status == Status.RUNNING) {
			kieSession.insert(fact);
			JELogger.info("inserting fact");
		}

		return false;
	}

	public boolean  addRules(List<Rule> rules) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean  compileRules(List<Rule> rules) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean  deleteRules(List<String> rulesIDs) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean  disableRule(String ruleID) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean  enableRule(String ruleID) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean  retractFact(JERuntimeObject Fact) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean  updateFact(JERuntimeObject Fact) {
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