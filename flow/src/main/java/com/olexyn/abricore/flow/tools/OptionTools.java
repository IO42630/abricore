package com.olexyn.abricore.flow.tools;

import com.olexyn.abricore.model.runtime.assets.OptionBrace;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.util.enums.OptionStatus;
import lombok.experimental.UtilityClass;

import java.util.List;

import static com.olexyn.abricore.flow.strategy.StrategyUtil.isOptionSelectable;
import static com.olexyn.abricore.model.runtime.assets.OptionType.CALL;
import static com.olexyn.abricore.util.num.Num.ONE;

@UtilityClass
public class OptionTools {


    public static OptionBrace getOptionBrace(
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




    static long calcMinimalDistance(StrategyDto strategy, Series series) {
        return strategy.getMinOptionDistance().generate(series);
    }

}
