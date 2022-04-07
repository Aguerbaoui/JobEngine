package io.je.rulebuilder.components.blocks.getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.annotation.Transient;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.GetterBlock;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class InstanceGetterBlock extends GetterBlock{
	

	List<String> attributeNames;
	HashMap<String,String> customOutputsIds ; //{ {speed:greaterBlock},{name:equalBlock} }
	
	@Transient
	HashMap<String,Block> customOutputs ; //{ {speed:greaterBlock},{name:equalBlock} }

	
	
	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		if (!alreadyScripted) {
			if (!inputBlocks.isEmpty()) {
				expression.append(inputBlocks.get(0).getExpression());
				expression.append("\n");

			}
			expression.append(getBlockNameAsVariable() + " : " + classPath);
			expression.append(" ( ");
			if (specificInstances != null && !specificInstances.isEmpty()) {
				expression.append("jobEngineElementID in ( " + getInstances() + ")");
				expression.append(" , ");

			}
			
			for(String attributeName : attributeNames)
			{
				
			}
			
			List<String> scriptedOutputs = new ArrayList<String>();
			for (var entry : customOutputs.entrySet()) {
				if(!scriptedOutputs.contains(entry.getValue().getJobEngineElementID()))
				{
					scriptedOutputs.add(entry.getValue().getJobEngineElementID());
					expression.append(entry.getKey());
					expression.append(" : ");
					expression.append(entry.getValue().getUnitExpression());
				}
					
			}
			
			
			
			expression.append(" ) ");
			setAlreadyScripted(true);
		}
		return expression.toString();

	
	}
	@Override
	public String getAsOperandExpression() throws RuleBuildFailedException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
