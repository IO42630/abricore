package com.olexyn.abricore.flow.tools;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.OptionBrace;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.enums.OptionStatus;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

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

@Component
public class OptionTools extends CtxAware {


    @Autowired
    public OptionTools(ConfigurableApplicationContext ctx) {
        super(ctx);
        init(ctx.getBean(AssetService.class).ofName("XAGUSD"));
    }

    public OptionBrace getOptionBrace(
        AssetService assetService,
        StrategyDto strategy,
        Series underlyingSeries
    ) {
        long minDistance = calcMinimalDistance(strategy, underlyingSeries);
        var lastSnap = underlyingSeries.getLast();
        List<OptionDto> selectableOptions = assetService.getOptions(strategy.getUnderlying()).stream()
            .filter(option -> isOptionSelectable(lastSnap, option, minDistance))
            .filter(option -> option.getStatus() != OptionStatus.DEAD)
            .toList();
        OptionDto call = selectableOptions.stream()
            .filter(option -> option.getOptionType() == CALL)
            .sorted()
            .findFirst().orElse(null);
        OptionDto put = selectableOptions.stream()
            .filter(option -> option.getOptionType() == OptionType.PUT)
            .sorted()
            .findFirst().orElse(null);
        return new OptionBrace(call, put);
    }

    long MIN_STRIKE = ONE;
    long MAX_STRIKE = 50 * ONE;
    private static Map<AssetDto, List<OptionDto>> paperCallOptionMap = new HashMap<>();
    private static Map<AssetDto, List<OptionDto>> paperPutOptionMap = new HashMap<>();


    private void init(@Nullable AssetDto asset) {
        if (asset == null) {
            return;
        }
        if (!paperCallOptionMap.containsKey(asset)) {
            paperCallOptionMap.put(asset, new ArrayList<>());
        }
        if (!paperPutOptionMap.containsKey(asset)) {
            paperPutOptionMap.put(asset, new ArrayList<>());
        }
        var paperCallOptionsForUnderlying = paperCallOptionMap.get(asset);
        if (paperCallOptionsForUnderlying.isEmpty()) {
            for (long i = MIN_STRIKE; i < MAX_STRIKE; i = i + ONE) {
                paperCallOptionsForUnderlying.add(makePaperOption(asset, i, CALL));
            }
        }
        var paperPutOptionsForUnderlying = paperPutOptionMap.get(asset);
        if (paperPutOptionsForUnderlying.isEmpty()) {
            for (long i = MIN_STRIKE; i < MAX_STRIKE; i = i + ONE) {
                paperPutOptionsForUnderlying.add(makePaperOption(asset, i, PUT));
            }
        }
    }

    private OptionDto makePaperOption(AssetDto asset, long strike, OptionType type) {
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

    private long calcMinimalDistance(StrategyDto strategy, Series series) {
        return strategy.getMinOptionDistance().generate(series);
    }

    public OptionBrace getPaperOptionBrace(StrategyDto strategy, Series underlyingSeries) {
        long minDistance = calcMinimalDistance(strategy, underlyingSeries);
        var lastSnap = underlyingSeries.getLast();
        var call = paperCallOptionMap.get(strategy.getUnderlying()).stream()
            .filter(option -> isOptionSelectable(lastSnap, option, minDistance))
            .sorted()
            .findFirst().orElse(null);
        var put = paperPutOptionMap.get(strategy.getUnderlying()).stream()
            .filter(option -> isOptionSelectable(lastSnap, option, minDistance))
            .sorted()
            .findFirst().orElse(null);
        return new OptionBrace(call, put);
    }

}
