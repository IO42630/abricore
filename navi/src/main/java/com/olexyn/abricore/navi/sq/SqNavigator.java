package com.olexyn.abricore.navi.sq;

import com.olexyn.abricore.model.runtime.PositionDto;
import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.navi.Navigator;
import com.olexyn.abricore.navi.TabDriver;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.util.Constants;
import com.olexyn.abricore.util.DataUtil;
import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;
import com.olexyn.abricore.util.enums.OptionStatus;
import com.olexyn.abricore.util.enums.PositionStatus;
import com.olexyn.abricore.util.exception.WebException;
import com.olexyn.abricore.util.log.LogU;
import com.olexyn.propconf.PropConf;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.olexyn.abricore.navi.TabPurpose.OBSERVE_SQ;
import static com.olexyn.abricore.navi.TabPurpose.SYNC_CDF_SQ;
import static com.olexyn.abricore.util.Constants.EMPTY;
import static com.olexyn.abricore.util.Constants.ID;
import static com.olexyn.abricore.util.Constants.INPUT;
import static com.olexyn.abricore.util.Constants.NULL_INPUT_MESSAGE;
import static com.olexyn.abricore.util.Constants.SPAN;
import static com.olexyn.abricore.util.Constants.TABLE;
import static com.olexyn.abricore.util.Constants.UL;
import static com.olexyn.abricore.util.Constants.VALUE;
import static com.olexyn.abricore.util.enums.TradeStatus.CLOSE_EXECUTED;
import static com.olexyn.abricore.util.enums.TradeStatus.CLOSE_ISSUED;
import static com.olexyn.abricore.util.enums.TradeStatus.CLOSE_PENDING;
import static com.olexyn.abricore.util.enums.TradeStatus.OPEN_EXECUTED;
import static com.olexyn.abricore.util.enums.TradeStatus.OPEN_ISSUED;
import static com.olexyn.abricore.util.enums.TradeStatus.OPEN_PENDING;
import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.NumCalc.num;
import static com.olexyn.abricore.util.num.NumSerialize.fromStr;
import static com.olexyn.abricore.util.num.NumUtil.prettyStr;

/**
 * In Sq there are many screens, lets define some common abbreviations
 * <p>
 * ASSET_DETAIL : Screen where the detail of the asset is shown
 * EXE_TRADE : Screen where the trade is confirmed and submitted.
 */
@Service
@Lazy
public class SqNavigator extends SqSession implements Navigator {

    private static final String ACCOUNT_URL = "https://trade.swissquote.ch/sqtr_account";
    private static final String CORE_URL = "https://trade.swissquote.ch/sqb_core";
    private final String TRADES_URL = ACCOUNT_URL + "/AccountAjaxCtrl?commandName=dailyOrders&client=" + CREDENTIALS.get(ACCOUNT_NR);
    private final String POSITIONS_CASH_URL = ACCOUNT_URL + "/AccountCtrl?commandName=account&client=" + CREDENTIALS.get(ACCOUNT_NR) + "&hide_no_value=false";
    private final String POSITIONS_ASSETS_URL = ACCOUNT_URL + "/AccountCtrl?commandName=assets&client=" + CREDENTIALS.get(ACCOUNT_NR);

    private final AssetService assetService;
    private final SqMapper sqMapper;

    @Autowired
    public SqNavigator(
        AssetService assetService,
        SqMapper sqMapper,
        TabDriver td
    ) {
        super(td);
        this.assetService = assetService;
        this.sqMapper = sqMapper;
    }

    private void goToMainScreen() {
        synchronized(td) {
            td.get("https://trade.swissquote.ch/bank_security/login/RedirectAtLogin.action?l=d");
        }
    }

    private void search(String keyword) {
        synchronized(td) {
            td.findElement(By.className("defaultInput")).sendKeys(keyword);
        }
    }

    private void getAssetDetailScreen(OptionDto option) {
        String isin = option.getSqIsin();
        Exchange exchange = option.getExchange();
        Currency currency = option.getCurrency();
        String baseUrl = "https://premium.swissquote.ch/sq_mi/market/Detail.action";
        String isinParam = "?s=" + isin + '_' + exchange.getCode() + '_' + currency.name();
        get(StringUtils.join(baseUrl, isinParam));
    }

    /**
     * <b>PreTradeScreen</b> : Screen where the parameters for the trade are entered.
     */
    private void getPreTradeScreen(String isin) {
        String baseUrl = "https://trade.swissquote.ch/sqtr_trade/trading/placeOrder.action";
        String partnerParam = "?partnerSource=fullquote";
        String isinParam = "&isin=" + isin;
        get(StringUtils.join(baseUrl, partnerParam, isinParam));
    }

