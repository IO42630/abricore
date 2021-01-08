package com.olexyn.abricore.flow;

public abstract class Mode {

    ActionMode actionMode;

    abstract void start() throws InterruptedException;

    void retrieveStoredData() {

     }

    void consultRules() {

    }

    void initializeAction() {

    }
}
