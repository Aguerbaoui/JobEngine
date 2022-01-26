package io.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Nodes {

    @JsonProperty("SIOTHMasterNode")
    private String siothMasterNode;



    public Nodes() {}

    public String getSiothMasterNode() {
        return siothMasterNode;
    }

    public void setSiothMasterNode(String siothMasterNode) {
        this.siothMasterNode = siothMasterNode;
    }

}
