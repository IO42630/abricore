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
import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;

import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_TAIL_DEPTH_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_TAIL_DEPTH_BOL_TIMES;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_TAIL_DEPTH_SIZE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_TAIL_DEPTH_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_TAIL_DEPTH_BOL_TIMES;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_TAIL_DEPTH_SIZE;
import static com.olexyn.abricore.util.num.NumUtil.toInt;

public class HasTailDepth implements TransactionCondition, Serializable {

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
        int barAmount = toInt(vector.getValue(isBuy ? BUY_TAIL_DEPTH_BARS : SELL_TAIL_DEPTH_BARS));
        var barDuration = Duration.ofSeconds(barAmount);
        int tailSize = toInt(vector.getValue(isBuy ? BUY_TAIL_DEPTH_SIZE : SELL_TAIL_DEPTH_SIZE));
        long tailDepth = vector.getValue(isBuy ? BUY_TAIL_DEPTH_BOL_TIMES : SELL_TAIL_DEPTH_BOL_TIMES);
        try {
            int side = -1;
            if ((!isCall && isBuy) || (isCall && !isBuy)) { side = 1; }

            boolean hasTailDepth = false;

            for (long offset = 1; offset < tailSize; offset++) {
                var offsetDuration = Duration.ofSeconds(offset);
                long lastTraded = series.getSnapshotBeforeOffset(offsetDuration).getTradePrice();
                long tail = series.bolRadius(offsetDuration, barDuration, tailDepth, Optional.empty());
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
