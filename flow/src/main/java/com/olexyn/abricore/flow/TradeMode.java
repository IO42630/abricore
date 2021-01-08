package com.olexyn.abricore.flow;

public class TradeMode extends Mode{

    ActionMode actionMode = ActionMode.LIVE;

    @Override
    void start() throws InterruptedException {

        retrieveStoredData();

        while (true) {
            fetchLiveData();
            consultRules();
            initializeAction();
            Thread.sleep(100);
        }
    }

    @Override
    void retrieveStoredData() {

    }

    void fetchLiveData() {

    }

    @Override
    void consultRules() {

    }



}
