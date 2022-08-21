package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

import java.util.List;

// FIXME rename to ClassGetterBlock
public abstract class GetterBlock extends ConditionBlock {

    protected String classId;
    protected String classPath;
    protected List<String> specificInstances;


    public GetterBlock(BlockModel blockModel) {
        super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
                blockModel.getDescription(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());
    }

    public GetterBlock() {
        super();
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

    @Override
    public String toString() {
        return "GetterBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID + ", jobEngineProjectID="
                + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public List<String> getSpecificInstances() {
        return specificInstances;
    }

    public void setSpecificInstances(List<String> specificInstances) {
        this.specificInstances = specificInstances;
    }


}
