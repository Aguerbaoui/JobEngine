package io.licensemanager.eventlistener;

import io.licensemanager.common.enums.SIOTHLicenseStatus;

public  interface LicenseStatusListener {
	
	public void onLicenseChange(SIOTHLicenseStatus status);
}