    private void get(String url) {
        synchronized(td) {
            if (td.getCurrentUrl().equals(url)) { td.refresh(); } else { td.get(url); }
        }
    }

    private Map<String, String> fetchPreTradeScreenTable(String isin) {
        synchronized(td) {
            td.switchToTab(OBSERVE_SQ);
            getPreTradeScreen(isin);
            if (td.getCurrentUrl().contains("sqtr_disclaimer")) {
                td.findElement(By.id("disclaimerAcceptCheckbox")).click();
                td.executeScript("javascript:disclaimerModule.accept()");
            }
            return resolveTable(td.findElement(By.className("tableContent")));
        }
    }

    /**
     * Fetch detail info for pre-filling trade.
     */
    public @Nullable SnapshotDto fetchPreTradeScreenSnap(AssetDto asset) {
        if (asset == null || asset.getSqIsin() == null) {
            LogU.warnPlain(NULL_INPUT_MESSAGE);
            return null;
        }
        LogU.infoPlain("Fetching TradeScreenSnap for [%s].", asset);
        try {
            var quoteMap = fetchPreTradeScreenTable(asset.getSqIsin());
            return sqMapper.quoteMapToSnapShot(quoteMap);
        } catch (NoSuchElementException e) {
            // TODO timeout is slow, find a faster way.
            if (asset instanceof OptionDto) {
                ((OptionDto) asset).setStatus(OptionStatus.DEAD);
            }
            return null;
        }
    }

    /**
     * Fetch option data from the AssetDetails screen. <br>
     * This creates a complete data set. <br>
     * However the data is probably delayed, thus e.g. price will need to be updated via PreTrade screen.
     */
    public OptionDto fetchAssetDetails(OptionDto option) {
        Map<String, String> quoteMap;
        synchronized(td) {
            td.switchToTab(OBSERVE_SQ);
            getAssetDetailScreen(option);
            if (td.getCurrentUrl().contains("sqi_web_search")) {
                option.setStatus(OptionStatus.DEAD);
                return option;
            }
            quoteMap = sqMapper.simplifyKeys(resolveTable(td.findElement(By.className("FullquoteTable"))));
            String href = td.getByText(quoteMap.get(sqMapper.UNDERLYING)).getAttribute("href");
            String underlyingIsin = DataUtil.resolveHrefParams(href).get("s").split(UL)[0];
            quoteMap.put(sqMapper.UNDERLYING_ISIN, underlyingIsin);
        }
        sleep(500L);
        option = sqMapper.quoteMapToOption(quoteMap);
        option.setStatus(OptionStatus.KNOWN);
        return option;
    }

    /**
     * Returns a Map of Row-Nr. -> List of Cells in Row. <br>
     * Row-Nr. 0 is usually the Header.
     */
    private Map<Integer, List<String>> resolveTableMap(WebElement table) {
        Map<Integer, List<String>> tableMap = new HashMap<>();
        int rowCount = 0;

        List<WebElement> rowElements = table.findElements(By.cssSelector("tr")).stream()
            .filter(x -> StringUtils.isNotBlank(x.getText()))
            .toList();
        for (WebElement rowElement : rowElements) {
            List<String> cells = rowElement.findElements(By.cssSelector("td")).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .toList();
            tableMap.put(rowCount++, cells);
        }
        return tableMap;
    }

    private Map<String, String> resolveTable(WebElement table) {
        Map<Integer, List<String>> tableMap = resolveTableMap(table);

        Map<String, String> tableContents = new HashMap<>();
        int rowSize = tableMap.get(0).size();

        String[] tmpKey = new String[rowSize];
        String[] tmpVal = new String[rowSize];

        for (var rowEntry : tableMap.entrySet()) {

            List<String> rowCells = rowEntry.getValue();

            for (int i = 0; i < rowSize; i++) {
                String value = rowCells.get(i);
                if (rowEntry.getKey() % 2 == 0) {
                    tmpKey[i] = value;
                }
                if (rowEntry.getKey() % 2 == 1) {
                    tmpVal[i] = value;
                }
            }

            for (int i = 0; i < rowSize; i++) {
                if (tmpKey[i] != null && tmpVal[i] != null) {
                    tableContents.put(tmpKey[i], tmpVal[i]);
                    tmpKey[i] = null;
                    tmpVal[i] = null;
                }
            }
        }

        return tableContents;
    }

