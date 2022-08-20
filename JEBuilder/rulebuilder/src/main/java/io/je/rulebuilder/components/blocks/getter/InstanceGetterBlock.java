package io.je.rulebuilder.components.blocks.getter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.rulebuilder.components.blocks.GetterBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;
import org.springframework.data.annotation.Transient;
import utils.log.LoggerUtils;

import java.util.List;

public class InstanceGetterBlock extends GetterBlock {


    List<String> attributeNames;


    @Transient
    ObjectMapper mapper = new ObjectMapper();


    public InstanceGetterBlock() {

    }


    public InstanceGetterBlock(BlockModel blockModel) {
        super(blockModel);
        try {
            classId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID);
            classPath = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSNAME);
            attributeNames = (List<String>) blockModel.getBlockConfiguration().get("attribute_name");
            specificInstances = (List<String>) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.SPECIFICINSTANCES);
            isProperlyConfigured = true;
        } catch (Exception e) {
            isProperlyConfigured = false;
        } finally {
            if (classId == null || classPath == null) {
                isProperlyConfigured = false;

            }
        }

    }

    @Override
    public String getReference(String optional) {
        return getBlockNameAsVariable() + optional;
    }

    /*
     * return example $blockName: Person( $age Keywords.toBeReplaced )
     */
    @Override
    public String getAsOperandExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        // FIXME check if no regressions
        //if (!alreadyScripted) {
            // input blocks can be an event block
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

            for (String attributeName : attributeNames) {
                expression.append(getAttributeVariableName(attributeName));
                expression.append(" : ");
                expression.append(attributeName);
                expression.append(" , ");
            }

            // FIXME two lines below needed?
            expression.replace(expression.length() - 3, expression.length() - 1, "");
            expression.append(" , ");

            expression.append(Keywords.toBeReplaced);
            expression.append(" ) ");
            setAlreadyScripted(true);
        //}
        return expression.toString();
    }


    public String getAttributeVariableName(String attributeName) {
        return getBlockNameAsVariable() + attributeName.replace(".", "");
    }


    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        // FIXME check if no regressions
        //if (!alreadyScripted) {

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

            //if all outputs are unique

            for (String attributeName : attributeNames) {
                expression.append(getAttributeVariableName(attributeName));
                expression.append(" : ");
                expression.append(attributeName);
                expression.append(" , ");

            }

            expression.replace(expression.length() - 3, expression.length() - 1, "");

            expression.append(" ) ");

            //setAlreadyScripted(true);
        //}
        return expression.toString();

    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        // FIXME
        StringBuilder expression = new StringBuilder();

        expression.append("\n not ( " + this.getExpression() + " ) \n");

        return expression.toString();
    }

    public void setAttributeNames(List<String> attributeNames) {
        this.attributeNames = attributeNames;
    }


    public List<String> getSpecificInstances() {
        return specificInstances;
    }

    public void setSpecificInstances(List<String> specificInstances) {
        this.specificInstances = specificInstances;
    }


}
