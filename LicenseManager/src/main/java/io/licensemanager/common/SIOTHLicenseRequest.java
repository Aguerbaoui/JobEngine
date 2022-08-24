package io.licensemanager.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SIOTHLicenseRequest {

    @JsonProperty("RequestType")
    public String requestType;

    @JsonProperty("RequestedMachine")
    public String requestedMachine;

    @JsonProperty("RequestedProcess")
    public String requestedProcess;

    @JsonProperty("RequestedFeature")
    public int requestedFeature;

    @JsonProperty("RequestedTags")
    public int requestedTags;

    public SIOTHLicenseRequest() {

    }

    public SIOTHLicenseRequest(String requestType, String requestedMachine, int requestedFeature) {
        this.requestType = requestType;
        this.requestedMachine = requestedMachine;
        this.requestedFeature = requestedFeature;

    }

    public SIOTHLicenseRequest(String requestType, String requestedMachine, String requestedProcess,
                               int requestedFeature, int requestedTags) {
        this.requestType = requestType;
        this.requestedMachine = requestedMachine;
        this.requestedProcess = requestedProcess;
        this.requestedFeature = requestedFeature;
        this.requestedTags = requestedTags;

    }

}
