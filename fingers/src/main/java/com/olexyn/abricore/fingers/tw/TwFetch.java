package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.fingers.DriverTools;
import com.olexyn.abricore.fingers.DriverTools.CRITERIA;
import com.olexyn.abricore.fingers.Fetch;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.Stock;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;

public class TwFetch extends Fetch {

    final Asset assetToScrape;
    boolean cont = true;

    TwFetch(Asset assetToScrape, WebDriver driver) {
        super(driver);
        this.assetToScrape = assetToScrape;
    }

    void fetchAsset(Mode mode) throws InterruptedException {

        driver.get("https://www.tradingview.com/" + "chart?symbol=FX%3AXAGUSD");


        Instant oldEarly = Instant.now();
        if (mode.equals(Mode.DOWNLOAD)) {
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
            setInterval(driver, Interval.M_1);
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
