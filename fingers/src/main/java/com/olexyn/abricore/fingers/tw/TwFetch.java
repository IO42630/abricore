package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.Fetch;
import com.olexyn.abricore.model.Asset;

public class TwFetch implements Fetch {

    final Asset assetToScrape;

    TwFetch(Asset assetToScrape) {
        this.assetToScrape = assetToScrape;
    }

    Asset fetchAsset() {

        return transform();
    }


    Asset transform() {

        return assetToScrape;
    }

}
