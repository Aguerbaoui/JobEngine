package blocks.basic;

import blocks.WorkflowBlock;

public class EndBlock extends WorkflowBlock {

    private String eventId = null;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