    /**
     *
     */
    public Set<OptionDto> fetchOptions(StrategyDto strategy, long lastTraded, long minDistance, long maxDistance) {
        synchronized(td) {
            Set<OptionDto> result = new HashSet<>();
            result.addAll(fetchOptions(strategy, OptionType.CALL, lastTraded, minDistance, maxDistance));
            result.addAll(fetchOptions(strategy, OptionType.PUT, lastTraded, minDistance, maxDistance));
            return result;
        }
    }

    /**
     * POST-CONDITION : Option.STATE = ALIVE
     */
    private Set<OptionDto> fetchOptions(
        StrategyDto strategy,
        OptionType optionType,
        long lastTraded, long minDistance,
        long maxDistance
    ) {
        AssetDto asset = strategy.getUnderlying();
        long minRatio = strategy.getMinRatio();
        long maxRatio = strategy.getMaxRatio();

        synchronized(td) {
            LogU.infoStart("fetch %s options for %s", optionType, asset);
            td.switchToTab(SYNC_CDF_SQ);
            Set<OptionDto> result = new HashSet<>();
            if (asset.getSqIsin() == null || asset.getSqIsin().isEmpty()) {
                LogU.warnPlain("Requested Asset has no ISIN. Returning empty Set");
                return result;
            }

            // 120 barrier options
            // 110 options
            String up = optionType == OptionType.CALL ? "up" : "down";
            td.get("https://premium.swissquote.ch/sqi_web_search/market/equity/swissderivatives/" +
                "SwissDerivativeSearch.action?&searchFilter.bean.trend=" +
                up +
                "&searchFilter.bean.underlyingIsin=" +
                asset.getSqIsin() +
                "&searchFilter.bean.productClass=120\n");

            // mono underlying
            td.setRadio(By.id("searchFilter.bean.monoUnderlying1"), true);

            // sdots
            td.setRadio(By.id("searchFilter.bean.exchangeId2"), true);

            // set CHF
            td.setComboByDataValue(By.id("searchFilter.bean.currencyFilter"), Currency.CHF.name());

            // set UBS
            td.setComboByDataValue(By.id("searchFilter.bean.issuerIdFilter"), "ubs");


            // filter distance to lastTraded
            long minStrike;
            long maxStrike;
            if (optionType == OptionType.CALL) {
                minStrike = lastTraded - maxDistance;
                maxStrike = lastTraded - minDistance;
            } else {
                minStrike = lastTraded + minDistance;
                maxStrike = lastTraded + maxDistance;
            }
            td.findElement(By.name("searchFilter.bean.minStrike")).sendKeys(prettyStr(minStrike, 3));
            td.findElement(By.name("searchFilter.bean.maxStrike")).sendKeys(prettyStr(maxStrike, 3));

            // filter by ratio
            td.findElement(By.name("searchFilter.bean.minRatio")).sendKeys(prettyStr(minRatio, 3));
            td.findElement(By.name("searchFilter.bean.maxRatio")).sendKeys(prettyStr(maxRatio, 3));


            // get result rows
            sleep(1000);
            List<String> cellTexts = td.findElement(By.id("results"))
                .findElements(By.cssSelector("td"))
                .stream().map(WebElement::getText)
                .collect(Collectors.toList());

            String[] errorStrings = {"Keine Produkte gefunden"};
            for (String errorString : errorStrings) {
                if (cellTexts.isEmpty() || cellTexts.get(0).contains(errorString)) {
                    LogU.warnPlain("No Options found for requested minDistance %s, maxDistance %s", minDistance, maxDistance);
                    return result;
                }
            }

            OptionDto tempAsset = null;
            for (String cellText : cellTexts) {
                int mod = cellTexts.indexOf(cellText) % 11;
                switch (mod) {
                    case 0 -> {
                        String valorNr = cellText.replace("Trade  ", EMPTY);
                        String detailUrl = td.getByText(valorNr).getAttribute("href");
                        String urlPayload = detailUrl.substring(detailUrl.indexOf("?s=") + 3);
                        String[] payload = urlPayload.split(Constants.UL);
                        String isin = payload[0];
                        tempAsset = new OptionDto(isin);
                        tempAsset.setSqIsin(isin);
                        tempAsset.setExchange(Exchange.ofCode(payload[1]));
                        tempAsset.setCurrency(Currency.valueOf(payload[2]));
                        tempAsset.setStatus(OptionStatus.FOUND);
                    }
                    case 5 -> {
                        assert tempAsset != null;
                        tempAsset.setStrike(fromStr(cellText));
                        tempAsset.setOptionType(optionType);
                        tempAsset.setUnderlying(asset);
                        result.add(tempAsset);
                    }
                    default -> {
                    }
                }
            }
            LogU.infoEnd("fetch %s options for %s", optionType, asset);
            return result;
        }
    }

