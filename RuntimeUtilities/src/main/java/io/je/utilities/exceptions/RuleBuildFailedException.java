package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class RuleBuildFailedException extends JEException {

    /**
     *
     */
    private static final long serialVersionUID = -5418747468040739097L;

    public RuleBuildFailedException( String message) {
		super(ResponseCodes.RULE_BUILD_FAILED,message);


    }

}
