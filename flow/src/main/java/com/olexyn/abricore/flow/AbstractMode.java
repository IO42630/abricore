package com.olexyn.abricore.flow;

public abstract class AbstractMode {

    ActionMode actionMode;

    abstract void start() throws InterruptedException;

    void retrieveStoredData() {

     }

    void consultRules() {

    }

    void initializeAction() {

    }
}
