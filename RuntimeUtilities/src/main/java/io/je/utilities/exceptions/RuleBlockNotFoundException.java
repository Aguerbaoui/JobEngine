package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class RuleBlockNotFoundException extends JEException {

    public RuleBlockNotFoundException(String message) {
        super(ResponseCodes.RULE_BLOCK_NOT_FOUND, message);

    }

}
