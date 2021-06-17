package com.olexyn.abricore.flow;

import com.olexyn.abricore.flow.mission.Mission;

public abstract class AbstractMode {

    ActionMode actionMode;

    protected Mission mission;

    abstract void start() throws InterruptedException;

    void retrieveStoredData() {

     }

    void consultRules() {

    }

    void initializeAction() {

    }
}
