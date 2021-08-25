package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.Timers;
import io.je.utilities.string.JEStringUtils;

public class TimerEvent extends WorkflowBlock {

    String timeDate;

    String timeDuration;

    String timeCycle;

    String endDate;

    Timers timer;

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        if(timeDuration != null) {
            this.timeDuration = "PT" + timeDuration.toUpperCase();
        }
        else {
            this.timeDuration = null;
        }
    }

    public String getTimeCycle() {
        return timeCycle;
    }

    public void setTimeCycle(String timeCycle) {
        if(timeCycle != null) {
            this.timeCycle = "R1000/PT" + timeCycle.toUpperCase();
        }
        else {
            this.timeCycle = null;
        }
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Timers getTimer() {
        return timer;
    }

    public void setTimer(Timers timer) {
        this.timer = timer;
    }
}
