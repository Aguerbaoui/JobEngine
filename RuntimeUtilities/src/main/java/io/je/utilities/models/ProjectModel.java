package io.je.utilities.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectModel {

    String _id;
    String key;
    String description;
    String createdBy;
    Map<String, String> blockNames;
    Map<String, Integer> blockNameCounters;
    boolean autoReload;
    boolean isRunning;
    boolean isBuilt;
    String configurationPath;
    String state;
    @JsonProperty("CreatedAt")
    private String createdAt;
    @JsonProperty("ModifiedAt")
    private String modifiedAt;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Map<String, String> getBlockNames() {
        return blockNames;
    }

    public void setBlockNames(Map<String, String> blockNames) {
        this.blockNames = blockNames;
    }

    public Map<String, Integer> getBlockNameCounters() {
        return blockNameCounters;
    }

    public void setBlockNameCounters(Map<String, Integer> blockNameCounters) {
        this.blockNameCounters = blockNameCounters;
    }

    public boolean isAutoReload() {
        return autoReload;
    }

    public void setAutoReload(boolean autoReload) {
        this.autoReload = autoReload;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public void setBuilt(boolean isBuilt) {
        this.isBuilt = isBuilt;
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
        this.configurationPath = configurationPath;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


}
