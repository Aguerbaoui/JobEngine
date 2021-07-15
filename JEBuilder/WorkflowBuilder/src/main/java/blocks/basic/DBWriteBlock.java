package blocks.basic;

import blocks.WorkflowBlock;

public class DBWriteBlock extends WorkflowBlock {

    private String databaseId;

    private String request;

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
