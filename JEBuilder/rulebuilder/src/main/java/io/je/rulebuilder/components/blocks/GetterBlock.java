package io.je.rulebuilder.components.blocks;

import java.util.List;

import io.je.rulebuilder.models.BlockModel;

public abstract class GetterBlock extends ConditionBlock {

	protected String classId;
	protected String classPath;
	protected 	List<String> specificInstances;

	
	public GetterBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
				blockModel.getDescription(),blockModel.getInputBlocksIds(),blockModel.getOutputBlocksIds());
	}
	
	/*
	 * returns the instances in the following format :
	 * instance1,instance2...,instancen
	 */
	protected String getInstances() {
		String instanceIds = "";
		instanceIds += "\"" + specificInstances.get(0) + "\"";
		for (int i = 1; i < specificInstances.size(); i++) {
			instanceIds += " , " + "\"" + specificInstances.get(i) + "\"";
		}
		return instanceIds;
	}
	

	public GetterBlock() {
		super();
	}

	@Override
	public String toString() {
		return "GetterBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID + ", jobEngineProjectID="
				+ jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}

}
