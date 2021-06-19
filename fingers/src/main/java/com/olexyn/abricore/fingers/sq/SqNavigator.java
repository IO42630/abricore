package com.olexyn.abricore.fingers.sq;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.fingers.Navigator;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.DataUtil;
import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.Constants.EMPTY;

public class SqNavigator implements Navigator {

    private final WebDriver driver;

    public SqNavigator(WebDriver driver) {
        this.driver = driver;
    }

    public void goToMainScreen() {
        driver.get("https://trade.swissquote.ch/bank_security/login/RedirectAtLogin.action?l=d");
    }

    public void search(String keyword) {
        driver.findElement(By.className("defaultInput")).sendKeys(keyword);
    }

    public void getTradeWindow(String isin, Currency currency, Exchange exchange) {
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
        if (driver.getCurrentUrl().equals(url)) {
            refresh();
        } else {
            driver.get(url);
        }
    }


    @Override
    public AssetSnapshot resolveQuote(Asset asset, Interval interval) {

        getTradeWindow(asset.getSqIsin(), asset.getCurrency(), asset.getExchange());

        List<String> resolve = resolveTable(driver.findElement(By.className("tableContent")));

        AssetSnapshot assetSnapshot = new AssetSnapshot(AssetService.ofName(resolve.get(22)));
        // assetSnapshot.setMultiplier(ANum.valueOf(resolve.get(7)));
        assetSnapshot.setInstant(Instant.now());
        // assetSnapshot.setBidVol(DataUtil.parseANum(resolve.get(12)));
        assetSnapshot.getPrice().setBid(ANum.of(resolve.get(13)));


        assetSnapshot.getPrice().setAsk(ANum.of(resolve.get(14)));
        // assetSnapshot.setAskVol(DataUtil.parseANum(resolve.get(15)));
        // TODO account for cases


        assetSnapshot.getAsset().setCurrency(Currency.CHF);

        if (asset instanceof Option) {
            Option option = (Option) asset;
            option.setStrike(ANum.of("0.0"));
            option.setExpiry(Instant.now());
        }

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
