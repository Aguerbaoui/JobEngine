package io.licensemanager.utilities;

import io.licensemanager.common.enums.SIOTHLicenseStatus;

public class InitResponse {
	private boolean licenseInitialized;
	private String  errorMsg;
	private SIOTHLicenseStatus status;

	
	public InitResponse(boolean licenseInitialized, String errorMsg, SIOTHLicenseStatus status) {
		super();
		this.licenseInitialized = licenseInitialized;
		this.errorMsg = errorMsg;
		this.status = status;
	}
	public boolean isLicenseInitialized() {
		return licenseInitialized;
	}
	public void setLicenseInitialized(boolean licenseInitialized) {
		this.licenseInitialized = licenseInitialized;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public SIOTHLicenseStatus getStatus() {
		return status;
	}
	public void setStatus(SIOTHLicenseStatus status) {
		this.status = status;
	}
	
	
	
	

}
