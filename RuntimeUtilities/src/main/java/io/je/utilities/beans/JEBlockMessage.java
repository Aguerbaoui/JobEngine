package io.je.utilities.beans;

public class JEBlockMessage {

    String blockName;
    String blockValue;


    public JEBlockMessage(String blockName, String blockValue) {
        super();
        this.blockName = blockName;
        this.blockValue = blockValue;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockValue() {
        return blockValue;
    }

    public void setBlockValue(String blockValue) {
        this.blockValue = blockValue;
    }


}
