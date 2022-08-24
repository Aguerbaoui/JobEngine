package io.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Connectors {


    @JsonProperty("Protocols")
    public List<String> protocols;

    @JsonProperty("Data Stores")
    public List<String> databases;

    @JsonProperty("Network Watchers")
    public List<String> networkWatchers;

    @JsonProperty("Brokers")
    public List<String> brokers;


    private Connectors() {
        // TODO Auto-generated constructor stub
    }


    public List<String> getProtocols() {
        return protocols;
    }


    public void setProtocols(List<String> protocols) {
        this.protocols = protocols;
    }


    public List<String> getDatabases() {
        return databases;
    }


    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }


    public List<String> getNetworkWatchers() {
        return networkWatchers;
    }


    public void setNetworkWatchers(List<String> networkWatchers) {
        this.networkWatchers = networkWatchers;
    }


    public List<String> getBrokers() {
        return brokers;
    }


    public void setBrokers(List<String> brokers) {
        this.brokers = brokers;
    }


}
