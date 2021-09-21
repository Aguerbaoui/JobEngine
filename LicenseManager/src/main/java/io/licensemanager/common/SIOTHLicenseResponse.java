package io.licensemanager.common;

import io.licensemanager.common.enums.SIOTHLicenseStatus;

public  class SIOTHLicenseResponse
{


    public boolean bAuthorized;
    public SIOTHLicenseStatus Status; 
    public String Error;


    public SIOTHLicenseResponse()
    {
            
    }

    public SIOTHLicenseResponse(boolean authorized, SIOTHLicenseStatus status)
    {
        this.bAuthorized = authorized;
        this.Status = status;
    }


}
