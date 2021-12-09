package io.je.project.listener;

import io.je.project.config.LicenseProperties;
import io.licensemanager.common.enums.SIOTHLicenseStatus;
import io.licensemanager.eventlistener.LicenseStatusListener;

public class JELicenseStatusListener implements LicenseStatusListener
{

	@Override
	public void onLicenseChange(SIOTHLicenseStatus status) {
				LicenseProperties.setLicenseStatus(status);
	}
	

}
