package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.SessionException;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.fingers.sq.SqSession;
import com.olexyn.abricore.flow.mission.Mission;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.options.BarrierOption;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.LogUtil;

import java.util.Comparator;
import java.util.logging.Logger;

public class ObserveSqMode extends Mode {

    private static final Logger LOGGER = LogUtil.get(ObserveSqMode.class);

    private final Mission mission;
    private Option tradableCdf = null;

    public ObserveSqMode(Mission mission) {
        this.mission = mission;
    }

    @Override
    public void run() {
        SqSession.doLogin();
        timer.start();
        while (timer.hasNotPassedSeconds("run.time.seconds")) {
            try {
                fetchData();
                timer.sleepMilli("sq.update.interval.milli");
            } catch (InterruptedException ignored) {}
        }
        synchronized (SeriesService.class) {
            SeriesService.save(SeriesService.of(tradableCdf));
        }
        Session.doLogout();
    }

    /**
     * Fetch CDF data from SQ.
     */
    @Override
    public void fetchData() throws InterruptedException {
        if (tradableCdf == null
            || !mission.getStrategy().isOptionSelectable(tradableCdf)
        ) {
            try {
                tradableCdf = determineTradableCdf();
            } catch (SessionException e) {
                LOGGER.warning("Can not determine Option to observe. Perhaps SyncCdfSqMode is not running.");
                Thread.sleep(1000L);
                return;
            }
        }

        AssetSnapshot snapshot;
        synchronized (Session.class) {
            snapshot = SqNavigator.fetchQuote(tradableCdf);
        }
        synchronized (SeriesService.class) {
            SeriesService.of(mission.getUnderlyingAsset()).put(snapshot);
        }
    }

    private Option determineTradableCdf() {
        Option result;
        synchronized (AssetService.class) {
            result = AssetService.ASSETS.stream()
                .filter(x -> x instanceof BarrierOption)
                .map(x -> (BarrierOption) x)
                .filter(x -> x.getUnderlying() == mission.getUnderlyingAsset())
                .filter(x -> mission.getStrategy().isOptionSelectable(x))
                .min(Comparator.comparing(Option::getStrike))
                .orElseThrow(SessionException::new);
        }
        return result;
    }

}
