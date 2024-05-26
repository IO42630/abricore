package com.olexyn.abricore.flow.jobs.sq;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.jobs.MainTradeBlock;
import com.olexyn.abricore.flow.jobs.TradeJob;
import com.olexyn.abricore.flow.jobs.util.time.ProtoTimeHelper;
import com.olexyn.abricore.flow.jobs.util.time.TimeHelper;
import com.olexyn.abricore.flow.tools.OptionTools;
import com.olexyn.abricore.model.runtime.AObserver;
import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.assets.OptionBrace;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.OptionType;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.navi.sq.SqNavigator;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.store.runtime.PositionService;
import com.olexyn.abricore.store.runtime.ProtoTradeService;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.store.runtime.TradeService;
import com.olexyn.abricore.util.UuidContext;
import com.olexyn.abricore.util.enums.TradeStatus;
import com.olexyn.abricore.util.enums.TransactionType;
import com.olexyn.abricore.util.exception.WebException;
import com.olexyn.min.log.LogU;
import com.olexyn.propconf.PropConf;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static com.olexyn.abricore.flow.JobType.OBS_TW;
import static com.olexyn.abricore.flow.strategy.StrategyUtil.resolveCondition;
import static com.olexyn.abricore.util.Constants.CHF;
import static com.olexyn.abricore.util.enums.TradeStatus.OPENING_POS;
import static com.olexyn.abricore.util.enums.TradeStatus.OPEN_PREPARED;
import static com.olexyn.abricore.util.enums.TransactionType.BUY;
import static com.olexyn.abricore.util.enums.TransactionType.SELL;
import static com.olexyn.abricore.util.num.NumCalc.div;
import static com.olexyn.abricore.util.num.NumCalc.times;

/**
 * There is a MAIN BLOCK. It consists of:
 * - run()
 * - fetchData()
 * - tryToPlaceOrders()
 *      - tryToPlaceBuyOrders()
 *          - placeBuyOrder()
 *      - tryToPlaceSellOrders()
 *          - placeSellOrder()
 *
 */
public class TradeSqJob extends TradeJob implements AObserver, MainTradeBlock {

    private static final Duration WAIT_BETWEEN_TRADES = PropConf.getDuration("trade.sq.wait.between.trades.seconds");

    private final PositionService positionService;
    private final AssetService assetService;
    private final SqNavigator sqNavigator;
    @Getter
    private final ProtoTradeService tradeService;

    @Getter
    @Setter
    private ProtoTimeHelper timeHelper;

    /**
     * TradeSqJob is a Job that trades a Strategy.
     * It is a FetchAware, so it has a FetchUtil.
     * It is an AObserver, so it observes a series of Snapshots.
     * It is a Runnable, so it can be run in a Thread.
     * It is a Job, so it has a JobType.
     * It is a SJob, so it has a Strategy.
     * It is a TradeSqJob, so it trades a Strategy.
     */
    public TradeSqJob(
        ConfigurableApplicationContext ctx,
        StrategyDto strategy
    ) {
        super(ctx, strategy);
        this.positionService = ctx.getBean(PositionService.class);
        this.assetService = ctx.getBean(AssetService.class);
        this.sqNavigator = ctx.getBean(SqNavigator.class);
        this.tradeService = ctx.getBean(UuidContext.class).getBean(TradeService.class, getUuid());
        setTimeHelper(bean(TimeHelper.class).init(strategy));
        setJobDependencyTypes(Set.of(OBS_TW));
    }

    // MAIN BLOCK : START

    @Override
    public void nestedRun() {
        synchronized(getLock()) {
            getObservedSeries().addObverser(this);
            getLock().notifyAll();
        }
        setReady(true);
        while (!shouldStart()) {
            synchronized(getLock()) {
                getLock().safeWait(PropConf.getDuration("trade.wait.for.dependencies.milli"));
            }
        }
        while (!shouldStop()) {
            synchronized(getLock()) {
                tryToPlaceOrders();
                getLock().safeWait(PropConf.getDuration("trade.wait.for.snapshot.milli"));
            }
        }
        removeThisObserver();
    }

    @Override
    public void tryToPlaceOrders() {
        try {
            tryToPlaceSellOrders();
            tryToPlaceBuyOrders();
        } catch (Exception e) {
            LogU.warnPlain("Exception in tryToPlaceOrders: %s", e.getMessage());
        }
    }

