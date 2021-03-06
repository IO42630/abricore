package com.olexyn.abricore.fingers.sq;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.Navigator;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.AssetType;
import com.olexyn.abricore.model.options.BarrierOption;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.model.options.OptionType;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.Constants;
import com.olexyn.abricore.util.LogUtil;
import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.olexyn.abricore.fingers.TabPurpose.OBSERVE_SW;
import static com.olexyn.abricore.fingers.TabPurpose.SYNC_CDF_SQ;
import static com.olexyn.abricore.util.Constants.EMPTY;

public class SqNavigator extends SqSession implements Navigator {

    private static final Logger LOGGER = LogUtil.get(SqNavigator.class);

    private static void goToMainScreen() {
        DRIVER.get("https://trade.swissquote.ch/bank_security/login/RedirectAtLogin.action?l=d");
    }

    private static void search(String keyword) {
        DRIVER.findElement(By.className("defaultInput")).sendKeys(keyword);
    }

    private static void getTradeWindow(String isin, Currency currency, Exchange exchange) {

        String partnerParam = "?partnerSource=fullquote";
        String exchangeParam = "&stockExchange=" + exchange.getCode();
        String currencyParam = "&currency=" + currency.toString();
        String isinParam = "&isin=" + isin;
        String marketParam = "&tradingMarketId=SWISS_OTC";

        String url = String.join(
            EMPTY,
            "https://trade.swissquote.ch/sqtr_trade/trading/placeOrder.action",
            partnerParam,
            exchangeParam,
            currencyParam,
            isinParam,
            marketParam
        );
        if (DRIVER.getCurrentUrl().equals(url)) {
            refresh();
        } else {
            DRIVER.get(url);
        }
    }

    public static AssetSnapshot fetchQuote(Asset asset) {
        switchToTab(OBSERVE_SW);
        getTradeWindow(asset.getSqIsin(), asset.getCurrency(), asset.getExchange());

        if (DRIVER.getCurrentUrl().contains("sqtr_disclaimer")) {
            DRIVER.findElement(By.id("disclaimerAcceptCheckbox")).click();
            execute("javascript:disclaimerModule.accept()");
        }

        Map<String, String> tableData = resolveTable(DRIVER.findElement(By.className("tableContent")));
        return SqQuoteSnapshot.of(tableData, asset).toAssetSnapShot();
    }

    private static Map<String, String> resolveTable(WebElement table) {

        Map<Integer, List<String>> tableMap = new HashMap<>();
        int rowCount = 0;

        List<WebElement> rowElements = table.findElements(By.cssSelector("tr"));
        for (WebElement rowElement : rowElements) {
            List<String> cells = rowElement.findElements(By.cssSelector("td")).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
            tableMap.put(rowCount++, cells);
        }

        Map<String, String> tableContents = new HashMap<>();
        int rowSize = tableMap.get(0).size();

        String[] tmpKey = new String[rowSize];
        String[] tmpVal = new String[rowSize];

        for (Entry<Integer, List<String>> rowEntry : tableMap.entrySet()) {

            List<String> rowCells = rowEntry.getValue();



            for (int i =0 ; i < rowSize; i++) {
                    String value = rowCells.get(i);
                    if (rowEntry.getKey() % 2 == 0) {
                        tmpKey[i] = value;
                    }
                    if (rowEntry.getKey() % 2 == 1) {
                        tmpVal[i] = value;
                    }
            }

            for (int i =0 ; i < rowSize; i++) {
                if (tmpKey[i] != null && tmpVal[i] != null) {
                    tableContents.put(tmpKey[i], tmpVal[i]);
                    tmpKey[i] = null;
                    tmpVal[i] = null;
                }
            }
        }

        return tableContents;
    }

    public static Set<Option> fetchOptions(Asset asset, ANum distance, Double minRatio, Double maxRatio) throws InterruptedException {
        Set<Option> result = new HashSet<>();
        result.addAll(fetchOptions(asset, OptionType.CALL, distance, minRatio, maxRatio));
        result.addAll(fetchOptions(asset, OptionType.PUT, distance, minRatio, maxRatio));
        return result;
    }

    public static Set<Option> fetchOptions(Asset asset, OptionType optionType, ANum distance, Double minRatio, Double maxRatio) throws InterruptedException {
        synchronized (Session.class) {
            switchToTab(SYNC_CDF_SQ);
            Set<Option> result = new HashSet<>();
            if (asset.getSqIsin() == null || asset.getSqIsin().isEmpty()) {
                LOGGER.warning("Requested Asset has no ISIN. Returning empty Set");
                return result;
            }

            // 120 barrier options
            // 110 options
            String up = optionType == OptionType.CALL ? "up" : "down";
            DRIVER.get("https://premium.swissquote.ch/sqi_web_search/market/equity/swissderivatives/" +
                "SwissDerivativeSearch.action?&searchFilter.bean.trend=" +
                up +
                "&searchFilter.bean.underlyingIsin=" +
                asset.getSqIsin() +
                "&searchFilter.bean.productClass=120\n");

            // mono underlying
            setRadio(By.id("searchFilter.bean.monoUnderlying1"), true);

            // sdots
            setRadio(By.id("searchFilter.bean.exchangeId2"), true);

            // set CHF
            setComboByDataValue(By.id("searchFilter.bean.currencyFilter"), Currency.CHF.name());

            // set UBS
            setComboByDataValue(By.id("searchFilter.bean.issuerIdFilter"), "ubs");


            // filter distance to lastTraded
            ANum lastTraded = SeriesService.getLastTraded(asset);
            if (optionType == OptionType.CALL) {
                DRIVER.findElement(By.name("searchFilter.bean.minStrike")).sendKeys(lastTraded.minus(distance).toString(3));
                DRIVER.findElement(By.name("searchFilter.bean.maxStrike")).sendKeys(lastTraded.num().toString(3));
            } else {
                DRIVER.findElement(By.name("searchFilter.bean.minStrike")).sendKeys(lastTraded.num().plus(new ANum(1)).toString(3));
                DRIVER.findElement(By.name("searchFilter.bean.maxStrike")).sendKeys(lastTraded.plus(distance).toString(3));
            }

            // filter by ratio
            DRIVER.findElement(By.name("searchFilter.bean.minRatio")).sendKeys(minRatio.toString());
            DRIVER.findElement(By.name("searchFilter.bean.maxRatio")).sendKeys(maxRatio.toString());


            // get result rows
            Thread.sleep(1000L);
            List<String> cellTexts = DRIVER.findElement(By.id("results"))
                .findElements(By.cssSelector("td"))
                .stream().map(WebElement::getText)
                .collect(Collectors.toList());


            BarrierOption tempAsset = null;
            for (String cellText : cellTexts) {
                int mod = cellTexts.indexOf(cellText) % 11;
                switch (mod) {
                    case 0:
                        String valorNr = cellText.replace("Trade  ", EMPTY);
                        String detailUrl = getByText(valorNr).getAttribute("href");
                        String urlPayload = detailUrl.substring(detailUrl.indexOf("?s=") + 3);
                        String[] payload = urlPayload.split(Constants.UL);
                        String isin = payload[0];
                        tempAsset = new BarrierOption(isin);
                        tempAsset.setSqIsin(isin);
                        tempAsset.setExchange(Exchange.ofCode(payload[1]));
                        tempAsset.setCurrency(Currency.valueOf(payload[2]));
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

}
