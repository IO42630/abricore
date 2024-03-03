package com.olexyn.abricore.model.runtime;

import com.olexyn.abricore.util.Lock;

public interface LockAware {

    Lock getLock();

}
