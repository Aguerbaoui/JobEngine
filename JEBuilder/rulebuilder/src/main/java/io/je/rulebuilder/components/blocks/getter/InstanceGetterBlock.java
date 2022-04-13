package io.je.rulebuilder.components.blocks.getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.rulebuilder.components.CustomBlockLink;
import io.je.rulebuilder.components.InstanceGetterBlockOutputIds;
import io.je.rulebuilder.components.blocks.GetterBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class InstanceGetterBlock extends GetterBlock{
	

	List<String> attributeNames;
	ArrayList<InstanceGetterBlockOutputIds> customOutputsIds ; //{ {speed:greaterBlock},{name:equalBlock} }
	
	@Transient
	HashMap<String,CustomBlockLink> customOutputs ; //{ {speed:greaterBlock},{name:equalBlock} }
	
	@Transient
	ObjectMapper mapper = new ObjectMapper();
	
	@Transient 
	List<String> additionalExpressions = new ArrayList<String>();
	
	public InstanceGetterBlock()
	{
		
	}
	
	public InstanceGetterBlock(BlockModel blockModel)
	{
		super(blockModel);
		try {
			classId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID);
			classPath = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSNAME);
			attributeNames = new ArrayList<String>();
			attributeNames.add("att1");
			attributeNames.add("att2");
			customOutputsIds = blockModel.getCustomOutputs();
			specificInstances = (List<String>) blockModel.getBlockConfiguration()
					.get(AttributesMapping.SPECIFICINSTANCES);
			isProperlyConfigured = true;
		} catch (Exception e) {
			isProperlyConfigured = false;
		} finally {
			if (classId == null || classPath == null ) {
				isProperlyConfigured = false;

			}
		}

	}

	public String getAttributeVariableName(String attributeName) {
		return getBlockNameAsVariable() + attributeName.replace(".", "");
	}
	
	@Override
	public  void addExpression(String additionalData) throws RuleBuildFailedException
	{
		if(additionalData!=null && !additionalData.equals(""))
		{
			additionalExpressions.add(additionalData);
		}
	}
	
	
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

			}
			
			//if all outputs are unique

				for(String attributeName : attributeNames)
				{
					expression.append(getAttributeVariableName(attributeName));
					expression.append(" : ");
					expression.append(attributeName);
					expression.append(" , ");


				}
				expression.replace(expression.length()-3, expression.length()-1, "");
				for (String exp : additionalExpressions)
				{
					expression.append(" , ");
					expression.append(exp);
				}

				
			
						
			expression.append(" ) ");
			setAlreadyScripted(true);
		}
		return expression.toString();

	
	}
	
	

	public void setAttributeNames(List<String> attributeNames) {
		this.attributeNames = attributeNames;
	}



	public ArrayList<InstanceGetterBlockOutputIds> getCustomOutputsIds() {
		return customOutputsIds;
	}

	public void setCustomOutputsIds(ArrayList<InstanceGetterBlockOutputIds> customOutputsIds) {
		this.customOutputsIds = customOutputsIds;
	}

	public HashMap<String, CustomBlockLink> getCustomOutputs() {
		return customOutputs;
	}

	public void setCustomOutputs(HashMap<String, CustomBlockLink> customOutputs) {
		this.customOutputs = customOutputs;
	}


	
	
}
