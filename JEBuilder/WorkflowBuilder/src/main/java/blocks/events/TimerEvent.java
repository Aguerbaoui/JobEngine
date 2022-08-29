package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.Timers;
import utils.date.DateUtils;

public class TimerEvent extends WorkflowBlock {

    String timeDate;

    String timeDuration;

    String timeCycle;

    String endDate;

    Timers timer;

    int occurrences = -1;

    public String getTimeDate() {
        return DateUtils.parseUTCStringToLocalTimeString(timeDate);
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        if (timeDuration != null) {
            this.timeDuration = "PT" + timeDuration.toUpperCase();
        } else {
            this.timeDuration = null;
        }
    }

    public String getTimeCycle() {
        return timeCycle;
    }

    public void setTimeCycle(String timeCycle) {
        if (timeCycle != null) {
            if (occurrences != -1) {
                this.timeCycle = "R" + occurrences + "/PT" + timeCycle.toUpperCase();
            } else {
                this.timeCycle = "R100000/PT" + timeCycle.toUpperCase();
            }
        } else {
            this.timeCycle = null;
        }
    }

    public String getEndDate() {
        return DateUtils.parseUTCStringToLocalTimeString(endDate);
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

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }
}
