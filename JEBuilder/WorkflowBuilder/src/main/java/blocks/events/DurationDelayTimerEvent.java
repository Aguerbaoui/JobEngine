package blocks.events;

import blocks.WorkflowBlock;

public class DurationDelayTimerEvent extends WorkflowBlock {

    String timeDuration;

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        this.timeDuration = timeDuration;
    }
}
