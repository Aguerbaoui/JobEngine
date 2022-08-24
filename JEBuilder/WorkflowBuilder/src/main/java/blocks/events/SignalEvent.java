package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import utils.string.StringUtilities;

public class SignalEvent extends WorkflowBlock {

    private String eventId;
    private boolean throwSignal;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        if (StringUtilities.isEmpty(eventId)) return;
        if (eventId.equalsIgnoreCase(APIConstants.DEFAULT)) this.eventId = null;
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
