package io.je.utilities.ruleutils;

public enum RuleStatus {
	ERROR("Error"),
	RUNNING("Running"),
	STOPPED("Stopped"),
	NOT_BUILT("New"),
	RUNNING_NOT_UP_TO_DATE("Not Updated");
	

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
