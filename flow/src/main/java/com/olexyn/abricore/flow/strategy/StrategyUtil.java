package com.olexyn.abricore.flow.strategy;

import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.enums.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.olexyn.abricore.model.runtime.assets.OptionType.CALL;
import static com.olexyn.abricore.model.runtime.assets.OptionType.PUT;
import static com.olexyn.abricore.util.enums.TransactionType.BUY;
import static com.olexyn.abricore.util.enums.TransactionType.SELL;

@Component
public class StrategyUtil extends CtxAware {

    @Autowired
    public StrategyUtil(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    public boolean isOptionSelectable(StrategyDto strategyDto, Series series, OptionDto option) {
        var lastSnap = series.getLast();
        if (lastSnap == null) { return false; }
        long lastUnderlyingPrice = lastSnap.getTradePrice();
        long difference;
        long distance = strategyDto.getMinOptionDistance().generate(series); // TODO calc this only once
        if (option.getOptionType() == OptionType.CALL) {
            difference = lastUnderlyingPrice - option.getStrike();
        } else {
            difference = option.getStrike() - lastUnderlyingPrice;
        }
        return difference > distance;
    }

    public void populateSeriesFromDb(StrategyDto strategy) {
        bean(SeriesService.class).of(
            strategy.getUnderlying(),
            strategy.getFrom(),
            strategy.getTo()
        );
    }

    public Optional<TransactionCondition> resolveCondition(
        StrategyDto strategy,
        TransactionType txType,
        OptionType optionType
    ) {
        TransactionCondition condition = null;
        if (txType == BUY && optionType == CALL) {
            condition = strategy.getCallBuyCondition();
        } else if (txType == BUY && optionType == PUT) {
            condition = strategy.getPutBuyCondition();
        } else if (txType == SELL && optionType == CALL) {
            condition = strategy.getCallSellCondition();
        } else if (txType == SELL && optionType == PUT) {
            condition = strategy.getPutSellCondition();
        }
        return Optional.ofNullable(condition);
    }

}
