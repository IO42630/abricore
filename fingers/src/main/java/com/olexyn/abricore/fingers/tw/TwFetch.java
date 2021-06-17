package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.datastore.AssetFactory;
import com.olexyn.abricore.fingers.DriverTools;
import com.olexyn.abricore.fingers.DriverTools.CRITERIA;
import com.olexyn.abricore.fingers.Fetch;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class TwFetch extends Fetch {

    final Asset assetToScrape;
    boolean cont = true;

    TwFetch(Asset assetToScrape, WebDriver driver) {
        super(driver);
        this.assetToScrape = assetToScrape;
    }

    void fetchAsset(Mode mode) throws InterruptedException {


        Instant oldEarly = Instant.now();
        if (mode.equals(Mode.DOWNLOAD)) {
            driver.get("https://www.tradingview.com/" + "chart?symbol=FX%3AXAGUSD");
            setInterval(driver, Interval.M_30);
            while (cont) {

                download(driver);

                Instant newEarly = null; //StoreCsv.getInstance().read(new Stock(""), Interval.M_30).firstEntry().getKey();
                if (newEarly.isBefore(oldEarly)) {
                    oldEarly = newEarly;
                    // TODO goto DATE - FOO (foo depends on Interval chosen)
                } else {
                    cont = false;
                }
            }
        } else  if (mode.equals(Mode.OBSERVE)) {

            Map<Asset, SnapShotSeries> symbolsToObserve = new HashMap<>();
            symbolsToObserve.put(AssetFactory.ofTwSymbol("ICEUS:DXY"), new SnapShotSeries(null, null));
            // driver.get("https://www.tradingview.com/");

            WebElement watchlist = driver.findElement(By.className("widgetbar-widget-watchlist"));


            if (!watchlist.isDisplayed()) {
                WebElement watchlistButton = driver.findElement(By.cssSelector("div[data-name='base']"));
                watchlistButton.click();
            }





            WebElement symbol = driver.findElement(By.cssSelector("div[data-symbol-full='']"));
            WebElement last = symbol.findElement(By.className("last-EJ_LFrif"));
            String text = last.getText();


            while (cont) {
                download(driver);
                Thread.sleep(Duration.ofMinutes(5L).toMillis());
            }
        }
    }

    private static void setInterval(WebDriver driver, Interval interval) {
        DriverTools.getWhere(driver, "apply-common-tooltip", CRITERIA.TITLE, interval.getTwLabel()).click();
        DriverTools.getWhere(driver, "label-3Xqxy756", CRITERIA.TEXT, interval.getTwLabel()).click();
    }

    private static void download(WebDriver driver) {
        DriverTools.getWhere(driver, "button-9U4gleap").click();
        DriverTools.getWhere(driver, "labelRow-3Q0rdE8-", CRITERIA.TEXT, "Export chart").click();
        DriverTools.getWhere(driver, "submitButton-2lNICzl3").click();
    }

}
