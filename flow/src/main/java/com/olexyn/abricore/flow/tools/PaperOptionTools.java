package com.olexyn.abricore.flow.tools;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.OptionBrace;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.util.enums.OptionStatus;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.olexyn.abricore.flow.strategy.StrategyUtil.isOptionSelectable;
import static com.olexyn.abricore.model.runtime.assets.OptionType.CALL;
import static com.olexyn.abricore.model.runtime.assets.OptionType.PUT;
import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.NumUtil.prettyStr;

@UtilityClass
public class PaperOptionTools {

    private final long MIN_STRIKE = ONE;
    private final long MAX_STRIKE = 50 * ONE;

    private static final Map<AssetDto, List<OptionDto>> PAPER_CALL_OPTION_MAP = new HashMap<>();
    private static final Map<AssetDto, List<OptionDto>> PAPER_PUT_OPTION_MAP = new HashMap<>();

    public static void init(@Nullable AssetDto asset) {
        if (asset == null) {
            return;
        }
        if (!PAPER_CALL_OPTION_MAP.containsKey(asset)) {
            PAPER_CALL_OPTION_MAP.put(asset, new ArrayList<>());
        }
        if (!PAPER_PUT_OPTION_MAP.containsKey(asset)) {
            PAPER_PUT_OPTION_MAP.put(asset, new ArrayList<>());
        }
        var paperCallOptionsForUnderlying = PAPER_CALL_OPTION_MAP.get(asset);
        if (paperCallOptionsForUnderlying.isEmpty()) {
            for (long i = MIN_STRIKE; i < MAX_STRIKE; i = i + ONE) {
                paperCallOptionsForUnderlying.add(makePaperOption(asset, i, CALL));
            }
        }
        var paperPutOptionsForUnderlying = PAPER_PUT_OPTION_MAP.get(asset);
        if (paperPutOptionsForUnderlying.isEmpty()) {
            for (long i = MIN_STRIKE; i < MAX_STRIKE; i = i + ONE) {
                paperPutOptionsForUnderlying.add(makePaperOption(asset, i, PUT));
            }
        }
    }

    private static OptionDto makePaperOption(AssetDto asset, long strike, OptionType type) {
        StringUtils.joinWith("_",
            "PAPER",
            type.name(),
            asset.getName(),
            prettyStr(strike, 2)
        );
        var paperCall = new OptionDto("PAPER_" + type.name() + '_' + UUID.randomUUID());
        paperCall.setUnderlying(asset);
        paperCall.setRatio(ONE);
        paperCall.setStrike(strike);
        paperCall.setOptionType(type);
        paperCall.setStatus(OptionStatus.KNOWN);
        return paperCall;
    }

    public static OptionBrace getPaperOptionBrace(StrategyDto strategy, Series underlyingSeries) {
        long minDistance = OptionTools.calcMinimalDistance(strategy, underlyingSeries);
        var lastSnap = underlyingSeries.getLast();
        var call = PAPER_CALL_OPTION_MAP.get(strategy.getUnderlying()).stream()
            .filter(option -> isOptionSelectable(lastSnap, option, minDistance))
            .sorted()
            .findFirst().orElse(null);
        var put = PAPER_PUT_OPTION_MAP.get(strategy.getUnderlying()).stream()
            .filter(option -> isOptionSelectable(lastSnap, option, minDistance))
            .sorted()
            .findFirst().orElse(null);
        return new OptionBrace(call, put);
    }

}