    public Set<PositionDto> fetchPositions() {
        Set<PositionDto> positions = new HashSet<>();
        positions.addAll(fetchCashPositions());
        positions.addAll(fetchAssetPositions());
        return positions;
    }

    private Set<PositionDto> fetchCashPositions() {
        Set<PositionDto> positions = new HashSet<>();
        Map<Integer, List<String>> cashTable;
        LogU.infoStart("fetch CASH POSITIONS.");
        synchronized(td) {
            td.get(POSITIONS_CASH_URL);
            cashTable = resolveTableMap(td.getByFieldValue(TABLE, ID, "account-table"));
        }
        for (var row : cashTable.entrySet()) {
            if (row.getKey() == 0 || row.getValue().get(0).startsWith("Total")) {
                continue;
            }
            var position = new PositionDto();
            position.setAsset(assetService.ofName(row.getValue().get(2)));
            position.setStatus(PositionStatus.CONFIRMED);
            position.setAmount(fromStr(row.getValue().get(4)));
            if (StringUtils.isEmpty(row.getValue().get(3))) {
                position.setPrice(ONE);
            } else {
                position.setPrice(fromStr(row.getValue().get(3)));
            }
            positions.add(position);
        }
        LogU.infoEnd("fetched %s CASH POSITIONS.", positions.size());
        return positions;
    }

    private Set<PositionDto> fetchAssetPositions() {
        Set<PositionDto> positions = new HashSet<>();
        synchronized(td) {
            LogU.infoStart("fetch ASSET POSITIONS.");
            td.get(POSITIONS_ASSETS_URL);
            var assetsTable = resolveTableMap(td.getByFieldValue(TABLE, ID, "assets-table"));
            for (var row : assetsTable.entrySet()) {
                if (!row.getValue().contains("Buy")) { continue; }
                String symbol = row.getValue().get(3);
                td.get(POSITIONS_ASSETS_URL);
                String href = td.getByText(symbol).getAttribute("href");
                var option = getOptionData(href);
                var position = new PositionDto();
                position.setAsset(option);
                position.setStatus(PositionStatus.CONFIRMED);
                position.setAmount(fromStr(row.getValue().get(4)));
                position.setPrice(fromStr(row.getValue().get(5)));
                positions.add(position);
            }
            LogU.infoEnd("fetched %s ASSET POSITIONS.", positions.size());
        }
        return positions;
    }

    public Set<TradeDto> fetchTrades() {
        LogU.infoStart("fetch TRADES.");
        Set<TradeDto> trades = new HashSet<>();
        Map<Integer, List<String>> tradesTable;
        synchronized(td) {
            td.get(TRADES_URL);
            tradesTable = resolveTableMap(td.getByFieldValue(TABLE, ID, "open-orders-table"));
        }
        var header = tradesTable.get(0);
        int buySellCol = header.indexOf("K/V");
        int amountCol = header.indexOf("Anz.");
        int symbolCol = header.indexOf("Symbol");
        int priceCol = header.indexOf("Preis(e)");
        int statusCol = header.indexOf("Status");
        int transactionIdCol = header.indexOf("Auftrag");
        int timeCol = header.indexOf("Datum");

        for (var rowEntry : tradesTable.entrySet()) {
            if (rowEntry.getKey() == 0) {
                continue;
            }
            var row = rowEntry.getValue();
            var asset = getOption(row.get(symbolCol));
            var trade = new TradeDto(asset);

            trade.setAmount(fromStr(row.get(amountCol)));

            String buySell = row.get(buySellCol);
            long price = fromStr(row.get(priceCol));
            String transactionId = row.get(transactionIdCol);

            if (buySell.equals("Kauf")) {
                // BUY
                switch (row.get(statusCol)) {
                    case "Offen" -> trade.setStatus(OPEN_PENDING);
                    case "Ausgeführt" -> {
                        trade.setStatus(OPEN_EXECUTED);
                        LocalTime localTime = DataUtil.parseTime(row.get(timeCol));
                        trade.setBuyInstant(DataUtil.getInstant(LocalDate.now(), localTime));
                    }
                    default -> throw new WebException();
                }
                trade.setBuyPrice(price);
                trade.setBuyId(transactionId);
            } else if (buySell.equals("Verkauf")) {
                // SELL
                switch (row.get(statusCol)) {
                    case "Offen", "Unreleased" -> trade.setStatus(CLOSE_PENDING);
                    case "Ausgeführt" -> {
                        trade.setStatus(CLOSE_EXECUTED);
                        trade.setSellInstant(Instant.now());
                    }
                    default -> throw new WebException();
                }
                trade.setSellPrice(price);
                trade.setSellId(transactionId);
            }

            trades.add(trade);
        }
        LogU.infoEnd("fetched %s TRADES", trades.size());
        return trades;
    }

