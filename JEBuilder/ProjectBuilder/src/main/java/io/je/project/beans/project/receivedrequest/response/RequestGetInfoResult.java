package io.je.project.beans.project.receivedrequest.response;

import lombok.Data;

@Data
public class RequestGetInfoResult {
    int ruleCount;
    int workflowCount;
    int eventCount;
    int dataflowCount;
}
