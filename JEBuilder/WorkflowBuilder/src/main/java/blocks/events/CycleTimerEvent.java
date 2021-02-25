package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.string.JEStringUtils;

public class CycleTimerEvent extends WorkflowBlock {

    String timeCycle;

    String endDate;

    public String getTimeCycle() {
        return timeCycle;
    }

    public void setTimeCycle(String timeCycle) {
        if (JEStringUtils.isEmpty(timeCycle)) return;
        if(timeCycle.equalsIgnoreCase(APIConstants.DEFAULT)) this.timeCycle = null;
        else
            this.timeCycle = timeCycle;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        if (JEStringUtils.isEmpty(endDate)) return;
        if(endDate.equalsIgnoreCase(APIConstants.DEFAULT)) this.endDate = null;
        else
            this.endDate = endDate;
    }
}
