package com.olexyn.abricore.flow.strategy;

import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.util.enums.TransactionType;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

import static com.olexyn.abricore.model.runtime.assets.OptionType.CALL;
import static com.olexyn.abricore.model.runtime.assets.OptionType.PUT;
import static com.olexyn.abricore.util.enums.TransactionType.BUY;
import static com.olexyn.abricore.util.enums.TransactionType.SELL;
import static com.olexyn.abricore.util.num.NumCalc.times;

@UtilityClass
public class StrategyUtil {


    public static boolean isOptionSelectable(@Nullable SnapshotDto lastSnap, OptionDto option, long minDistance) {
        if (lastSnap == null) { return false; }
        long lastUnderlyingPrice = lastSnap.getTradePrice();
        long difference;
        if (option.getOptionType() == OptionType.CALL) {
            difference = lastUnderlyingPrice - option.getStrike();
        } else {
            difference = option.getStrike() - lastUnderlyingPrice;
        }
        return difference > minDistance;
    }

    public static Optional<TransactionCondition> resolveCondition(
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

    public static long getVolume(StrategyDto strategy) {
        return strategy.getTrades().stream()
            .map(trade -> times(trade.getBuyPrice(), trade.getAmount()))
            .mapToLong(x -> x   )
            .sum();
    }

}
