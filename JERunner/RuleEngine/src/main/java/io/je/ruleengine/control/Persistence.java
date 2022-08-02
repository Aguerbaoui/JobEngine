package io.je.ruleengine.control;

import java.lang.Long;

public class Persistence {

    Long startTimestamp = null;
    Long duration = null;


    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

}
