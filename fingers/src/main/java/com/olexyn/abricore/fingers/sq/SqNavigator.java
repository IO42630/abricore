package com.olexyn.abricore.fingers.sq;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.Navigator;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.AssetType;
import com.olexyn.abricore.model.options.BarrierOption;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.model.options.OptionType;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.LogUtil;
import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.Constants.EMPTY;

public class SqNavigator implements Navigator {

    private static final Logger LOGGER = LogUtil.get(SqNavigator.class);

    public void goToMainScreen() {
        Session.getDriver().get("https://trade.swissquote.ch/bank_security/login/RedirectAtLogin.action?l=d");
    }

    public void search(String keyword) {
        Session.getDriver().findElement(By.className("defaultInput")).sendKeys(keyword);
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
        if (Session.getDriver().getCurrentUrl().equals(url)) {
            refresh();
        } else {
            Session.getDriver().get(url);
        }
    }


    @Override
    public AssetSnapshot resolveQuote(Asset asset) {

        getTradeWindow(asset.getSqIsin(), asset.getCurrency(), asset.getExchange());

        List<String> resolve = resolveTable(Session.getDriver().findElement(By.className("tableContent")));

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
        Session.getDriver().navigate().refresh();
    }

    private List<String> resolveTable(WebElement table) {
        return table.findElements(By.cssSelector("td")).stream()
            .map(WebElement::getText)
            .map(String::trim)
            .collect(Collectors.toList());
    }

    public Set<Asset> getCdf(Asset asset, OptionType optionType, ANum strike, ANum distance, Double minRatio, Double maxRatio) throws InterruptedException {
        Set<Asset> result = new HashSet<>();
        if (asset.getSqIsin() == null || asset.getSqIsin().isEmpty()) {
            LOGGER.warning("Requested Asset has no ISIN. Returning empty Set");
            return result;
        }

        // 120 barrier options
        // 110 options
        String up = optionType == OptionType.CALL ? "up" : "down";
        Session.getDriver().get("https://premium.swissquote.ch/sqi_web_search/market/equity/swissderivatives/" +
            "SwissDerivativeSearch.action?&searchFilter.bean.trend=" +
            up +
            "&searchFilter.bean.underlyingIsin=" +
            asset.getSqIsin() +
            "&searchFilter.bean.productClass=120\n");

        // mono underlying
        Session.setRadio(By.id("searchFilter.bean.monoUnderlying1"), true);

        // sdots
        Session.setRadio(By.id("searchFilter.bean.exchangeId2"), true);

        // set CHF
        Session.setComboByDataValue(By.id("searchFilter.bean.currencyFilter"), Currency.CHF.name());

        // set UBS
        Session.setComboByDataValue(By.id("searchFilter.bean.issuerIdFilter"), "ubs");


        // filter by strike
        if (optionType == OptionType.CALL) {
            Session.getDriver().findElement(By.name("searchFilter.bean.minStrike")).sendKeys(strike.minus(distance).toString(3));
            Session.getDriver().findElement(By.name("searchFilter.bean.maxStrike")).sendKeys(strike.num().toString(3));
        } else {
            Session.getDriver().findElement(By.name("searchFilter.bean.minStrike")).sendKeys(strike.num().plus(new ANum(1)).toString(3));
            Session.getDriver().findElement(By.name("searchFilter.bean.maxStrike")).sendKeys(strike.plus(distance).toString(3));
        }

        // filter by ratio
        Session.getDriver().findElement(By.name("searchFilter.bean.minRatio")).sendKeys(minRatio.toString());
        Session.getDriver().findElement(By.name("searchFilter.bean.maxRatio")).sendKeys(maxRatio.toString());


        // get result rows
        Thread.sleep(1000L);
        List<String> cellTexts = Session.getDriver().findElement(By.id("results"))
            .findElements(By.cssSelector("td"))
            .stream().map(WebElement::getText)
            .collect(Collectors.toList());


        BarrierOption tempAsset = null;
        for (String cellText : cellTexts) {
            int mod = cellTexts.indexOf(cellText) % 11;
            switch (mod) {
                case 0:
                    tempAsset = new BarrierOption( cellText.split("  ")[1]);
                    tempAsset.setAssetType(AssetType.BARRIER_OPTION);
                    break;
                case 5:
                    assert tempAsset != null;
                    tempAsset.setStrike(ANum.of(cellText));
                    tempAsset.setOptionType(optionType);
                    tempAsset.setUnderlying(asset);
                    result.add(tempAsset);
                    break;
                default:
                    break;
            }
        }
        return result;
    }

}
