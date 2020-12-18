package io.je.rulebuilder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;
import io.je.rulebuilder.config.AttributesMapping;

import java.util.List;

public class BlockModel {

    String jobEngineId;

    @JsonProperty(AttributesMapping.PROJECTID)
    String projectId;

    @JsonProperty(AttributesMapping.RULEID)
    String ruleId;

    @JsonProperty(AttributesMapping.BLOCKID)
    String blockId;

    @JsonProperty(AttributesMapping.BLOCKTYPE)
    String blockType;

    @JsonProperty(AttributesMapping.TIMEPERSISTENCEON)
    String timePersistenceOn;

    @JsonProperty(AttributesMapping.TIMEPERSISTENCEVALUE)
    int timePersistenceValue;

    @JsonProperty(AttributesMapping.TIMEPERSISTENCEUNIT)
    TimePersistenceUnit timePersistenceUnit;

    @JsonProperty(AttributesMapping.FIRSTOPERAND)
    List<String> operandIds;


    @JsonProperty(AttributesMapping.OPERATIONID)
    String operatorId;


    /*
     * constructor : TODO: generate unique job engine id
     */
    private BlockModel() {
        try {
            jobEngineId = projectId + "_" + ruleId + "_" + blockId;
        } catch (Exception e) {
            // TODO: handle exception

        }
    }


    public String getJobEngineId() {
        return jobEngineId;
    }


    public void setJobEngineId(String jobEngineId) {
        this.jobEngineId = jobEngineId;
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


    public String getBlockId() {
        return blockId;
    }


    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }


    public String getBlockType() {
        return blockType;
    }


    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }


    public String getTimePersistenceOn() {
        return timePersistenceOn;
    }


    public void setTimePersistenceOn(String timePersistenceOn) {
        this.timePersistenceOn = timePersistenceOn;
    }


    public int getTimePersistenceValue() {
        return timePersistenceValue;
    }


    public void setTimePersistenceValue(int timePersistenceValue) {
        this.timePersistenceValue = timePersistenceValue;
    }


    public TimePersistenceUnit getTimePersistenceUnit() {
        return timePersistenceUnit;
    }


    public void setTimePersistenceUnit(TimePersistenceUnit timePersistenceUnit) {
        this.timePersistenceUnit = timePersistenceUnit;
    }


    public List<String> getOperandIds() {
        return operandIds;
    }


    public void setOperandIds(List<String> operandIds) {
        this.operandIds = operandIds;
    }


    public String getOperatorId() {
        return operatorId;
    }


    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }


}