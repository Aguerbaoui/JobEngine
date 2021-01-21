package io.je.project.services;

import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import io.je.project.beans.JEProject;
import io.je.rulebuilder.builder.RuleBuilder;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.ScriptedRule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.RuleModel;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.RuleBuilderErrors;
import io.je.utilities.exceptions.AddRuleBlockException;
import io.je.utilities.exceptions.JERunnerUnreachableException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleBlockNotFoundException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleNotAddedException;
import io.je.utilities.exceptions.RuleNotFoundException;

/*
 * Service class to handle business logic for rules
 */
@Service
public class RuleService {

	/*
	 * creates a new rule from the Rule Model
	 */
	private static UserDefinedRule createRule(String projectId,RuleModel rule) throws RuleNotAddedException {

		return new UserDefinedRule(projectId,rule);
	}

	/*
	 * Add a rule to a project
	 */
	public void addRule(String projectId,RuleModel ruleModel)
			throws ProjectNotFoundException, RuleNotAddedException, RuleAlreadyExistsException {
		UserDefinedRule rule = createRule(projectId,ruleModel);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		} else if (project.ruleExists(ruleModel.getRuleId())) {
			throw new RuleAlreadyExistsException( RuleBuilderErrors.RuleAlreadyExists);
		}
		project.addRule(rule);
	}
	

	
	
	
	/*
	 * delete rule from a project
	 */
	public void deleteRule(String projectId,String ruleId) throws ProjectNotFoundException, RuleNotFoundException
	{
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		} else if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException( RuleBuilderErrors.RuleNotFound);
		}
		project.deleteRule(ruleId);
	}
	
	/*
	 * update rule : update rule attributes
	 * TODO: individual update function for each attribute
	 */
	public void updateRule(String projectId, RuleModel ruleModel) throws RuleNotAddedException, ProjectNotFoundException, RuleNotFoundException {
		UserDefinedRule rule = createRule(projectId,ruleModel);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		} else if (!project.ruleExists(ruleModel.getRuleId())) {
			throw new RuleNotFoundException( RuleBuilderErrors.RuleNotFound);
		}
		project.updateRuleAttributes(rule);
		
	}

	/*
	 * update rule : add block to rule
	 */
	public void addBlockToRule(BlockModel blockModel) throws AddRuleBlockException, ProjectNotFoundException, RuleNotFoundException {

		
		if (blockModel.getProjectId() == null) {
			throw new AddRuleBlockException( RuleBuilderErrors.BlockProjectIdentifierIsEmpty);
		}

		if (blockModel.getRuleId() == null) {
			throw new AddRuleBlockException( RuleBuilderErrors.BlockRuleIdentifierIsEmpty);
		}

		JEProject project = ProjectService.getProjectById(blockModel.getProjectId());
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		} else if (!project.ruleExists(blockModel.getRuleId())) {
			throw new RuleNotFoundException( RuleBuilderErrors.RuleNotFound);
		}
		project.addBlockToRule(blockModel);
	}
	
	
	/*
	 * update rule : update a block in the rule
	 */
	public void updateBlock(BlockModel blockModel) throws AddRuleBlockException, ProjectNotFoundException, RuleNotFoundException {
		if (blockModel.getProjectId() == null) {
			throw new AddRuleBlockException( RuleBuilderErrors.BlockProjectIdentifierIsEmpty);
		}

		if (blockModel.getRuleId() == null) {
			throw new AddRuleBlockException( RuleBuilderErrors.BlockRuleIdentifierIsEmpty);
		}

		JEProject project = ProjectService.getProjectById(blockModel.getProjectId());
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		} else if (!project.ruleExists(blockModel.getRuleId())) {
			throw new RuleNotFoundException( RuleBuilderErrors.RuleNotFound);
		}
		project.updateRuleBlock(blockModel);
		
	}
	
	
	/*
	 * delete block
	 */
	public void deleteBlock(String projectId, String ruleId, String blockId) throws  ProjectNotFoundException, RuleNotFoundException, RuleBlockNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		} else if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException( RuleBuilderErrors.RuleNotFound);
		}
		project.deleteRuleBlock(ruleId, blockId);
		
	}
	
	
	
	
	
	/*
	 * build rule : create drl + check for compilation errors
	 */
	public void buildRule(String projectId, String ruleId) throws ProjectNotFoundException, RuleNotFoundException, RuleBuildFailedException, JERunnerUnreachableException, IOException
	{
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		} else if (!project.ruleExists(ruleId)) {
			throw new RuleNotFoundException( RuleBuilderErrors.RuleNotFound);
		}
		    	RuleBuilder.buildRule(project.getRule(ruleId), project.getConfigurationPath());

		
	}


	
	/*
	 * build rule : create drl + check for compilation errors + add to jerunner
	 * TODO: handle the case where some rules are built while others aren't 
	 */
	public void buildRules(String projectId) throws ProjectNotFoundException, RuleBuildFailedException, JERunnerUnreachableException, IOException
	{
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		}
			for (Entry<String, JERule> entry : project.getRules().entrySet()) {
			    String key = entry.getKey();
			    RuleBuilder.buildRule(project.getRules().get(key), project.getConfigurationPath());
			   
			    
			}


		
	}
	


	/*
	 * Retrieve list of all rules that exist in a project.
	 */
	public Collection<JERule> getAllRules(String projectId) throws ProjectNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		}
		return project.getRules().values();
	}

	public JERule getRule(String projectId, String ruleId) throws ProjectNotFoundException, RuleNotFoundException {
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		
		} else if (!project.ruleExists(ruleId)) {
		throw new RuleNotFoundException( RuleBuilderErrors.RuleNotFound);
	}
		return project.getRules().get(ruleId);
	}

	/*
	 * add scripted rule
	 */
	public void addScriptedRule(String projectId, String ruleId, String script) throws ProjectNotFoundException, RuleAlreadyExistsException {
		ScriptedRule rule = new ScriptedRule(projectId,ruleId,script);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		} 
		project.addRule(rule);
		
	}

	
	/*
	 * update scripted rule
	 */
	public void updateScriptedRule(String projectId, String ruleId, String script) throws ProjectNotFoundException, RuleNotFoundException {
		ScriptedRule rule = new ScriptedRule(projectId,ruleId,script);
		JEProject project = ProjectService.getProjectById(projectId);
		if (project == null) {
			throw new ProjectNotFoundException( Errors.projectNotFound);
		}
		project.updateRule(rule);
		
	}



}
