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
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BUY;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.RADIUS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.RSI;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.SELL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.SIDE;
import static com.olexyn.abricore.util.num.Num.FIFTY;
import static com.olexyn.abricore.util.num.NumCalc.times;
import static com.olexyn.abricore.util.num.NumUtil.positive;
import static com.olexyn.abricore.util.num.NumUtil.toInt;

public class HasRsiRadius implements TransactionCondition {

    @Serial
    private static final long serialVersionUID = -6673974227370187252L;

    /**
     * Does RSI leave the radius. <br>
     * DISABLED -> IGNORE_TRUE
     */
    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        boolean isBuy = trade.getBuyPrice() == 0;
        var type = isBuy ? BUY : SELL;
        long side = vector.getValue(type, RSI, SIDE);
        int barAmount = toInt(vector.getValue(type, RSI, BARS));
        var barDuration = Duration.ofSeconds(barAmount);
        long radius = vector.getValue(type, RSI, RADIUS);
        try {
            boolean upper = positive(side);
            long rsi = series.rsi(barDuration);
            long line = FIFTY + times(side, radius);
            return ((upper && rsi > line)
                || (!upper && rsi < line));
        } catch (MissingException | SoftCalcException e) {
            return false;
        } catch (Exception e) {
            LogU.warnPlain("%s", e.getMessage());
            return false;
        }
    }

}
