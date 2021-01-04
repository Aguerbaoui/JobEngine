package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.rulebuilder.config.RuleBuilderConfig;
import io.je.utilities.files.JEFileUtils;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.JEObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.template.ObjectDataCompiler;

/*
 * rule definition in job engine
 */
public class JERule extends JEObject {

	String salience;
	boolean enabled;
	String dateEffective;
	String dateExpires;
	String timer;
    ConditionBlockNode conditionBlockNode;
    List<Consequence> consequences ;
    

    /*
     * Constructor
     */
    public JERule(String jobEngineElementID, String jobEngineProjectID) {
        super(jobEngineElementID, jobEngineProjectID);
        consequences = new ArrayList<>();
    }


    /*generate DRL for this rule */
    
	public void generateDRL(String configPath)
	{
		// set rule attributes
        Map<String, String> ruleTemplateAttributes = new HashMap<>();
        ruleTemplateAttributes.put("ruleName", "rule_"+jobEngineElementID);
        ruleTemplateAttributes.put("salience", salience);   
        ruleTemplateAttributes.put("enabled", String.valueOf(enabled));        

        ruleTemplateAttributes.put("condition", conditionBlockNode.getRoot().getString(0, ""));        
        ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();
        String ruleContent = "";
        try {
            ruleContent = objectDataCompiler.compile(Arrays.asList(ruleTemplateAttributes), new FileInputStream(RuleBuilderConfig.ruleTemplatePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String fileName = configPath +"\\" + jobEngineElementID +".drl";
        JELogger.info(getClass(), ruleContent);
        JEFileUtils.copyStringToFile(ruleContent, fileName, "UTF-8");

		
	}




	public String getSalience() {
		return salience;
	}


	public void setSalience(String salience) {
		this.salience = salience;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	public String getDateEffective() {
		return dateEffective;
	}


	public void setDateEffective(String dateEffective) {
		this.dateEffective = dateEffective;
	}


	public String getDateExpires() {
		return dateExpires;
	}


	public void setDateExpires(String dateExpires) {
		this.dateExpires = dateExpires;
	}




	public String getTimer() {
		return timer;
	}


	public void setTimer(String timer) {
		this.timer = timer;
	}




	public ConditionBlockNode getCondition() {
		return conditionBlockNode;
	}


	public void setCondition(ConditionBlockNode conditionBlockNode) {
		this.conditionBlockNode = conditionBlockNode;
	}

	public void setCondition(PersistableBlock rootBlock) {
		this.conditionBlockNode = new ConditionBlockNode(rootBlock);
	}
	



	public List<Consequence> getConsequences() {
		return consequences;
	}


	public void setConsequences(List<Consequence> consequences) {
		this.consequences = consequences;
	}
}