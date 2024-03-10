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


import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_RSI_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_RSI_RADIUS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_RSI_SIDE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_RSI_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_RSI_RADIUS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_RSI_SIDE;
import static com.olexyn.abricore.util.num.Num.FIFTY;
import static com.olexyn.abricore.util.num.NumCalc.times;
import static com.olexyn.abricore.util.num.NumUtil.positive;
import static com.olexyn.abricore.util.num.NumUtil.toInt;

public class HasRsiRadius implements TransactionCondition, Serializable {

    @Serial
    private static final long serialVersionUID = -6673974227370187252L;

    /**
     * Does RSI leave the radius. <br>
     * DISABLED -> IGNORE_TRUE
     */
    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        boolean isBuy = trade.getBuyPrice() == 0;
        long side = vector.getValue(isBuy ? BUY_RSI_SIDE : SELL_RSI_SIDE);
        int barAmount = toInt(vector.getValue(isBuy ? BUY_RSI_BARS : SELL_RSI_BARS));
        var barDuration = Duration.ofSeconds(barAmount);
        long radius = vector.getValue(isBuy ? BUY_RSI_RADIUS : SELL_RSI_RADIUS);
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
