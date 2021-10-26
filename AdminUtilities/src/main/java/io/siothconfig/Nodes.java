package io.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Nodes {

    @JsonProperty("SIOTHMasterNode")
    private String siothMasterNode;

    @JsonProperty("VM1")
    private String vm1;

    public Nodes() {}

    public String getSiothMasterNode() {
        return siothMasterNode;
    }

    public void setSiothMasterNode(String siothMasterNode) {
        this.siothMasterNode = siothMasterNode;
    }

    public String getVm1() {
        return vm1;
    }

    public void setVm1(String vm1) {
        this.vm1 = vm1;
    }
}
