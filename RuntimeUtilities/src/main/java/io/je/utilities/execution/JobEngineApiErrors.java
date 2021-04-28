package io.je.utilities.execution;

public enum JobEngineApiErrors {
    JERunnerException("JERunnerException", 1),
    NoError("NoError", 0);

    public final String exceptionName;

    public final int exceptionCode;


    JobEngineApiErrors(String exceptionName, int exceptionCode) {
        this.exceptionName = exceptionName;
        this.exceptionCode = exceptionCode;
    }
}
