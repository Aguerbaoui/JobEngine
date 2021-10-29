package io.je.utilities.ruleutils;

public enum RuleStatus {
	ERROR("Error"),
	BUILDING("Building"),
	RUNNING("Running"),
	STOPPING("Stopping"),
	STOPPED("Stopped"),
	NOT_BUILT("Not Built"),
	RUNNING_NOT_UP_TO_DATE("Running/Not Updated");
	

    private final String name;       

    private RuleStatus(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
	
	

}
