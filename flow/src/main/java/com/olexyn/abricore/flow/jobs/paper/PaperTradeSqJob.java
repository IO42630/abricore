package com.olexyn.abricore.flow.jobs.paper;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.jobs.sq.TradeSqJob;
import com.olexyn.abricore.flow.jobs.util.time.PaperTimeHelper;
import com.olexyn.abricore.flow.tools.OptionTools;
import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.OptionBrace;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.store.runtime.PaperSeriesService;
import com.olexyn.abricore.store.runtime.PaperTradeService;
import com.olexyn.abricore.store.runtime.ProtoTradeService;
import com.olexyn.abricore.util.enums.TradeStatus;
import com.olexyn.abricore.util.enums.TransactionType;
import com.olexyn.abricore.util.exception.WebException;
import com.olexyn.abricore.util.log.LogU;
import com.olexyn.abricore.util.num.NumSerialize;
import com.olexyn.propconf.PropConf;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.util.Set;

import static com.olexyn.abricore.flow.JobType.PAPER_OBS_TW;
import static com.olexyn.abricore.model.runtime.assets.OptionType.CALL;
import static com.olexyn.abricore.util.Formats.N_F;
import static com.olexyn.abricore.util.enums.TransactionType.BUY;
import static com.olexyn.abricore.util.enums.TransactionType.SELL;
import static com.olexyn.abricore.util.num.NumCalc.div;
import static com.olexyn.abricore.util.num.NumCalc.num;
import static com.olexyn.abricore.util.num.NumCalc.times;

public class PaperTradeSqJob extends TradeSqJob {

    private static final int MAX_BUY_GRID_COUNT = PropConf.getInt("evo.max.buy.grid.count");
    private static final int MAX_SELL_GRID_COUNT = PropConf.getInt("evo.max.sell.grid.count");

    private int buyGridCounter;
    private int sellGridCounter;


    private static final int TRADE_HOLD_TIME = 60;

    private static Long count = 0L;
    @Getter
    @Setter
    private long cash;

    public PaperTradeSqJob(
        ConfigurableApplicationContext ctx,
        StrategyDto strategy
    ) {
        super(ctx, strategy);
        this.cash = strategy.getAllocatedCapital();
        setTimeHelper(bean(PaperTimeHelper.class).init(strategy));
        setJobDependencyTypes(Set.of(PAPER_OBS_TW));
    }

    @Override
    public void tryToPlaceSellOrders() {
        getLatentTradesOfUnderlying()
            .forEach(trade -> {
                var option = (OptionDto) trade.getAsset();
                if (getTimeHelper().isSellForced() ||
                    testCondition(SELL, option.getOptionType(), trade)) {
                    placeSellOrder(trade);
                }
            });
    }

    @Override
    public void tryToPlaceOrders() {
        try {
            if (continueWithoutPlacingOrders(BUY)) { return; }
            tryToPlaceSellOrders();
            if (continueWithoutPlacingOrders(SELL)) { return; }
            tryToPlaceBuyOrders();
        } catch (Exception e) {
            LogU.warnPlain("Exception in tryToPlaceOrders: %s", e.getMessage());
        }
    }

    @Override
    public void placeBuyOrder(TradeDto trade) {
        OptionDto option = (OptionDto) trade.getAsset();
        if (option == null) { return; }
        var penalty = NumSerialize.fromStr(PropConf.get("paper.trade.sanity.price.penalty"));
        var expectedPrice = dummyPrice(getUnderlying(), option);
        var sanityPrice = expectedPrice + penalty;
        trade.setBuyPrice(sanityPrice);

        long allocatedCapital = getAllocatedCapital();
        long size = getStrategy().getSizingInCondition().apply(allocatedCapital);
        trade.setAmount(num(div(size, sanityPrice)));
        trade.setStatus(TradeStatus.OPEN_EXECUTED);
        var lastSnap = getObservedSeries().getLast();
        if (lastSnap == null) { return; }
        trade.setBuyInstant(lastSnap.getInstant());
        count++;
        trade.setBuyId(N_F.format(count));

        bean(PaperTradeService.class).put(trade);
        setCash(getCash() - size);
        buyGridCounter = MAX_BUY_GRID_COUNT;
    }

    @Override
    public void placeSellOrder(TradeDto trade) {
        switch (trade.getStatus()) {
            case CLOSE_EXECUTED:
                break;
            case OPEN_EXECUTED:
                OptionDto option = (OptionDto) trade.getAsset();
                long penalty = NumSerialize.fromStr(PropConf.get("paper.trade.sanity.price.penalty"));
                trade.setSellPrice(dummyPrice(getUnderlying(), option) - penalty);
                trade.setSellInstant(getObservedSeries().getLastKey());
                if (Duration.between(trade.getBuyInstant(), trade.getSellInstant()).getSeconds() < TRADE_HOLD_TIME) {
                    return;
                }
                count++;
                trade.setSellId(N_F.format(count));
                trade.setStatus(TradeStatus.CLOSE_EXECUTED);
                bean(PaperTradeService.class).put(trade);
                setCash(getCash() + times(trade.getAmount(), trade.getSellPrice()));
                sellGridCounter = MAX_SELL_GRID_COUNT;
                break;
            default:
                throw new WebException();
        }
    }

    // OVERRIDE GETTERS

    @Override
    protected OptionBrace getOptionBrace() {
        return bean(OptionTools.class)
            .getPaperOptionBrace(getStrategy(), getObservedSeries());
    }

    @Override
    public Series getObservedSeries() {
        return bean(PaperSeriesService.class).of(getUnderlying());
    }

    @Override
    public ProtoTradeService getTradeService() {
        return bean(PaperTradeService.class);
    }

    @Override
    public JobType getType() { return JobType.PAPER_TRADE_SQ; }

    // PRIVATE methods, which are used only by the PAPER.

    private long dummyPrice(AssetDto ul, OptionDto option) {
        long lastAssetTraded = bean(PaperSeriesService.class).getLastTraded(ul);
        if (option.getOptionType() == CALL) {
            return div(lastAssetTraded - option.getStrike(), option.getRatio());
        }
        return div(option.getStrike() - lastAssetTraded, option.getRatio());
    }

    private boolean continueWithoutPlacingOrders(TransactionType type) {
        if (type == SELL) {
            return --sellGridCounter > 0;
        }
        return --buyGridCounter > 0;
    }

}
