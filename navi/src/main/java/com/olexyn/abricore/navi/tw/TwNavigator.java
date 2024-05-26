package com.olexyn.abricore.navi.tw;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import com.olexyn.abricore.navi.AbricoreTabDriverConfigProvider;
import com.olexyn.abricore.navi.Navigator;
import com.olexyn.abricore.util.Constants;
import com.olexyn.abricore.util.enums.Interval;
import com.olexyn.min.log.LogU;
import com.olexyn.propconf.PropConf;
import com.olexyn.tabdriver.TabDriver;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.olexyn.abricore.navi.TabPurpose.DOWNLOAD_TW;
import static com.olexyn.abricore.navi.TabPurpose.OBSERVE_TW;
import static com.olexyn.abricore.navi.tw.TwUrls.TW_HOME;
import static com.olexyn.abricore.navi.tw.TwUrls.TW_ROOT;
import static com.olexyn.abricore.util.Constants.BUTTON;
import static com.olexyn.abricore.util.Constants.DIV;
import static com.olexyn.abricore.util.Constants.INPUT;
import static com.olexyn.abricore.util.num.NumSerialize.fromStr;

@Service
@Lazy
public class TwNavigator extends TwSession implements Navigator {

    static boolean timeSkipDone = false;
    protected static boolean cookieAccepted = false;

    private static final long TIMEFRAME_OF_DOWNLOAD = PropConf.getLong("tw.download.timeframe.hours");
    private static final long WAIT_TO_LOAD = PropConf.getLong("tw.download.timeframe.wait.to.load.seconds");

    @Autowired
    public TwNavigator(
        AbricoreTabDriverConfigProvider tdConfig
    ) {
        super(new TabDriver(tdConfig));
    }

    public void fetchHistoricalData(
        List<AssetDto> assets, List<Interval> intervals
    ) {
        for (var asset : assets) {
            fetchHistoricalData(asset, intervals);
        }
    }

    public void fetchHistoricalData(
        AssetDto asset, List<Interval> intervals
    ) {
        for (var interval : intervals) {
            fetchHistoricalData(asset, interval);
        }
    }

    private void acceptCookie() {
        if (cookieAccepted) { return; }
        try {
            WebElement toastContainer = td.getByFieldValue(DIV, "data-role", "toast-container");
            if (toastContainer == null) { return; }

            var button = toastContainer.findElement(By.cssSelector(BUTTON));
            if (button == null) { return; }
            button.click();
        } catch (NoSuchElementException e) {
            LogU.infoPlain("No cookie popup present. -> SKIP.");
        }
        cookieAccepted = true;
    }

    public void fetchHistoricalData(AssetDto asset, Interval interval) {

        synchronized(td) {
            td.switchToTab(DOWNLOAD_TW.name());
            sleep(1000);

            String url = asset.getTwSymbol().replace(":", "%3A");

            td.get(TW_ROOT + "chart?symbol=" + url);
            try {
                Alert alert = td.switchTo().alert();
                alert.accept();
            } catch (NoAlertPresentException e) {
                LogU.infoPlain("No alert present. -> SKIP.");
            }

            acceptCookie();

            setInterval(interval);

            var goToDateButton = td.getByFieldValue(BUTTON, "data-name", "go-to-date");
            goToDateButton.click();

            var startDateTime = LocalDateTime.now().minus(Duration.ofHours(TIMEFRAME_OF_DOWNLOAD));

            if (!timeSkipDone) {
                // the form maintains the last entered value, thus do this only once.
                var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());
                String startDateStr = startDateTime.toLocalDate().format(dateFormatter);
                String nowDateStr = LocalDate.now().format(dateFormatter);
                WebElement dateField = td.getByFieldValue(INPUT, "value", nowDateStr);
                td.sendDeleteKeys(dateField, 10);
                dateField.sendKeys(startDateStr);
                timeSkipDone = true;
            }

            var timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
            String startTimeString = startDateTime.toLocalTime().format(timeFormatter);


            WebElement timeField = td.getByFieldValue(INPUT, "maxlength", "5");

            timeField.sendKeys(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE);
            timeField.sendKeys(startTimeString);
            timeField.sendKeys(Keys.ENTER);

            WebElement submitButton = td.getByFieldValue(BUTTON, "name", "submit");
            submitButton.click();
        }

        sleep(WAIT_TO_LOAD * Constants.SECONDS);

        synchronized(td) {
            td.switchToTab(DOWNLOAD_TW.name());
            sleep(1000);
            download();
        }

    }

    public Set<SnapshotDto> fetchQuotes(List<AssetDto> assets) {
        synchronized(td) {
            td.switchToTab(OBSERVE_TW.name());
            Set<SnapshotDto> snapshotDtos = new HashSet<>();

            sleep(1000);
            ensureUrl(TW_HOME);

            WebElement watchlist = td.findByCss("[class*='widgetbar-widget-watchlist']").orElseThrow();
            if (!watchlist.isDisplayed()) {
                td.findByCss("[aria-label*='Watchlist']").orElseThrow().click();
            }

            for (var asset : assets) {

                var symbolRow = td.findByCss(String.format("[data-symbol-full*='%s']", asset.getTwSymbol()))
                    .orElseThrow();

                WebElement last = td.findByCss(symbolRow, "span[class*='last-']").orElseThrow();
                SnapshotDto snapshotDto = new SnapshotDto(asset);
                snapshotDto.setInstant(Instant.now());
                snapshotDto.setTradePrice(fromStr(last.getText()));
                snapshotDtos.add(snapshotDto);
            }
            return snapshotDtos;
        }
    }

    private void setInterval(Interval interval) {
        td.findElement(By.id("header-toolbar-intervals")).click();
        td.getByFieldValue(DIV, "data-value", interval.getFileToken().toUpperCase()).click();
    }

    private void download() {
        td.findByCss("button[data-name*='save-load']").orElseThrow().click();
        td.getByText("Export chart data…").click();
        td.findByCss("button[name='submit']").orElseThrow().click();
    }

}


