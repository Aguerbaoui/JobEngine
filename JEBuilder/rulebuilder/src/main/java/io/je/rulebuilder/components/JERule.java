package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.utilities.runtimeobject.JEObject;

import java.util.ArrayList;
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
    List<Consequence> consequences;


    /*
     * Constructor
     */
    public JERule(String jobEngineElementID, String jobEngineProjectID) {
        super(jobEngineElementID, jobEngineProjectID);
        consequences = new ArrayList<>();
    }


    /*generate DRL for this rule */
    
	public void generateDRL()
	{
		// set rule attributes
        Map<String, String> ruleTemplateAttributes = new HashMap<>();
        ruleTemplateAttributes.put("ruleName", jobEngineElementID);
        ruleTemplateAttributes.put("salience", salience);
        
        
        ruleTemplateAttributes.put("ruleName", jobEngineElementID);
        ruleTemplateAttributes.put("ruleName", jobEngineElementID);
        
        ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();

        

		
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