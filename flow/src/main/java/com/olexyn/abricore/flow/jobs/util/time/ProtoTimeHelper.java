package com.olexyn.abricore.flow.jobs.util.time;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.propconf.PropConf;
import lombok.Getter;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;


/**
 * TimeHelper is a helper class for time related calculations.
 * It ckecks 3 conditions:
 * BUY only if:
 * 1. THe market has benn open since >X minutes.
 * 2. The market has >X minutes before close.
 * FORCE SELL if:
 * 1. The market has <X minutes before close.
 */
@Getter
public abstract class ProtoTimeHelper extends CtxAware {

    protected static final Duration START_BUYING_SECONDS = PropConf.getDuration("trade.sq.time.offset.to.start.buying.seconds");
    protected static final Duration STOP_BUYING_SECONDS = PropConf.getDuration("trade.sq.time.offset.to.stop.buying.seconds");
    protected static final Duration FORCE_SELL_SECONDS = PropConf.getDuration("trade.sq.time.offset.to.force.sell.seconds");

    protected static final Clock CLOCK = Clock.systemDefaultZone();

    private AssetDto asset = null;


    protected ProtoTimeHelper(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    public ProtoTimeHelper init(StrategyDto strategy) {
        this.asset = strategy.getUnderlying();
        setUuid(strategy.getUuid());
        return this;
    }


    /**
     * Get the instant when the exchange (market) closes today.
     * If close time of the exchange is unknown,
     * or the exchange is closed today,
     * return 23:59.
     */
    protected abstract Instant getTodaysOpen();

    /**
     * Get the instant when the exchange (market) closes today.
     * If close time of the exchange is unknown,
     * or the exchange is closed today,
     * return 00:00.
     */
    protected abstract Instant getTodaysClose();

    private boolean isStartBuyingTimeAfterOpen() {
        return now().isAfter(getTodaysOpen().plus(START_BUYING_SECONDS));
    }



    private boolean isStopBuyingTimeBeforeClose() {
        return now().isAfter(getTodaysClose().minus(STOP_BUYING_SECONDS));
    }

    private boolean isForceSellTimeBeforeClose() {
        return now().isAfter(getTodaysClose().minus(FORCE_SELL_SECONDS));
    }

    public boolean isBuyAllowed() {
        return isStartBuyingTimeAfterOpen()
            && !isStopBuyingTimeBeforeClose();
    }

    public boolean isSellForced() {
        return isForceSellTimeBeforeClose();
    }


    protected abstract Instant now();


    public ZoneOffset getZoneOffset() {
        return CLOCK.getZone().getRules().getOffset(now());
    }





}