    @Override
    public final void tryToPlaceBuyOrders() {
        if (isWaitBetweenTradesNOK()) { return; }
        var optionBrace = getOptionBrace();
        for (var optionType : OptionType.values()) {
            if (optionBrace.get(optionType) == null) { return; }
            if (isSizeNOK()) { return; }
            if (!getTimeHelper().isBuyAllowed()) { return; }
            LogU.infoPlain("BUY - " + optionType.name() + " - Checking conditions.");
            var trade = new TradeDto(optionBrace.get(optionType));
            if (testCondition(BUY, optionType, trade)) {
                placeBuyOrder(trade);
            }
        }
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
    public void placeBuyOrder(TradeDto trade) {
        if (trade == null || trade.getAsset() == null) { return; }
        var snap = sqNavigator.fetchPreTradeScreenSnap(trade.getAsset());
        if (snap == null) { return; }
        long expectedPrice = snap.getAskPrice();
        trade.setBuyPrice(expectedPrice);
        trade.setAmount(div(getSize(), expectedPrice));
        trade.setStatus(OPEN_PREPARED);
        trade = sqNavigator.placeBuyOrder(trade);
        getTradeService().put(trade);
    }

    /**
     * Sell all Options of underlying Asset.
     */
    @Override
    public void placeSellOrder(TradeDto trade) {
        switch (trade.getStatus()) {
            case CLOSE_PREPARED:
            case CLOSE_ISSUED:
            case CLOSE_PENDING:
                // TODO maybe need to re-submit with lower limit.
                break;
            case CLOSE_EXECUTED:
                break;
            case OPEN_PREPARED:
            case OPEN_ISSUED:
            case OPEN_PENDING:
                // TODO cancel order
                break;
            case OPEN_EXECUTED:
                // SELL
                var snap = sqNavigator.fetchPreTradeScreenSnap(trade.getAsset());
                if (snap == null) { return; }
                trade.setSellPrice(getStrategy().getSellDistance().generate(getObservedSeries()));
                trade.setStatus(TradeStatus.CLOSE_PREPARED);
                trade = sqNavigator.placeSellOrder(trade);
                getTradeService().put(trade);
                break;
            default:
                throw new WebException();
        }
    }

    // MAIN BLOCK : END

    @Override
    public void cancel() { setCancelled(true); }

    private Instant getLastTradeBuyInstant() {
        var trades = getTradeService().getTrades();
        if (!trades.isEmpty()) {
            var lastBuyInstant = trades.lastEntry().getValue().getBuyInstant();
            return lastBuyInstant == null ? Instant.MIN : lastBuyInstant;
        }
        return Instant.MIN;
    }

    private boolean isWaitBetweenTradesNOK() {
        var lastTradeBuyInstant = getLastTradeBuyInstant();
        var last = getObservedSeries().getLast();
        if (last == null) { return true; }
        var lastUnderlyingInstant = last.getInstant();
        return lastTradeBuyInstant
            .plus(WAIT_BETWEEN_TRADES)
            .isAfter(lastUnderlyingInstant);
    }

    protected boolean testCondition(TransactionType txType, OptionType optionType, TradeDto trade) {
        var condition = resolveCondition(
                getStrategy(),
                txType,
                optionType
            ).orElse(null);
        if (condition == null) { return false; }
        return condition.test(getObservedSeries(), trade, getStrategy().getVector());
    }

    // OVERRIDE GETTERS

    protected long getCash() {
        var chfService = Optional.ofNullable(assetService.ofName(CHF));
        if (chfService.isEmpty()) { return 0; }
        var cashPosition = positionService.of(chfService.get());
        return cashPosition == null ? 0 : cashPosition.getAmount();
    }

    protected OptionBrace getOptionBrace() {
        return OptionTools.getOptionBrace(
                assetService,
                getStrategy(),
                getObservedSeries()
            );
    }

    @Override
    public Series getObservedSeries() {
        return bean(SeriesService.class).of(getUnderlying());
    }


    @Override
    public JobType getType() { return JobType.TRADE_SQ; }

    // COMMON LOGIC

    protected boolean hasAllDepsPresent() {
        var allDepsPresent = true;
        for (var depType : getJobDependencyTypes()) {
            var depTypePresent = getDependencies()
                .anyMatch(job -> job.getType() == depType && job.isReady());
            if (!depTypePresent) {
                allDepsPresent = false;
            }
        }
        return allDepsPresent;
    }

    protected boolean isUnderlyingNonEmpty() {
        return !getObservedSeries().isEmpty();
    }


    // PRIVATE LOGIC.

    private void removeThisObserver() {
        synchronized(getLock()) {
            getObservedSeries().removeObserver(this);
            getLock().notifyAll();
            LogU.infoPlain("Removing this observer.");
        }
    }

    protected boolean shouldStart() {
        var allDepsPresent = hasAllDepsPresent();
        var underlyingNonEmpty = isUnderlyingNonEmpty();
        if (!allDepsPresent) { LogU.infoPlain("Dep Jobs not Ready. WAIT."); }
        if (!underlyingNonEmpty) { LogU.infoPlain("Underlying empty. WAIT."); }
        return allDepsPresent && underlyingNonEmpty;
    }

    private boolean shouldStop() {
        if (isCancelled()) { return true; }
        if (!hasAliveDependencies()) {
            LogU.infoPlain("%s no ALIVE dependencies.", getType());
            setCancelled(true);
            return true;
        }
        var lastPaperSnap = getObservedSeries().getLast();
        if (lastPaperSnap == null) {
            LogU.warnPlain("%s likely EMPTY -> NOT STOPPING", getObservedSeries());
            return false;
        }
        Instant safeShutdownTime = getStrategy().getTo().minus(Duration.ofMinutes(3));
        if (lastPaperSnap.getInstant().isAfter(safeShutdownTime)) {
            LogU.infoPlain("%s   Safe shutdown time reached.", getUuid());
            setCancelled(true);
            return true;
        }
        return false;
    }

    protected final boolean isSizeNOK() {
        var currentSize = getTradeService().getLatentTrades()
            .map(trade -> {
                if (trade.getAmount() == 0) { return 0L; }
                if (OPENING_POS.contains(trade.getStatus()) && trade.getBuyPrice() != 0) {
                    return times(trade.getBuyPrice(), trade.getAmount());
                }
//                var pos = positionService.of(trade.getAsset());
//                return pos.getPrice().times(trade.getAmount());
                return 0L; // TODO FIXME AAA
            })
            .reduce(Long::sum).orElse(0L);
        boolean totalSizeExceeded = currentSize + getSize() > getAllocatedCapital();
        boolean cashMissing = getCash() < getSize();
        boolean isSizeNOK = totalSizeExceeded || cashMissing;
        if (isSizeNOK) {
            LogU.infoPlain("Size is NOK. No orders will be placed.");
        }
        return isSizeNOK;
    }

}
