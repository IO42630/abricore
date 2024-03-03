package com.olexyn.abricore.store.functions.condition;

import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.util.exception.MissingException;
import com.olexyn.abricore.util.exception.SoftCalcException;
import com.olexyn.abricore.util.log.LogU;

import java.time.Duration;

import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BOL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.LOSS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.STOP;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.TIMES;
import static com.olexyn.abricore.util.Constants.S0;
import static com.olexyn.abricore.util.Constants.S1;
import static com.olexyn.abricore.util.num.NumUtil.toInt;

public class StopLossAtBol implements TransactionCondition {

    /**
     * This is a SELL condition. If for whatever reason we are holding an asset, and it drops outside of bol, this acts as a STOP-LOSS.
     */
    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        try {
            OptionDto option = (OptionDto) trade.getAsset();
            boolean isCall = option.getOptionType() == OptionType.CALL;
            long bolTimes = vector.getValue(STOP, LOSS, BOL, TIMES);
            int barAmount = toInt(vector.getValue(STOP, LOSS, BOL, BARS));
            var barDuration = Duration.ofSeconds(barAmount);

            long currentMa = series.ma(S0, barDuration);
            if (series.getLast() == null) { return false; }
            boolean currentPriceAboveMa = series.getLast().getTradePrice() >= currentMa;

            boolean currentPriceInBol = series.priceInBol(S0, barDuration, bolTimes);
            boolean previousPriceInBol = series.priceInBol(S1, barDuration, bolTimes);
            boolean shouldSellCall = isCall && !currentPriceAboveMa && !currentPriceInBol && previousPriceInBol;
            boolean shouldSellPut = !isCall && currentPriceAboveMa && currentPriceInBol && !previousPriceInBol;
            return shouldSellCall || shouldSellPut;
        } catch (MissingException | SoftCalcException e) {
            return false;
        } catch (Exception e) {
            LogU.warnPlain("%s", e.getMessage());
            return false;
        }
    }

}
