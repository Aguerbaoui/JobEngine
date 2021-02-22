package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.string.JEStringUtils;

public class MessageEvent extends WorkflowBlock {

    private String eventId;

    public String getEventId() {
        return eventId;
    }

    private boolean throwMessage;

    public void setEventId(String eventId) {
        if (JEStringUtils.isEmpty(eventId)) return;
        if(eventId.equalsIgnoreCase(APIConstants.DEFAULT)) this.eventId = null;
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
