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

import java.io.Serial;
import java.time.Duration;

import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BOL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BUY;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.DEPTH;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.SELL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.SIZE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.TAIL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.TIMES;
import static com.olexyn.abricore.util.num.NumUtil.toInt;

public class HasTailDepth implements TransactionCondition {

    @Serial
    private static final long serialVersionUID = 4746646717191593610L;

    /**
     * Does Tail have Bar where diff TradePrice & Bolliger > 1 + tailDepth?
     */
    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        boolean isBuy = trade.getBuyPrice() == 0;
        OptionDto option = (OptionDto) trade.getAsset();
        boolean isCall = option.getOptionType() == OptionType.CALL;
        var type = isBuy ? BUY : SELL;
        int barAmount = toInt(vector.getValue(type, TAIL, DEPTH, BARS));
        var barDuration = Duration.ofSeconds(barAmount);
        int tailSize = toInt(vector.getValue(type, TAIL, DEPTH, SIZE));
        long tailDepth = vector.getValue(type, TAIL, DEPTH, BOL, TIMES);
        try {
            int side = -1;
            if ((!isCall && isBuy) || (isCall && !isBuy)) { side = 1; }

            boolean hasTailDepth = false;

            for (long offset = 1; offset < tailSize; offset++) {
                var offsetDuration = Duration.ofSeconds(offset);
                long lastTraded = series.getSnapshotBeforeOffset(offsetDuration).getTradePrice();
                long tail = series.bolRadius(offsetDuration, barDuration, tailDepth);
                if (side < 0 && lastTraded > tail) { hasTailDepth = true; }
                if (side > 0 && lastTraded < tail) { hasTailDepth = true; }
            }
            return hasTailDepth;
        } catch (MissingException | SoftCalcException e) {
            return false;
        } catch (Exception e) {
            LogU.warnPlain("%s", e.getMessage());
            return false;
        }
    }

}
