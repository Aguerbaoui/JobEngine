package io.je.rulebuilder.models;


import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum Operator {
    EQUALS("==", 2001),
    DIFFERENT("!=", 2002),
    GREATER(">", 2003),
    GREATER_OR_EQUALS(">=", 2004),
    LESS_THAN("<", 2005),
    LESS_THAN_OR_EQUALS("<=", 2006),
    CONTAINS(" contains ", 2007),
    NOT_CONTAINS(" not contains ", 2008),
    MATCHES(" matches ", 2009),
    NOT_MATCHES(" not matches ", 2010),
    SOUNDS_LIKE(" soundslike ", 2011),
    STARTS_WITH(" str[startsWith] ", 2012),
    END_WITH(" str[endsWith] ", 2013),
    AFTER(">", 2014),
    BEFORE("<", 2015);
    private final String full;
    private final int code;


    private Operator(String full, int code) {
        this.full = full;
        this.code = code;
    }

    public static Boolean isStringOperator(int operation) {
        return (operation >= 2007 && operation <= 2013);

    }

    // Reverse lookup methods
    public static Optional<Operator> getOperatorByCode(int code) {
        return Arrays.stream(Operator.values())
                .filter(operator -> operator.code == code)
                .findFirst();
    }

    public static Optional<Operator> getOperatorByName(String operation) {
        return Arrays.stream(Operator.values())
                .filter(operator -> Objects.equals(operator.full, operation))
                .findFirst();
    }

    public String getFullName() {
        return full;
    }

    public Integer getCode() {
        return code;
    }

}

