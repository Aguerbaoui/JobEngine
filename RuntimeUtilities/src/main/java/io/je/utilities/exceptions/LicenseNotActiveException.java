package io.je.utilities.exceptions;

import io.je.utilities.constants.ResponseCodes;

public class LicenseNotActiveException extends JEException {

	private static final long serialVersionUID = -8105433593328558817L;

	public LicenseNotActiveException(String licenseStatus) {
		super(ResponseCodes.LICENSE_INACTIVE," Job Engine is not authorized to run. License Status " + licenseStatus
				+ ". Please Contact Integration Objects...");
		
	}
	


}
