package blocks.events;

import blocks.WorkflowBlock;

public class CycleTimerEvent extends WorkflowBlock {

    String timeCycle;

    String endDate;

    public String getTimeCycle() {
        return timeCycle;
    }

    public void setTimeCycle(String timeCycle) {
        this.timeCycle = timeCycle;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
