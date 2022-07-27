package io.licensemanager.utilities;

public class LicenseMessages {

	
	private LicenseMessages()
	{
		
	}
	
	public static final  String ERROR_RECEIVING_DATA = "Error occurred while receiving data.";
	public static final  String ERROR_SENDING_REQUEST = "Error occurred while sending request.";

	public static String licenseManagerUnreachable(String licenseManagerUrl )
	{
		return "The License Manager is not reachable. License Manager Address: "+licenseManagerUrl+".";
	}
	
	public static String initZMQError(String strError)
	{
		return "Error occurred while initializing ZMQ Request. Error: " + strError +".";
	}

	public static String initLicenseError(String strError)
	{
		return "Exception occurred while initializing license. Exception: "  + strError+".";
	}
}
