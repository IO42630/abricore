package com.olexyn.abricore.store.functions.condition;

import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.util.exception.MissingException;
import com.olexyn.abricore.util.exception.SoftCalcException;
import com.olexyn.min.log.LogU;

import java.time.Duration;

import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.STOP_LOSS_MA_BARS;
import static com.olexyn.abricore.util.Constants.S0;
import static com.olexyn.abricore.util.Constants.S1;
import static com.olexyn.abricore.util.num.NumUtil.toInt;

public class StopLossAtMa implements TransactionCondition {


    /**
     * This is a SELL condition. If for whatever reason we are holding an asset, and it drops below ma, this acts as a STOP-LOSS.
     */
    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        try {
            OptionDto option = (OptionDto) trade.getAsset();
            boolean isCall = option.getOptionType() == OptionType.CALL;
            int barAmount = toInt(vector.getValue(STOP_LOSS_MA_BARS));
            var barDuration = Duration.ofSeconds(barAmount);
            var currentSnap = series.getLast();
            if (currentSnap == null) { return false; }
            var previousSnap = series.lower(currentSnap);
            long currentMa = series.ma(S0, barDuration);
            long previousMa = series.ma(S1, barDuration);
            boolean currentPriceAboveMa = currentSnap.getTradePrice() > currentMa;
            boolean previousPriceAboveMa = previousSnap.getTradePrice() > previousMa;
            boolean shouldSellCall = isCall && !currentPriceAboveMa && previousPriceAboveMa;
            boolean shouldSellPut = !isCall && currentPriceAboveMa && !previousPriceAboveMa;
            return shouldSellCall || shouldSellPut;
        } catch (MissingException | SoftCalcException e) {
            return false;
        } catch (Exception e) {
            LogU.warnPlain("%s", e.getMessage());
            return false;
        }
    }

}
