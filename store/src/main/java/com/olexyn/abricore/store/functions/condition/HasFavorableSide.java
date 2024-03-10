package com.olexyn.abricore.store.functions.condition;

import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorKey;
import com.olexyn.abricore.util.exception.MissingException;
import com.olexyn.abricore.util.exception.SoftCalcException;
import com.olexyn.abricore.util.log.LogU;

import java.time.Duration;


import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SIDE_BARS;
import static com.olexyn.abricore.util.Constants.S0;
import static com.olexyn.abricore.util.num.NumUtil.positive;
import static com.olexyn.abricore.util.num.NumUtil.toInt;

public class HasFavorableSide implements TransactionCondition {


    /**
     * Is the current price on the favorable side of the moving average?
     * If the trade is long, the price must be above the moving average.
     * If the trade is short, the price must be below the moving average.
     * Long means buying a call or selling(closing) a put.
     * Short means selling(closig) a call or buying a put.
     */
    @Override
    public boolean test(Series series, TradeDto trade, VectorDto vector) {
        OptionDto option = (OptionDto) trade.getAsset();
        boolean isCall = option.getOptionType() == OptionType.CALL;
        boolean isBuy = trade.getBuyPrice() == 0;
        int barAmount = toInt(vector.getValue(SIDE_BARS));
        var barDuration = Duration.ofSeconds(barAmount);
        try {
            long lastTraded = series.getLastTraded();
            if (lastTraded == 0) { return false; }
            long ma = series.ma(S0, barDuration);
            boolean isAbove = positive(lastTraded - ma);
            boolean isHighSide = (isCall && isAbove) || (!isCall && !isAbove);
            return (isBuy && !isHighSide) || (!isBuy && isHighSide);
        } catch (MissingException | SoftCalcException e) {
            return false;
        } catch (Exception e) {
            LogU.warnPlain("%s", e.getMessage());
            return false;
        }
    }
}
