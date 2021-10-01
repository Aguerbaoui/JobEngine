package io.je.project.listener;

import io.je.project.config.LicenseProperties;
import io.je.utilities.log.JELogger;
import io.licensemanager.common.enums.SIOTHLicenseStatus;
import io.licensemanager.eventlistener.LicenseStatusListener;
import utils.log.LogCategory;
import utils.log.LogSubModule;

public class JELicenseStatusListener implements LicenseStatusListener
{

	@Override
	public void onLicenseChange(SIOTHLicenseStatus status) {
				LicenseProperties.setLicenseStatus(status);
	}
	

}
