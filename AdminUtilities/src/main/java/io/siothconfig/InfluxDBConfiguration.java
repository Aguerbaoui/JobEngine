package io.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InfluxDBConfiguration {

    @JsonProperty("InfluxDBURL")
    public String influxDBURL;

    @JsonProperty("InfluxUserName")
    public String influxUserName;

    @JsonProperty("InfluxDBPassword")
    public String influxDBPassword;

    @JsonProperty("StoreAndForwardDBName")
    public String storeAndForwardDBName;

    @JsonProperty("LogMeasurementName")
    public String logMeasurementName;

    @JsonProperty("MonitoringMeasurementName")
    public String monitoringMeasurementName;

    @JsonProperty("DataModelMeasurementName")
    public String dataModelMeasurementName;

    @JsonProperty("InfluxRetentionDuration")
    public String influxRetentionDuration;

    private InfluxDBConfiguration() {
        // TODO Auto-generated constructor stub
    }

    public String getInfluxDBURL() {
        return influxDBURL;
    }

    public void setInfluxDBURL(String influxDBURL) {
        this.influxDBURL = influxDBURL;
    }

    public String getInfluxUserName() {
        return influxUserName;
    }

    public void setInfluxUserName(String influxUserName) {
        this.influxUserName = influxUserName;
    }

    public String getInfluxDBPassword() {
        return influxDBPassword;
    }

    public void setInfluxDBPassword(String influxDBPassword) {
        this.influxDBPassword = influxDBPassword;
    }

    public String getStoreAndForwardDBName() {
        return storeAndForwardDBName;
    }

    public void setStoreAndForwardDBName(String storeAndForwardDBName) {
        this.storeAndForwardDBName = storeAndForwardDBName;
    }

    public String getLogMeasurementName() {
        return logMeasurementName;
    }

    public void setLogMeasurementName(String logMeasurementName) {
        this.logMeasurementName = logMeasurementName;
    }

    public String getMonitoringMeasurementName() {
        return monitoringMeasurementName;
    }

    public void setMonitoringMeasurementName(String monitoringMeasurementName) {
        this.monitoringMeasurementName = monitoringMeasurementName;
    }

    public String getDataModelMeasurementName() {
        return dataModelMeasurementName;
    }

    public void setDataModelMeasurementName(String dataModelMeasurementName) {
        this.dataModelMeasurementName = dataModelMeasurementName;
    }

    public String getInfluxRetentionDuration() {
        return influxRetentionDuration;
    }

    public void setInfluxRetentionDuration(String influxRetentionDuration) {
        this.influxRetentionDuration = influxRetentionDuration;
    }
}