    public OptionDto getOption(String symbol) {
        String href;
        synchronized(td) {
            td.get(TRADES_URL);
            href = td.getByText(symbol).getAttribute("href");
        }
        return getOptionData(href);
    }

    /**
     * Check of DATA exists in AssetService. <br>
     * If not: <br>
     * First gather ISIN / currency / market from HREF. <br>
     * Then use ISIN to gather data from detail page.
     */
    private OptionDto getOptionData(String href) {
        String isin = sqMapper.hrefToIsin(href);
        AssetDto asset = assetService.ofIsin(isin);
        if (asset != null) {
            return (OptionDto) asset;
        }
        LogU.warnPlain("Can't find Asset %s in AssetService. Adding as new Asset.", isin);
        OptionDto optionFromHref = sqMapper.hrefToOption(href);
        OptionDto option = optionFromHref.mergeFrom(fetchAssetDetails(optionFromHref));
        assetService.addAsset(option);
        return option;
    }


    /**
     * Fetches the details of an asset from the detail page.
     */
    public TradeDto placeBuyOrder(TradeDto trade) {
        if (PropConf.getBool("trade.sq.disable.trade")) {
            LogU.infoPlain("DUMMY BUY [%s] of [%s] @ [%s]", trade.getAmount(), trade.getAsset(), trade.getBuyPrice());
            return trade;
        }
        // TODO expand stub
        LogU.infoPlain("BUY [%s] of [%s] @ [%s]", trade.getAmount(), trade.getAsset(), trade.getBuyPrice());
        synchronized(td) {
            getPreTradeScreen(trade.getAsset().getSqIsin());
            // the BUY radio is not interactive, so we click SELL and ARROW_UP.
            td.getByFieldValue(INPUT, VALUE, "S").click();
            td.getByFieldValue(INPUT, VALUE, "S").sendKeys(Keys.ARROW_UP);
            td.getByFieldValue(INPUT, "name", "orderDescription.quantity")
                .sendKeys(String.valueOf(num(trade.getAmount())));
            td.getByFieldValue(INPUT, "name", "orderDescription.limit")
                .sendKeys(prettyStr(trade.getBuyPrice(), 2));
            td.findByCss("a[id*='button.continueTrade']")
                .click();
            td.getByFieldValue("a", ID, "button.placeOrder").click();
            trade.setBuyId(td.getByFieldValue(SPAN, ID, "orderNumber").getText());
        }
        trade.setStatus(OPEN_ISSUED);
        return trade;
    }

    /**
     * Fetches the details of an asset from the detail page.
     */
    public TradeDto placeSellOrder(TradeDto trade) {
        if (PropConf.getBool("trade.sq.disable.trade")) {
            LogU.infoPlain("DUMMY SELL [%s] of [%s] @ [%s]", trade.getAmount(), trade.getAsset(), trade.getBuyPrice());
            return trade;
        }
        LogU.infoPlain("SELL [%s] of [%s] at [%s]", trade.getAmount(), trade.getAsset(), trade.getSellPrice());
        synchronized(td) {
            getPreTradeScreen(trade.getAsset().getSqIsin());
            td.getByFieldValue(INPUT, VALUE, "S").click(); // SELL
            td.getByFieldValue(INPUT, "name", "orderDescription.quantity")
                .sendKeys(String.valueOf(num(trade.getAmount())));
            td.getByFieldValue(INPUT, "name", "orderDescription.limit")
                .sendKeys(prettyStr(trade.getSellPrice(), 3));
            td.getByFieldValue("a", ID, "button.continueTrade")
                .click();
            td.getByFieldValue("a", ID, "button.placeOrder")
                .click();
            trade.setSellId(td.getByFieldValue(SPAN, ID, "orderNumber").getText());
        }
        trade.setStatus(CLOSE_ISSUED);
        return trade;
    }

}
