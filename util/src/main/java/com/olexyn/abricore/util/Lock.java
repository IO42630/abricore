package com.olexyn.abricore.util;

import com.olexyn.abricore.util.log.LogU;

import java.time.Duration;

public class Lock {

    public final void safeWait(Duration timeout) {
        safeWait(timeout.toMillis());
    }

    /**
     * obs-tw waits for trade-sq OR TRADE_SQ waits for some data push
     */
    public final void safeWait(long timeout) {
        try {
            synchronized(this) {
                this.notifyAll();
                this.wait(timeout); // obs-tw waits for trade-sq
            }
        } catch (InterruptedException e) {
            LogU.infoPlain("INTERRUPTED");
        }
    }

}
