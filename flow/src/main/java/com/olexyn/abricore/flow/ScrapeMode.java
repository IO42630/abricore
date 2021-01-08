package com.olexyn.abricore.flow;

public class ScrapeMode extends Mode{

    @Override
    void start() throws InterruptedException {

        while(true) {
            fetchHistoricalData();
            storeData();
            Thread.sleep(100);
        }

    }

    void fetchHistoricalData() {

    }

    void storeData() {

    }
}
