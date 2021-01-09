package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.Agent;
import com.olexyn.abricore.model.Asset;
import org.openqa.selenium.WebDriver;

public class TwAgent implements Agent {

    final Asset assetToScrape;

    public TwAgent(Asset assetToScrape) {
        this.assetToScrape = assetToScrape;
    }

    public void start() throws InterruptedException {

        TwLogin login = new TwLogin();
        WebDriver driver = login.doLogin();

        TwFetch fetch = new TwFetch(assetToScrape, driver);
        boolean stop = false;
        while (!stop) {
            assetToScrape.mergeFromAsset(fetch.fetchAsset());
            Thread.sleep(100);
            stop = true;
        }
        login.doLogout(driver);
    }
}
