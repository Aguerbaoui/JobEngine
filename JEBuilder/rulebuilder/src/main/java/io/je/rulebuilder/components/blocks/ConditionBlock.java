package io.je.rulebuilder.components.blocks;

import java.util.ArrayList;
import java.util.List;

import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;


/*
 * blocks used to define a rule's condition
 */
public abstract class ConditionBlock extends Block {

	
	

    // persistence in time
    boolean timePersistenceOn;
    int timePersistenceValue;
    TimePersistenceUnit timePersistenceUnit;
    List<String> inputs = new ArrayList<>();

    public ConditionBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, boolean timePersistenceOn,
                          int timePersistenceValue, TimePersistenceUnit timePersistenceUnit) {
        super(jobEngineElementID, jobEngineProjectID, ruleId);
        this.timePersistenceOn = timePersistenceOn;
        this.timePersistenceValue = timePersistenceValue;
        this.timePersistenceUnit = timePersistenceUnit;
    }

	public boolean isTimePersistenceOn() {
		return timePersistenceOn;
	}

	public void setTimePersistenceOn(boolean timePersistenceOn) {
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

	public List<String> getInputs() {
		return inputs;
	}

	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}
    
    


}

