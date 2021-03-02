package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.Agent;
import com.olexyn.abricore.fingers.Fetch.Mode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Commodity;
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
        fetch.fetchAsset(Mode.OBSERVE);
        login.doLogout(driver);
    }

    public static void main(String... args) throws InterruptedException {
        new TwAgent(new Commodity("XAGUSD")).start();
    }
}
