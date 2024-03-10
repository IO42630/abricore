package com.olexyn.abricore.store.functions.condition;

import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.util.exception.MissingException;
import com.olexyn.abricore.util.exception.SoftCalcException;
import com.olexyn.abricore.util.log.LogU;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;


import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_BOL_TAIL_SIZE_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_BOL_TAIL_SIZE_BOL_TIMES;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_BOL_TAIL_SIZE_SIZE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_BOL_TAIL_SIZE_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_BOL_TAIL_SIZE_BOL_TIMES;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_BOL_TAIL_SIZE_SIZE;
import static com.olexyn.abricore.util.Constants.S0;
import static com.olexyn.abricore.util.enums.TransactionType.BUY;
import static com.olexyn.abricore.util.enums.TransactionType.SELL;
import static com.olexyn.abricore.util.num.NumUtil.toInt;



public class HasBolTailSize implements TransactionCondition, Serializable {

    @Serial
    private static final long serialVersionUID = -69827639204556502L;

    /**
     * Does TradedPrice go below/above bolTimes * Bolliger(barAmount) for tailSize amount of bars.
     */
    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        boolean isBuy = trade.getBuyPrice() == 0;
        int barAmount = toInt(vector.getValue(isBuy ? BUY_BOL_TAIL_SIZE_BARS : SELL_BOL_TAIL_SIZE_BARS));
        int tailSize = toInt(vector.getValue(isBuy ? BUY_BOL_TAIL_SIZE_SIZE : SELL_BOL_TAIL_SIZE_SIZE));
        long bolTimes = vector.getValue(isBuy ? BUY_BOL_TAIL_SIZE_BOL_TIMES : SELL_BOL_TAIL_SIZE_BOL_TIMES);
        try {
            var barDuration = Duration.ofSeconds(barAmount);
            for (int tailPos = 1; tailPos < tailSize; tailPos++) {
                var tailDuration = Duration.ofSeconds(tailPos);
                if (series.priceInBol(tailDuration, barDuration, bolTimes)) {
                    return false;
                }
            }
            return series.priceInBol(S0, barDuration, bolTimes);
        } catch (MissingException | SoftCalcException e) {
            return false;
        } catch (Exception e) {
            LogU.warnPlain("%s", e.getMessage());
            return false;
        }
    }

}
