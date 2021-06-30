package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.fingers.Navigator;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.Constants;
import com.olexyn.abricore.util.LogUtil;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.olexyn.abricore.fingers.TabPurpose.DOWNLOAD_TW;
import static com.olexyn.abricore.fingers.TabPurpose.OBSERVE_TW;

public class TwNavigator extends Navigator {

    private static final Logger LOGGER = LogUtil.get(TwNavigator.class);

    static boolean timeSkipDone = false;

    public static void fetchHistoricalData(List<Asset> assets, List<Interval> intervals, long timeFrame, long timeToWait) throws InterruptedException {
        Session.switchToTab(DOWNLOAD_TW);
        for (Asset asset : assets) {
            fetchHistoricalData(asset, intervals, timeFrame, timeToWait);
        }
    }

    public static void fetchHistoricalData(Asset asset, List<Interval> intervals, long timeFrame, long timeToWait) throws InterruptedException {
        Session.switchToTab(DOWNLOAD_TW);
        for (Interval interval : intervals) {
            fetchHistoricalData(asset, interval, timeFrame, timeToWait);
        }
    }


    public static void fetchHistoricalData(Asset asset, Interval interval, long timeFrame, long timeToWait) throws InterruptedException {
        Session.switchToTab(DOWNLOAD_TW);
        Thread.sleep(1000);

        String url = asset.getTwSymbol().replace(":", "%3A");

        Session.DRIVER.get("https://www.tradingview.com/" + "chart?symbol=" + url);
        try{
            Alert alert = Session.DRIVER.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException e) {

        }
        setInterval(interval);


        WebElement goToDateButton = Session.getByFieldValue("div", "data-name", "go-to-date");
        goToDateButton.click();

        LocalDateTime startDateTime = LocalDateTime.now().minus(Duration.ofMinutes(timeFrame));

        LocalDate startDate = startDateTime.toLocalDate();
        LocalTime startTime = startDateTime.toLocalTime();
        boolean timeSkip = startDate.isBefore(LocalDate.now());

        if (timeSkip && !timeSkipDone) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDateStr = startDate.format(dateFormatter);
            String nowDateStr = LocalDate.now().format(dateFormatter);
            WebElement dateField = Session.getByFieldValue("input", "value", nowDateStr);
            Session.sendDeleteKeys(dateField, 10);
            dateField.sendKeys(startDateStr);
            timeSkipDone = true;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String startTimeString = startTime.format(timeFormatter);


        WebElement timeField = Session.getByFieldValue("input", "maxlength", "5");

        timeField.sendKeys(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE);
        timeField.sendKeys(startTimeString);
        timeField.sendKeys(Keys.ENTER);

        WebElement submitButton = Session.getByFieldValue("button", "name", "submit");
        submitButton.click();

        Thread.sleep(timeToWait * Constants.SECONDS);

        download();
    }

    public static List<AssetSnapshot> fetchQuotes(List<Asset> assets) throws InterruptedException {
        Session.switchToTab(OBSERVE_TW);
        List<AssetSnapshot> assetSnapshots = new ArrayList<>();

        Thread.sleep(1000);

        WebElement watchlist = Session.DRIVER.findElement(By.className("widgetbar-widget-watchlist"));
        if (!watchlist.isDisplayed()) {
            WebElement watchlistButton = Session.DRIVER.findElement(By.cssSelector("div[data-name='base']"));
            watchlistButton.click();
        }

        for (Asset asset : assets) {
            String dataSymbolFull = String.format("div[data-symbol-full='%s']", asset.getTwSymbol());
            WebElement symbol = Session.DRIVER.findElement(By.cssSelector(dataSymbolFull));
            WebElement last = symbol.findElement(By.className("last-EJ_LFrif"));
            AssetSnapshot assetSnapshot = new AssetSnapshot(asset);
            assetSnapshot.setInstant(Instant.now());
            assetSnapshot.getPrice().setTraded(ANum.of(last.getText()));
            assetSnapshots.add(assetSnapshot);
        }

        return assetSnapshots;
    }

    private static void setInterval(Interval interval) {
        Session.DRIVER.findElement(By.id("header-toolbar-intervals")).click();
        Session.getByFieldValue("div", "data-value", interval.getFileToken().toUpperCase()).click();
    }

    private static void download() {
        WebElement topLeftArea = Session.getByFieldValue("div", "class", "layout__area--topleft");
        Session.getByFieldValue(topLeftArea, "div", "data-role", "button").click();

        Session.getByText("Export chart data…").click();

        Session.getByFieldValue("button", "name", "submit").click();
    }

}
