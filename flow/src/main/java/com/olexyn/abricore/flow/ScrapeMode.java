package com.olexyn.abricore.flow;

import com.olexyn.abricore.fingers.StoreData;
import com.olexyn.abricore.fingers.tw.TwAgent;
import com.olexyn.abricore.model.Asset;

public class ScrapeMode extends AbstractMode {

    final ModeEnum modeEnum;
    final Asset assetToScrape;

    ScrapeMode(ModeEnum modeEnum, Asset assetToScrape) {
        this.modeEnum = modeEnum;
        this.assetToScrape = assetToScrape;
    }

    @Override
    void start() throws InterruptedException {

        while (true) {
            fetchHistoricalData();
            StoreData.getInstance().store(assetToScrape);
            Thread.sleep(100);
        }
    }

    void fetchHistoricalData() throws InterruptedException {

        switch (modeEnum) {
            case SCRAPE_TW:
                new TwAgent(assetToScrape).start();
        }
    }
}
