package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class RulesNotFiredException extends JEException {

    private static final long serialVersionUID = 4577636932130976595L;

    public RulesNotFiredException( String message) {
		super(ResponseCodes.RULE_NOT_FIRED,message);


    }


}
