package com.olexyn.abricore.flow.jobs;

import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.snapshots.ProtoSeries;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.store.runtime.ProtoTradeService;
import com.olexyn.abricore.util.exception.MissingException;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.stream.Stream;

import static com.olexyn.abricore.util.enums.TradeStatus.LATENT_POS;

public class TradeJob extends SJob {

    protected TradeJob(ConfigurableApplicationContext ctx, StrategyDto strategy) {
        super(ctx, strategy);
    }


    public final Stream<TradeDto> getLatentTradesOfUnderlying() {
        return getTradesOfUnderlying()
            .filter(trade -> LATENT_POS.contains(trade.getStatus()));
    }

    public long getAllocatedCapital() {
        return getStrategy().getAllocatedCapital();
    }

    public long getSize() {
        return getStrategy().getSizingInCondition().apply(getAllocatedCapital());
    }


    public Stream<TradeDto> getTradesOfUnderlying() {
        if (getObservedSeries() == null) {
            return Stream.empty();
        }
        return getTradeService().getByUnderlying(getUnderlying());
    }

    /**
     * Return the Series that is observed by this Job.
     * If this is a PaperJob, the Series is the PaperSeries.
     */
    public ProtoSeries getObservedSeries() {
        throw new MissingException();
    }

    public ProtoTradeService getTradeService() {
        throw new MissingException();
    }

}
