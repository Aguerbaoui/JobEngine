package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import utils.string.StringUtilities;


public class MessageEvent extends WorkflowBlock {

    private String eventId;
    private boolean throwMessage;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        if (StringUtilities.isEmpty(eventId)) return;
        if (eventId.equalsIgnoreCase(APIConstants.DEFAULT)) this.eventId = null;
        else
            this.eventId = eventId;
    }

    public boolean isThrowMessage() {
        return throwMessage;
    }

    public void setThrowMessage(boolean throwMessage) {
        this.throwMessage = throwMessage;
    }
}
