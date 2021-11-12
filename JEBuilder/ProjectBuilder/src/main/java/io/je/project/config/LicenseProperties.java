package io.je.project.config;

import io.je.project.listener.JELicenseStatusListener;
import io.je.utilities.exceptions.LicenseNotActiveException;
import io.je.utilities.log.JELogger;
import io.licensemanager.LicenseManager.ClientLicenseManager;
import io.licensemanager.common.enums.SIOTHLicenseStatus;
import io.licensemanager.utilities.InitResponse;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;

public class LicenseProperties {

	private static String licenseManagerUrl =  "tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode()
			+ ":" + SIOTHConfigUtility.getSiothConfig().getPorts().getSiothLicensePort();
	static SIOTHLicenseStatus licenseStatus = null;
	private static int jobEngineFeatureCode = 4920;

	public static void init() {

		JELogger.info("Checking License Status..", LogCategory.SIOTH_APPLICATION, "", LogSubModule.JEBUILDER, "");
		InitResponse response = ClientLicenseManager.initializeLicense(licenseManagerUrl, jobEngineFeatureCode, 0);

		if (!response.getErrorMsg().isEmpty()) {
			JELogger.error("Error occured while initializing license. Error: " + response.getErrorMsg(),
					LogCategory.SIOTH_APPLICATION, "", LogSubModule.JEBUILDER, "");

		}
	//	setLicenseStatus(response.getStatus());
	//	ClientLicenseManager.initListeners(jobEngineFeatureCode);
	//	ClientLicenseManager.register(new JELicenseStatusListener());

	}

	public static boolean licenseIsActive() {

		if (licenseStatus == null) {
			return false;
		}
		if ((licenseStatus == SIOTHLicenseStatus.Corrupted) || licenseStatus == SIOTHLicenseStatus.Backdated
				|| licenseStatus == SIOTHLicenseStatus.Expired) {
			return false;
		}
		return true;
	}

	public static void checkLicenseIsActive() throws LicenseNotActiveException {

		if (!licenseIsActive()) {
		//	JELogger.info("License is not active ", LogCategory.SIOTH_APPLICATION, "",
			//		LogSubModule.JEBUILDER, "");
			//throw new LicenseNotActiveException(String.valueOf(licenseStatus));
		}
	}

	public static void setLicenseStatus(SIOTHLicenseStatus status) {

		JELogger.info("License Status changed to " + status + ".", LogCategory.SIOTH_APPLICATION, "",
				LogSubModule.JEBUILDER, "");

		if ((status == SIOTHLicenseStatus.Corrupted) || status == SIOTHLicenseStatus.Backdated
				|| status == SIOTHLicenseStatus.Expired) {

			if (licenseStatus == null) {
				JELogger.error(
						" Job Engine is not authorized to run. License Status " + status
								+ ". Please Contact Integration Objects...",
						LogCategory.SIOTH_APPLICATION, "", LogSubModule.JEBUILDER, "");

			} else {
				JELogger.error(
						" Job Engine is no longer authorized to run. License Status " + status
								+ ". Please Contact Integration Objects...",
						LogCategory.SIOTH_APPLICATION, "", LogSubModule.JEBUILDER, "");

			}

		}
		licenseStatus = status;

	}

}
