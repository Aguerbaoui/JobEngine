package io.je.rulebuilder.components;

public class BlockLinkModel {
    String blockId;
    int order;
    String connectionName;


    public BlockLinkModel() {
        super();
        // TODO Auto-generated constructor stub
    }

    public BlockLinkModel(String blockId) {
        super();
        this.blockId = blockId;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String block) {
        this.blockId = block;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String variableName) {
        this.connectionName = variableName;
    }


}
