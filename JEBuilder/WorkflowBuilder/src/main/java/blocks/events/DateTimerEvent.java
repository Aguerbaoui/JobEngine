package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.string.JEStringUtils;

public class DateTimerEvent extends WorkflowBlock {

    String timeDate;

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        if (JEStringUtils.isEmpty(timeDate)) return;
        if(timeDate.equalsIgnoreCase(APIConstants.DEFAULT)) this.timeDate = null;
        else
            this.timeDate = timeDate;
    }
}
