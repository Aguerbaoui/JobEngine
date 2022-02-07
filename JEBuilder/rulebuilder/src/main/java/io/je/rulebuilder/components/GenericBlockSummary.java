package io.je.rulebuilder.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.je.rulebuilder.components.blocks.GenericBlockSet;
import io.je.rulebuilder.components.blocks.execution.LinkedSetterBlock;
import io.je.rulebuilder.components.blocks.execution.SetterBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;

public class GenericBlockSummary {
	String ruleId;
	//all generic blocks
	Map<String, GenericBlockSet> allGenericBlocks = new HashMap<>();
	List<String>specificBlocks = new ArrayList<String>();
	List<String>genericBlocks = new ArrayList<String>();

	public int getNumberOfClasses()
	{
		return allGenericBlocks.size();
	}
	
	public GenericBlockSummary(String ruleId) {
		super();
		this.ruleId = ruleId;
	}
	public Map<String, GenericBlockSet> getAllAttributeBlocks() {
		return allGenericBlocks;
	}
	public void setAllAttributeBlocks(Map<String, GenericBlockSet> allGetterBlocksByClassId) {
		this.allGenericBlocks = allGetterBlocksByClassId;
	}

	public void addGetterBlock(AttributeGetterBlock getter)
	{
		if(getter.getSpecificInstances()==null || getter.getSpecificInstances().isEmpty())
		{
			if(!allGenericBlocks.containsKey(getter.getClassId()))
			{
				allGenericBlocks.put(getter.getClassId(), new GenericBlockSet(getter.getClassId()));
			}
			if(!allGenericBlocks.get(getter.getClassId()).contains(getter))
			{
				allGenericBlocks.get(getter.getClassId()).add(getter);
			}
			genericBlocks.add(getter.getJobEngineElementID());
		}else
		{
			specificBlocks.add(getter.getJobEngineElementID());
		}
	}
	
	public void addSetterBlock(SetterBlock setter)
	{
		
		if(setter.isGeneric())
		{
			if(!allGenericBlocks.containsKey(setter.getDestinationClassId()))
			{
				allGenericBlocks.put(setter.getDestinationClassId(), new GenericBlockSet(setter.getDestinationClassId()));
			}
			if(!allGenericBlocks.get(setter.getDestinationClassId()).contains(setter))
			{
				allGenericBlocks.get(setter.getDestinationClassId()).add(setter);
			}
			genericBlocks.add(setter.getJobEngineElementID());
		}else
		{
			specificBlocks.add(setter.getJobEngineElementID());
		}
	}
	
	public void addSetterBlock(LinkedSetterBlock setter) {
		
		if(setter.isGeneric())
		{
			if(!allGenericBlocks.containsKey(setter.getClassId()))
			{
				allGenericBlocks.put(setter.getClassId(), new GenericBlockSet(setter.getClassId()));
			}
			if(!allGenericBlocks.get(setter.getClassId()).contains(setter))
			{
				allGenericBlocks.get(setter.getClassId()).add(setter);
			}
			genericBlocks.add(setter.getJobEngineElementID());
		}else
		{
			specificBlocks.add(setter.getJobEngineElementID());
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
	public GenericBlockSet getSingleBlockSet() {
		return  allGenericBlocks.entrySet().iterator().next().getValue();
	}
	


}
