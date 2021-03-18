package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class ConfigException extends JEException {

    public ConfigException( String message) {
		super(ResponseCodes.CONFIG_EXCEPTION,message);

    }


}
