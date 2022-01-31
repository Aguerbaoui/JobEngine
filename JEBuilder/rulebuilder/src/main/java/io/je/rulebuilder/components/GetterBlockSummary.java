package io.je.rulebuilder.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;

public class GetterBlockSummary {
	String ruleId;
	Map<String, List<AttributeGetterBlock>> allGetterBlocksByClassId = new HashMap<>();
	List<String>specificBlocks = new ArrayList<String>();
	List<String>genericBlocks = new ArrayList<String>();

	
	
	public GetterBlockSummary(String ruleId) {
		super();
		this.ruleId = ruleId;
	}
	public Map<String, List<AttributeGetterBlock>> getAllGetterBlocksByClassId() {
		return allGetterBlocksByClassId;
	}
	public void setAllGetterBlocksByClassId(Map<String, List<AttributeGetterBlock>> allGetterBlocksByClassId) {
		this.allGetterBlocksByClassId = allGetterBlocksByClassId;
	}

	public void addBlock(AttributeGetterBlock getter)
	{
		if (!allGetterBlocksByClassId.containsKey(getter.getClassId())) {
			allGetterBlocksByClassId.put(getter.getClassId(), new ArrayList<>());
		}
		if (!allGetterBlocksByClassId.get(getter.getClassId()).contains(getter)) {
			allGetterBlocksByClassId.get(getter.getClassId()).add(getter);
			if(getter.getSpecificInstances()==null || getter.getSpecificInstances().isEmpty())
			{
				genericBlocks.add(getter.getJobEngineElementID() );
			}else {
				specificBlocks.add(getter.getJobEngineElementID() );
			}
		}
	}
	
	public boolean allBlocksAreGeneric()
	{
		return specificBlocks.isEmpty();
	}	
	
	public boolean allBlocksAreSpecific()
	{
		return genericBlocks.isEmpty();
	}	


}
