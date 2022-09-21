package io.je.project.beans.project.receivedrequest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.utilities.ruleutils.OperationStatusDetails;
import lombok.Data;

import java.util.List;

@Data
public class ProjectRequestResult {
    @JsonProperty(value = "isRunning")
    boolean isRunning = false;
    @JsonProperty(value = "isStopped")
    boolean isStopped = false;
    @JsonProperty(value = "isDeleted")
    boolean isDeleted = false;
    @JsonProperty(value = "isUpdated")
    boolean isUpdated = false;
    RequestGetInfoResult getInfoResult;
    @JsonProperty(value = "isBuilt")
    boolean isBuilt = false;
    String strError;
    List<OperationStatusDetails> result;
}
