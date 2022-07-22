package io.je.utilities.exceptions;

import io.je.utilities.constants.JEKeys;

public class JEException extends Exception {

    private static final long serialVersionUID = 1L;
    private int code;

    public JEException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    public JEException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    public static String getProjectIdMessage(String projectId) {
        return "[" + JEKeys.PROJECT_ID + "= " + projectId + "]";
    }

    public static String getRuleIdMessage(String ruleId) {
        return "[" + JEKeys.RULE_ID + "= " + ruleId + "]";

    }

    public static String getWorkflowIdMessage(String workflowId) {
        return "[" + JEKeys.WORKFLOW_ID + "= " + workflowId + "]";

    }

    public static String getEventIdMessage(String eventId) {
        return "[" + JEKeys.EVENT_ID + "= " + eventId + "]";

    }

    public static String getVariableIdMessage(String variableId) {
        return "[" + JEKeys.VARIABLE_ID + "= " + variableId + "]";

    }

    public static String getClassIdMessage(String classId) {
        return "[" + JEKeys.CLASS_ID + "= " + classId + "]";

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

