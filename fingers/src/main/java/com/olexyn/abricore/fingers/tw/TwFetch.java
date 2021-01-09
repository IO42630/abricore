package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.Fetch;
import com.olexyn.abricore.model.Asset;
import org.openqa.selenium.WebDriver;

public class TwFetch extends Fetch {

    final Asset assetToScrape;

    TwFetch(Asset assetToScrape, WebDriver webDriver) {
        super(webDriver);
        this.assetToScrape = assetToScrape;
    }

    Asset fetchAsset() {

        return transform();
    }


    Asset transform() {

        return assetToScrape;
    }

}
