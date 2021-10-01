package io.je.project.listener;

import io.je.project.config.LicenseProperties;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.licensemanager.common.enums.SIOTHLicenseStatus;
import io.licensemanager.eventlistener.LicenseStatusListener;

public class JELicenseStatusListener implements LicenseStatusListener
{

	@Override
	public void onLicenseChange(SIOTHLicenseStatus status) {
				LicenseProperties.setLicenseStatus(status);
	}
	

}
