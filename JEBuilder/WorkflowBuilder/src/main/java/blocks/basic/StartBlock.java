package blocks.basic;

import blocks.WorkflowBlock;
import blocks.events.TimerEvent;
import io.je.utilities.constants.APIConstants;
import utils.string.StringUtilities;

public class StartBlock extends WorkflowBlock {

    private String eventId = null;

    private TimerEvent timerEvent = null;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        if (StringUtilities.isEmpty(eventId)) return;
        if(eventId.equalsIgnoreCase(APIConstants.DEFAULT))
            this.eventId = null;
        else
            this.eventId = eventId;
    }

    public TimerEvent getTimerEvent() {
        return timerEvent;
    }

    public void setTimerEvent(TimerEvent timerEvent) {
        this.timerEvent = timerEvent;
    }
}
