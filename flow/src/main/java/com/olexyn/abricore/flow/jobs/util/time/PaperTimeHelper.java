package com.olexyn.abricore.flow.jobs.util.time;

import com.olexyn.abricore.model.runtime.snapshots.FrameDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.store.dao.FrameDao;
import com.olexyn.abricore.store.runtime.PaperSeriesService;
import com.olexyn.abricore.util.DataUtil;
import com.olexyn.abricore.util.exception.MissingException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeMap;

import static com.olexyn.abricore.util.enums.FrameType.GAP;

@Scope("prototype")
@Component
public class PaperTimeHelper extends ProtoTimeHelper {

    private TreeMap<Instant, FrameDto> gaps = new TreeMap<>();
    private Series paperSeries;


    protected PaperTimeHelper(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    @Override
    public PaperTimeHelper init(StrategyDto strategy) {
        super.init(strategy);
        this.paperSeries = bean(PaperSeriesService.class).of(getAsset());
        bean(FrameDao.class).findAllByAssetAndFrameType(getAsset().getName(), GAP)
            .forEach(frame -> gaps.put(frame.getStart(), frame));
        return this;
    }


    @Override
    protected Instant getTodaysOpen() {
        var paperDate = paperDate();
        var paperDay = DayOfWeek.from(paperDate);
        return getAsset().getExchange().getOpen(paperDay)
            .atDate(paperDate).toInstant(getZoneOffset());
    }

    @Override
    protected Instant getTodaysClose() {
        var paperDate = paperDate();
        var paperDay = DayOfWeek.from(paperDate);
        return getAsset().getExchange().getClose(paperDay)
            .atDate(paperDate).toInstant(getZoneOffset());

    }

    private Instant getPreviousGapEnd() {
        var previousGapStart = gaps.lowerKey(now());
        var previousGap = gaps.get(previousGapStart);
        if (previousGap != null && previousGap.getEnd() != null) {
            return previousGap.getEnd();
        }
        return now();
    }

    private Instant getNextGapStart() {
        var nextGapStart = gaps.higherKey(now());
        return nextGapStart != null ? nextGapStart : Instant.MAX;
    }

    private boolean isStartBuyingTimeAfterGap() {
        return now().isAfter(getPreviousGapEnd().plus(START_BUYING_SECONDS));
    }

    private boolean isStopBuyingTimeBeforeGap() {
        return now().isAfter(getNextGapStart().minus(STOP_BUYING_SECONDS));
    }

    private boolean isForceSellTimeBeforeGap() {
        return now().isAfter(getNextGapStart().minus(FORCE_SELL_SECONDS));
    }

    private LocalDate paperDate() {
        return paperDateTime().toLocalDate();
    }

    private LocalDateTime paperDateTime() {
        return LocalDateTime.ofInstant(now(), getZoneOffset());
    }

    boolean isStartBuyingTimeAfterCycleStart() {
        return now().isAfter(DataUtil.getInstant("paper.trade.start").plus(START_BUYING_SECONDS));
    }

    boolean isSellForcedCycleEnd() {
        return now().isAfter(DataUtil.getInstant("paper.trade.end").minus(FORCE_SELL_SECONDS));
    }


    @Override
    public boolean isBuyAllowed() {
        return super.isBuyAllowed()
            && isStartBuyingTimeAfterCycleStart()
            && isStartBuyingTimeAfterGap()
            && !isStopBuyingTimeBeforeGap();
    }

    @Override
    public boolean isSellForced() {
        return super.isSellForced()
            || isSellForcedCycleEnd()
            || isForceSellTimeBeforeGap();
    }

    @Override
    protected Instant now() {
        var paperNow = paperSeries.getLastKey();
        if (paperNow == null) {
            throw new MissingException("PaperNow is null");
        }
        return paperNow;
    }


}