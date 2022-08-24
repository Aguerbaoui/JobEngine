package blocks.events;

import blocks.WorkflowBlock;
import io.je.utilities.constants.APIConstants;
import utils.string.StringUtilities;

public class DurationDelayTimerEvent extends WorkflowBlock {

    String timeDuration;

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        if (StringUtilities.isEmpty(timeDuration)) return;
        if (timeDuration.equalsIgnoreCase(APIConstants.DEFAULT)) this.timeDuration = null;
        else
            this.timeDuration = timeDuration;
    }
}
