package com.olexyn.abricore.flow;

import com.olexyn.abricore.flow.jobs.Job;
import com.olexyn.abricore.util.log.LogU;
import com.olexyn.propconf.PropConf;

public class JobTimer {

    private static final long NAP_TIME = 100;

    public void sleepMilli(Job job, String prop) {
        long sleepTime = PropConf.getLong(prop);
        sleepMilli(job, sleepTime);
    }

    public void sleepMilli(Job job, long sleepTime) {
        long counter = 0;
        while (counter < sleepTime && !job.isCancelled()) {
            try {
                Thread.sleep(NAP_TIME);
            } catch (InterruptedException e) {
                LogU.infoPlain("Interrupted: %s", e.getMessage());
            }
            counter += NAP_TIME;
        }
    }

}
