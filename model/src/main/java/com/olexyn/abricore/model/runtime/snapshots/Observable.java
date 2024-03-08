package com.olexyn.abricore.model.runtime.snapshots;

import com.olexyn.abricore.model.runtime.AObserver;

public interface Observable {

    void notifyObservers();

    void addObverser(AObserver obs);

    void removeObserver(AObserver obs);

}
