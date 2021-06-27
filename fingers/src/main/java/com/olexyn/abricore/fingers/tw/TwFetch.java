package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.fingers.DriverUtil;
import com.olexyn.abricore.fingers.Fetch;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.ANum;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TwFetch extends Fetch {

    final Asset assetToScrape;
    boolean cont = true;

    public TwFetch(WebDriver driver) {
        super(driver);
        this.assetToScrape = null;
    }

    public void fetchHistoricalData(List<Asset> assets, long interval) throws InterruptedException {

        Thread.sleep(1000);

        Asset asset = AssetService.ofName("BTCUSD");
        String url = asset.getTwSymbol().replace(":", "%3A");

        driver.get("https://www.tradingview.com/" + "chart?symbol=" + url);
        setInterval(driver, Interval.S_1);


        WebElement goToDateButton = DriverUtil.getByFieldValue(driver, "div", "data-name", "go-to-date");
        goToDateButton.click();

        LocalDateTime startDateTime = LocalDateTime.now().minus(Duration.ofHours(6));

        LocalDate startDate = startDateTime.toLocalDate();
        LocalTime startTime = startDateTime.toLocalTime();

        if (startDate.isBefore(LocalDate.now())) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDateStr = startDate.format(dateFormatter);
            String nowDateStr = LocalDate.now().format(dateFormatter);

            WebElement dateField = DriverUtil.getByFieldValue(driver, "input", "value", nowDateStr);
            DriverUtil.sendDeleteKeys(dateField, 10);
            dateField.sendKeys(startDateStr);

        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String startTimeString = startTime.format(timeFormatter);


        WebElement timeField = DriverUtil.getByFieldValue(driver, "input", "value", "00:00");
        timeField.sendKeys(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE);
        timeField.sendKeys(startTimeString);
        timeField.sendKeys(Keys.ENTER);

        WebElement submitButton = DriverUtil.getByFieldValue(driver, "button", "name", "submit");
        submitButton.click();

        download(driver);


    }

    public List<AssetSnapshot> fetchQuotes(List<Asset> assets) throws InterruptedException {
        List<AssetSnapshot> assetSnapshots = new ArrayList<>();

        Thread.sleep(1000);

        WebElement watchlist = driver.findElement(By.className("widgetbar-widget-watchlist"));
        if (!watchlist.isDisplayed()) {
            WebElement watchlistButton = driver.findElement(By.cssSelector("div[data-name='base']"));
            watchlistButton.click();
        }

        for (Asset asset : assets) {
            String dataSymbolFull = String.format("div[data-symbol-full='%s']", asset.getTwSymbol());
            WebElement symbol = driver.findElement(By.cssSelector(dataSymbolFull));
            WebElement last = symbol.findElement(By.className("last-EJ_LFrif"));
            AssetSnapshot assetSnapshot = new AssetSnapshot(asset);
            assetSnapshot.setInstant(Instant.now());
            assetSnapshot.getPrice().setTraded(ANum.of(last.getText()));
            assetSnapshots.add(assetSnapshot);
        }

        return assetSnapshots;
    }

    private static void setInterval(WebDriver driver, Interval interval) {
        driver.findElement(By.id("header-toolbar-intervals")).click();
        DriverUtil.getByFieldValue(driver, "div", "data-value", interval.getFileLabel().toUpperCase()).click();
    }

    private static void download(WebDriver driver) {
        WebElement topLeftArea = DriverUtil.getByFieldValue(driver, "div", "class", "layout__area--topleft");
        DriverUtil.getByFieldValue(topLeftArea, "div", "data-role", "button").click();

        DriverUtil.getByText(driver, "Export chart data…").click();

        DriverUtil.getByFieldValue(driver, "button", "name", "submit").click();
    }

}
