package io.je.serviceTasks;

public class DatabaseTask extends ActivitiTask {

    private String request;

    private String databaseId;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }
}
