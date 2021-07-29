package blocks.basic;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.string.JEStringUtils;

public class StartBlock extends WorkflowBlock {

    private String eventId = null;

    private String timeDelay = null;

    private String timerCycle = null;

    private String timerDate = null;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        if (JEStringUtils.isEmpty(eventId)) return;
        if(eventId.equalsIgnoreCase(APIConstants.DEFAULT))
            this.eventId = null;
        else
            this.eventId = eventId;
    }

    public String getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(String timeDelay) {
        this.timeDelay = timeDelay;
    }

    public String getTimerCycle() {
        return timerCycle;
    }

    public void setTimerCycle(String timerCycle) {
        this.timerCycle = timerCycle;
    }

    public String getTimerDate() {
        return timerDate;
    }

    public void setTimerDate(String timerDate) {
        this.timerDate = timerDate;
    }
}
