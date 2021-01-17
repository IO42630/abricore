package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.DriverTools;
import com.olexyn.abricore.fingers.DriverTools.CRITERIA;
import com.olexyn.abricore.fingers.Fetch;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class TwFetch extends Fetch {

    final Asset assetToScrape;

    TwFetch(Asset assetToScrape, WebDriver driver) {
        super(driver);
        this.assetToScrape = assetToScrape;
    }

    Asset fetchAsset() {
        driver.get("https://www.tradingview.com/" + "chart?symbol=FX%3AXAGUSD");
        DriverTools.getWhere(driver, "apply-common-tooltip", CRITERIA.TITLE, Interval.M_30.getTwLabel()).click();
        DriverTools.getWhere(driver, "label-3Xqxy756", CRITERIA.TEXT, Interval.M_30.getTwLabel()).click();


        DriverTools.getWhere(driver, "button-9U4gleap").click();
        DriverTools.getWhere(driver, "labelRow-3Q0rdE8-", CRITERIA.TEXT, "Export chart").click();

        DriverTools.getWhere(driver, "button-1iktpaT1").click();


        //JavascriptExecutor js = (JavascriptExecutor) driver;
        //js.executeScript("window.scrollBy(1000,0)");

        return transform();
    }

    Asset transform() {

        return assetToScrape;
    }

}
