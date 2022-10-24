package io.je.project.beans.project.sentrequest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.project.beans.project.sentrequest.ExchangedUpdatedObjectAttributeNames;

import java.util.List;
import java.util.Map;

public class ExchangedUpdateObject {
    public RequestType type = RequestType.UPDATE;
    public String projectId;
    public Map<String, String> addedblockNames;
    public List<String> deletedBlockNames;
    public Map<String, Integer> blockNameCounters;
    public boolean isRunning;
    public boolean isBuilt;
    @JsonProperty(ExchangedUpdatedObjectAttributeNames.AUTO_RELOAD)
    public boolean autoReload;
    public String configurationPath;

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Map<String, String> getAddedblockNames() {
        return addedblockNames;
    }

    public void setAddedblockNames(Map<String, String> addedblockNames) {
        this.addedblockNames = addedblockNames;
    }

    public List<String> getDeletedBlockNames() {
        return deletedBlockNames;
    }

    public void setDeletedBlockNames(List<String> deletedBlockNames) {
        this.deletedBlockNames = deletedBlockNames;
    }

    public Map<String, Integer> getBlockNameCounters() {
        return blockNameCounters;
    }

    public void setBlockNameCounters(Map<String, Integer> blockNameCounters) {
        this.blockNameCounters = blockNameCounters;
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

    public boolean isAutoReload() {
        return autoReload;
    }

    public void setAutoReload(boolean autoReload) {
        this.autoReload = autoReload;
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
        this.configurationPath = configurationPath;
    }

}