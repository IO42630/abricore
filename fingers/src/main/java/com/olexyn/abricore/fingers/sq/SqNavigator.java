package com.olexyn.abricore.fingers.sq;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.SymbolsService;
import com.olexyn.abricore.fingers.DriverUtil;
import com.olexyn.abricore.fingers.Navigator;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.model.options.BarrierOption;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.model.options.OptionType;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    public void getCdf(Asset asset, OptionType optionType, ANum strike, ANum distance, Double minRatio, Double maxRatio) throws InterruptedException {
        // 120 barrier options
        // 110 options
        String up = optionType == OptionType.CALL ? "up" : "down";
        driver.get("https://premium.swissquote.ch/sqi_web_search/market/equity/swissderivatives/" +
            "SwissDerivativeSearch.action?&searchFilter.bean.trend=" +
            up +
            "&searchFilter.bean.underlyingIsin=" +
            asset.getSqIsin() +
            "&searchFilter.bean.productClass=120\n");

        // mono underlying
        DriverUtil.setRadio(driver, By.id("searchFilter.bean.monoUnderlying1"), true);

        // sdots
        DriverUtil.setRadio(driver, By.id("searchFilter.bean.exchangeId2"), true);

        // set CHF
        DriverUtil.setComboByDataValue(driver, By.id("searchFilter.bean.currencyFilter"), Currency.CHF.name());

        // set UBS
        DriverUtil.setComboByDataValue(driver, By.id("searchFilter.bean.issuerIdFilter"), "ubs");


        // filter by strike
        if (optionType == OptionType.CALL) {
            driver.findElement(By.name("searchFilter.bean.minStrike")).sendKeys(strike.minus(distance).toString(3));
            driver.findElement(By.name("searchFilter.bean.maxStrike")).sendKeys(strike.num().toString(3));
        } else {
            driver.findElement(By.name("searchFilter.bean.minStrike")).sendKeys(strike.num().plus(new ANum(1)).toString(3));
            driver.findElement(By.name("searchFilter.bean.maxStrike")).sendKeys(strike.plus(distance).toString(3));
        }

        // filter by ratio
        driver.findElement(By.name("searchFilter.bean.minRatio")).sendKeys(minRatio.toString());
        driver.findElement(By.name("searchFilter.bean.maxRatio")).sendKeys(maxRatio.toString());


        // get result rows
        Thread.sleep(1000L);
        List<String> cellTexts = driver.findElement(By.id("results"))
            .findElements(By.cssSelector("td"))
            .stream().map(WebElement::getText)
            .collect(Collectors.toList());

        List<Asset> assets = new ArrayList<>();
        BarrierOption tempAsset = null;
        for (String cellText : cellTexts) {
            int mod = cellTexts.indexOf(cellText) % 11;
            switch (mod) {
                case 0:
                    tempAsset = new BarrierOption( cellText.split("  ")[1]);
                    break;
                case 5:
                    tempAsset.setStrike(ANum.of(cellText));
                    tempAsset.setType(optionType);
                    AssetService.addAsset(tempAsset);
                    break;
                default:
                    break;
            }
        }
        Set<Asset> assetList = SymbolsService.SYMBOLS;

        int br = 0;
    }

}
