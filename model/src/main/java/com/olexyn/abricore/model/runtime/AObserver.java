package com.olexyn.abricore.model.runtime;

public interface AObserver extends LockAware {

    Thread getThread();

    void cancel();

}
