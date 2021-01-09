package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.Agent;
import com.olexyn.abricore.model.Asset;

public class TwAgent implements Agent {

    final Asset assetToScrape;

    public TwAgent(Asset assetToScrape) {
        this.assetToScrape = assetToScrape;
    }

    public void start() throws InterruptedException {

        TwLogin login = new TwLogin();
        login.login();

        TwFetch fetch = new TwFetch(assetToScrape);
        while (true) {
            assetToScrape.mergeFromAsset(fetch.fetchAsset());
            Thread.sleep(100);
        }
    }
}
