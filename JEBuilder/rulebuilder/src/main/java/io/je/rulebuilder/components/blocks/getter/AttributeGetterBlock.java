package io.je.rulebuilder.components.blocks.getter;

import java.util.List;

import org.springframework.data.annotation.Transient;

import io.je.rulebuilder.components.blocks.GetterBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class AttributeGetterBlock extends GetterBlock {

	
	String attributeName;

	@Transient
	String primeJoinId;

	public AttributeGetterBlock(BlockModel blockModel) {
		super(blockModel);
		try {
			classId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID);
			classPath = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSNAME);
			attributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.ATTRIBUTENAME);
			specificInstances = (List<String>) blockModel.getBlockConfiguration()
					.get(AttributesMapping.SPECIFICINSTANCES);
			isProperlyConfigured = true;
		} catch (Exception e) {
			isProperlyConfigured = false;
		} finally {
			if (classId == null || classPath == null || attributeName == null) {
				isProperlyConfigured = false;

			}
		}

	}

	public AttributeGetterBlock() {
		super();
	}

	@Override
	public String toString() {
		return "AttributeGetterBlock [classPath=" + classPath + ", attributeName=" + attributeName + ", ruleId="
				+ ruleId + ", blockName=" + blockName + ", blockDescription=" + blockDescription
				+ ", jobEngineElementID=" + jobEngineElementID + ", jobEngineProjectID=" + jobEngineProjectID
				+ ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}

	



	public String getAttributeVariableName() {
		return getBlockNameAsVariable() + attributeName.replace(".", "");
	}

	/*
	 * returns drl expression example : $blockname : Person(id==2, $age:age)
	 */
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
			if (this.primeJoinId != null) {
				expression.append("jobEngineElementID == " + this.primeJoinId);
				expression.append(" , ");

			}
			expression.append(getAttributeVariableName() + " : " + getattributeGetterExpression());
			expression.append(" ) ");
			setAlreadyScripted(true);
		}
		return expression.toString();

	}



	

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	// TODO: remove this. All attribute names will starts with lowercase
	private String getattributeGetterExpression() {
		return attributeName;
		/*
		 * String s = ""; String str = attributeName; String[] a = str.split("\\.", 5);
		 * 
		 * for (int i = 0; i < a.length - 1; i++) { a[i] = a[i].substring(0,
		 * 1).toUpperCase() + a[i].substring(1); a[i] = "get" + a[i] + "()"; s = s +
		 * a[i] + "."; } a[a.length - 1] = a[a.length - 1].substring(0, 1).toUpperCase()
		 * + a[a.length - 1].substring(1); a[a.length - 1] = "get" + a[a.length - 1] +
		 * "()"; s = s + a[a.length - 1]; return s;
		 */
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public List<String> getSpecificInstances() {
		return specificInstances;
	}

	public void setSpecificInstances(List<String> specificInstances) {
		this.specificInstances = specificInstances;
	}

	@Override
	public void addSpecificInstance(String instanceId) {
		if (this.specificInstances.isEmpty()) {
			this.primeJoinId = instanceId;
		}
	}

	@Override
	public void removeSpecificInstance() {
		this.primeJoinId = null;
	}

}
