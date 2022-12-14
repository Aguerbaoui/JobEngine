package io.je.runtime.events;

import io.je.utilities.beans.JEEvent;
import utils.log.LoggerUtils;

public class EventTimeoutRunnable implements Runnable {

    JEEvent event;


    public EventTimeoutRunnable(JEEvent event) {
        super();
        this.event = event;
    }


    @Override
    public void run() {
        try {
            Thread.sleep(event.getTimeout());
            synchronized (event) {
                event.untrigger();

            }


        } catch (InterruptedException e) {
            LoggerUtils.logException(e);
            Thread.currentThread().interrupt();
        }

    }

}
