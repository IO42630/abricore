package com.olexyn.abricore.fingers.sq;

import com.olexyn.abricore.datastore.Symbols;
import com.olexyn.abricore.fingers.sq.enums.Exchange;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotType;
import com.olexyn.abricore.util.DataUtil;
import com.olexyn.abricore.util.enums.Currency;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.Constants.EMPTY;

public class Navigator {

    private final WebDriver driver;

    public Navigator(WebDriver driver) {
        this.driver = driver;
    }



    public void goToMainScreen() {
        driver.get("https://trade.swissquote.ch/bank_security/login/RedirectAtLogin.action?l=d");
    }

    public void search(String keyword) {
        driver.findElement(By.className("defaultInput")).sendKeys(keyword);
    }

    public void tradeWindow(String isin, Currency currency, Exchange exchange) {
        String url = String.join(
            EMPTY,
            "https://trade.swissquote.ch/sqb_core/DispatchCtrl?commandName=trade&isin=",
            isin,
            "&currency=",
            currency.toString(),
            "&stockExchange=",
            exchange.getCode(),
            "&partnerSource=fullquote"
        );
        driver.get(url);
    }

    public AssetSnapshot resolveFullQuote() {

        List<String> resolve = resolveTable(driver.findElement(By.className("tableContent")));

        AssetSnapshot assetSnapshot = new AssetSnapshot(Symbols.getAsset(resolve.get(22)), Interval.S_1);
        assetSnapshot.getTypeList().addAll(List.of(SnapShotType.SPREAD, SnapShotType.OPTION));
        assetSnapshot.setCurrency(Currency.CHF);
        assetSnapshot.setMultiplier(Long.valueOf(resolve.get(7)));
        assetSnapshot.setInstant(Instant.now());
        assetSnapshot.setBidVol(DataUtil.parseLong(resolve.get(12)));
        assetSnapshot.setBidPrice(DataUtil.parseDouble(resolve.get(13)));


        assetSnapshot.setAskPrice(DataUtil.parseDouble(resolve.get(14)));
        assetSnapshot.setAskVol(DataUtil.parseLong(resolve.get(15)));
        // TODO account for cases
        assetSnapshot.setStrike(DataUtil.parseDouble("0.0"));
        // TODO
        assetSnapshot.setExpiry(LocalDateTime.now());

        return  assetSnapshot;
    }

    public void refresh() {
        this.driver.navigate().refresh();
    }

    private List<String> resolveTable(WebElement table) {
        return table.findElements(By.cssSelector("td")).stream()
            .map(WebElement::getText)
            .map(String::trim)
            .collect(Collectors.toList());
    }

}
