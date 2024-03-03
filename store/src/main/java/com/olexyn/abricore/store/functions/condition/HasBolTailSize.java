package com.olexyn.abricore.store.functions.condition;

import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.util.exception.MissingException;
import com.olexyn.abricore.util.exception.SoftCalcException;
import com.olexyn.abricore.util.log.LogU;

import java.io.Serial;
import java.time.Duration;

import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BOL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BUY;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.SELL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.SIZE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.TAIL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.TIMES;
import static com.olexyn.abricore.util.Constants.S0;
import static com.olexyn.abricore.util.num.NumUtil.toInt;



public class HasBolTailSize implements TransactionCondition {

    @Serial
    private static final long serialVersionUID = -69827639204556502L;

    /**
     * Does TradedPrice go below/above bolTimes * Bolliger(barAmount) for tailSize amount of bars.
     */
    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        boolean isBuy = trade.getBuyPrice() == 0;
        var type = isBuy ? BUY : SELL;
        int barAmount = toInt(vector.getValue(type, BOL, TAIL, SIZE, BARS));
        int tailSize = toInt(vector.getValue(type, BOL, TAIL, SIZE, SIZE));
        long bolTimes = vector.getValue(type, BOL, TAIL, SIZE, BOL, TIMES);
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
