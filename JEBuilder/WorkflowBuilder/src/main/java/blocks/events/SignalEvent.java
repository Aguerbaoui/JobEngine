package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.string.JEStringUtils;

public class SignalEvent extends WorkflowBlock {

    private String eventId;

    public String getEventId() {
        return eventId;
    }

    private boolean throwSignal;
    public void setEventId(String eventId) {
        if (JEStringUtils.isEmpty(eventId)) return;
        if(eventId.equalsIgnoreCase(APIConstants.DEFAULT)) this.eventId = null;
        else
            this.eventId = eventId;
    }

    public boolean isThrowSignal() {
        return throwSignal;
    }

    public void setThrowSignal(boolean throwSignal) {
        this.throwSignal = throwSignal;
    }
}
