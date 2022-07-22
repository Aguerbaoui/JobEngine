package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class AddRuleBlockException extends JEException {


    public AddRuleBlockException(String message) {
        super(ResponseCodes.ADD_RULE_BLOCK, message);
    }

}
