package blocks.events;

import blocks.WorkflowBlock;

public class DateTimerEvent extends WorkflowBlock {

    String timeDate;

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }
}
