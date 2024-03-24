package com.olexyn.abricore.navi.sq;

import com.olexyn.abricore.model.runtime.SqDetail;
import com.olexyn.abricore.model.runtime.assets.AssetType;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import com.olexyn.abricore.util.DataUtil;
import com.olexyn.abricore.util.enums.Currency;
import com.olexyn.abricore.util.enums.Exchange;
import com.olexyn.abricore.util.enums.OptionStatus;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Map.Entry;

import static com.olexyn.abricore.util.Constants.DASH;
import static com.olexyn.abricore.util.Constants.EMPTY;
import static com.olexyn.abricore.util.Constants.SLASH;
import static com.olexyn.abricore.util.Constants.SPACE;
import static com.olexyn.abricore.util.Constants.UL;
import static com.olexyn.abricore.util.num.NumSerialize.fromStr;

@UtilityClass
public class SqMapper {

    private static final String STRIKE = "Strike";
    private static final String PRICE = "Preis";
    private static final String TYPE = "Titelart";
    public static final String UNDERLYING = "Basiswert";
    public static final String UNDERLYING_ISIN = "underlyingIsin";
    private static final String GELDKURS_KEY = "Geldkurs ";





    /**
     * quoteMap -> OPTION <br>
     * Result is not meant to be complete. <br>
     * Use Option.merge() and Option.isComplete() to complete the Option data.
     */
    public static OptionDto quoteMapToOption(Map<String, String> quoteMap) {
        OptionDto option = new OptionDto();
        quoteMap = simplifyKeys(quoteMap);
        // STRIKE
        String value = quoteMap.get(STRIKE);
        if (value.contains(SLASH)) {
            option.setStrike(fromStr(value.substring(0, value.indexOf(SLASH)).trim()));
        } else if (value.contains(SPACE)) {
            option.setStrike(fromStr(value.substring(0, value.indexOf(SPACE))));
        }
        // EXPIRY
        value = quoteMap.get("Verfall");
        if (
            StringUtils.isBlank(value)
                || value.equals(DASH)
                || value.contains("Open-End")
                || value.contains("Ohne Ende")
        ) {
            option.setExpiry(Instant.now().plus(Duration.ofDays(730)));
        } else {
            option.setExpiry(Instant.parse(value));
        }
        // RATIO
        String ratioStr = quoteMap.get("Multiplier");
        if (ratioStr == null) { ratioStr = quoteMap.get("Ratio"); }
        option.setRatio(fromStr(ratioStr));
        // OPTION TYPE
        long price = fromStr(quoteMap.get(PRICE).split(SPACE)[0]);
        if (price < option.getStrike()) {
            option.setOptionType(OptionType.PUT);
        } else if (price > option.getStrike()) {
            option.setOptionType(OptionType.CALL);
        }
        // ASSET TYPE
        AssetType assetType = AssetType.ofSqCode(quoteMap.get(TYPE).split(SPACE)[0]);
        if (assetType != null) {
            option.setAssetType(assetType);
        } else if (quoteMap.get(TYPE).contains("Knock")) {
            option.setAssetType(AssetType.BARRIER_OPTION);
        }
        option.setStatus(OptionStatus.FOUND);
        return option;
    }

    public static Map<String, String> simplifyKeys(Map<String, String> quoteMap) {
        Map<String, String> simplifiedMap = new java.util.HashMap<>(quoteMap);
        for (var entry : quoteMap.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            if (key.startsWith(STRIKE)) {
                simplifiedMap.put(STRIKE, value);
            }
            if (key.startsWith(PRICE)) {
                simplifiedMap.put(PRICE, value);
            }
            if (key.startsWith(TYPE)) {
                simplifiedMap.put(TYPE, value);
            }
            if (key.startsWith(UNDERLYING)) {
                simplifiedMap.put(UNDERLYING, value);
            }
        }
        return simplifiedMap;
    }

    /**
     * quoteMap -> SNAPSHOT
     */
    public static SnapshotDto quoteMapToSnapShot(Map<String, String> quoteMap) {
        var snap = new SnapshotDto();
        for (Entry<String, String> entry : quoteMap.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (key.startsWith(GELDKURS_KEY)) {
                snap.setBidPrice(fromStr(val));
            }
            if (key.startsWith("Briefkurs ")) {
                snap.setAskPrice(fromStr(val));

            }
        }
        LocalDate date = quoteMap.keySet().stream()
            .filter(x -> x.startsWith("Basiswert Preis "))
            .map(x -> x.replace("Basiswert Preis ", EMPTY).trim())
            .map(x -> {
                if (x.contains(DASH)) {
                    return LocalDate.parse(x, DataUtil.SQ_DATE_FORMATTER);
                }
                return LocalDate.now();
            })
            .findFirst().orElseThrow();
        LocalTime time = quoteMap.keySet().stream()
            .filter(x -> x.startsWith(GELDKURS_KEY))
            .map(x -> x.replace(GELDKURS_KEY, EMPTY).trim())
            .map(LocalTime::parse)
            .findFirst().orElseThrow();
        snap.setInstant(DataUtil.getInstant(date, time));
        return snap;
    }

    public static String hrefToIsin(String href) {
        return DataUtil.resolveHrefParams(href).get("isin");
    }

    public static OptionDto hrefToOption(String href) {
        Map<String, String> paramMap = DataUtil.resolveHrefParams(href);
        String isin = paramMap.get("isin");
        OptionDto option = new OptionDto(isin);
        option.setSqIsin(isin);
        if (paramMap.containsKey("stockExchange")) { option.setExchange(Exchange.ofCode(paramMap.get("stockExchange"))); }
        if (paramMap.containsKey("currency")) { option.setCurrency(Currency.valueOf(paramMap.get("currency"))); }
        return option;
    }

    // <a href="https://premium.swissquote.ch/sq_mi/market/Detail.action?s=US0000000000_67_USD">...</a>
    private static final String SQ_DETAIL_QUERY = "https://premium.swissquote.ch/sq_mi/market/Detail.action?s=";

    public static String encodeSqDetailHref(SqDetail sqDetail) {
        return SQ_DETAIL_QUERY + sqDetail.getIsin() + UL + sqDetail.getExchange().getCode() + UL + sqDetail.getCurrency().name();
    }

    public static SqDetail decodeSqDetailHref(String encodedHref) {
        var trimmed = encodedHref.replace(SQ_DETAIL_QUERY, EMPTY);
        var parts = trimmed.split(UL);
        return new SqDetail(
            parts[0],
            Exchange.ofCode(parts[1]),
            Currency.valueOf(parts[2])
        );
    }


}
