package io.je.rulebuilder.components;

import java.util.HashMap;
import java.util.Map;

import io.je.rulebuilder.builder.BlockBuilder;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.models.BlockModel;

/*
 * Format in which rules are saved before build;
 */

public class ComposedRule {
	
	String projectId;
	String ruleId;
	String salience;
	boolean enabled;
	String dateEffective;
	String dateExpires;
	String duration;
	String timer;
	String calendar;
	Map<String,Block> blocks = new HashMap<>();
	
	public ComposedRule()
	{
		
	}
	
	public void addBlock(BlockModel blockModel)
	{
		Block block = BlockBuilder.createBlock(blockModel);
		blocks.put(block.getJobEngineElementID(), block);
	}
	
	public void updateBlock(Block block)
	{
		
	}
	
	public void deletBlock(String blockId)
	{
		
	}
	
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
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
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getTimer() {
		return timer;
	}
	public void setTimer(String timer) {
		this.timer = timer;
	}
	public String getCalendar() {
		return calendar;
	}
	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}
	
	

}
