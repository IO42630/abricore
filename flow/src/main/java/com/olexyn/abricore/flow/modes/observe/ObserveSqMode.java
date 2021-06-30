package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.sq.SqSession;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.options.BarrierOption;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ObserveSqMode extends Mode {

    private static final Logger LOGGER = LogUtil.get(ObserveSqMode.class);

    private final Asset underlyingAsset;
    private List<Asset> cdfList = new ArrayList<>();

    public ObserveSqMode(Asset underlyingAsset) {
        this.underlyingAsset = underlyingAsset;
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
        for (Asset cdf : cdfList) {
            SeriesService.save(SeriesService.of(cdf));
        }
        Session.doLogout();
    }

    /**
     * Fetch CDF data from SQ.
     */
    @Override
    public void fetchData() {
        synchronized (AssetService.class) {
            cdfList = AssetService.ASSETS.stream().filter(x -> x instanceof BarrierOption)
                .map(x -> (BarrierOption) x)
                .filter(x -> x.getUnderlying() == underlyingAsset)
                .collect(Collectors.toList());
        }
        List<AssetSnapshot> snapshots = new ArrayList<>();
        synchronized (Session.class) {
            for (Asset cdf : cdfList) {
                snapshots.add(SqNavigator.fetchQuote(cdf));
            }
        }
        synchronized (SeriesService.class) {
            SeriesService.putData(snapshots);
        }
    }

}
