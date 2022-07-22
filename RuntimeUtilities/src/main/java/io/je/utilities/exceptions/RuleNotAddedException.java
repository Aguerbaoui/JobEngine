package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class RuleNotAddedException extends JEException {

    public RuleNotAddedException(String message) {
        super(ResponseCodes.RULE_NOT_ADDED, message);

    }

}
