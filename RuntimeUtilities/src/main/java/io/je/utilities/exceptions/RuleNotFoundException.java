package io.je.utilities.exceptions;

import io.je.utilities.constants.JEKeys;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;

public class RuleNotFoundException extends JEException {

	private static final long serialVersionUID = -8105433593328558817L;

	public RuleNotFoundException( String projectId, String ruleId, String...optionalDescription) {
		super(ResponseCodes.RULE_NOT_FOUND,
				getProjectIdMessage(projectId) + getRuleIdMessage(ruleId) + " : " + JEMessages.RULE_NOT_FOUND +
				(optionalDescription.length==0? "" : JEKeys.ERROR_DESC +optionalDescription[0]));
		
	}
	


}
