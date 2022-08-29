package blocks.basic;

import blocks.WorkflowBlock;
import blocks.events.TimerEvent;

public class StartBlock extends WorkflowBlock {

    private String eventId = null;

    private TimerEvent timerEvent = null;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public TimerEvent getTimerEvent() {
        return timerEvent;
    }

    public void setTimerEvent(TimerEvent timerEvent) {
        this.timerEvent = timerEvent;
    }
}
