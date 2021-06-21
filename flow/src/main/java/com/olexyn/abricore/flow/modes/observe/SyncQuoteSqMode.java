package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.fingers.sq.SqSession;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Commodity;
import com.olexyn.abricore.model.options.BarrierOption;
import com.olexyn.abricore.model.options.OptionType;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class SyncQuoteSqMode extends  ObserveMode {

    private SqSession sqSession;
    private SqNavigator sqNavigator;

    public SyncQuoteSqMode(Asset asset) {
        super(asset);
    }


    @Override
    public void run() throws InterruptedException {
        start();
        timer.start();
        while (timer.hasPassed(Duration.ofSeconds(10))) {
            fetchData();
            Thread.sleep(10L);
        }
        Series series = getCdfSeriesList().get(0);
        SeriesService.save(series);
        stop();
    }

    @Override
    public void start() {
        sqSession = new SqSession();
        sqNavigator = new SqNavigator(sqSession.doLogin());
    }

    @Override
    public void stop() {

    }

    @Override
    public void fetchData() throws InterruptedException {
        // TODO
        // for all assets, fetch cdf +- 1
        // remove obsolete cdf
        List<Asset> cdfList = getAssets().stream().filter(x -> x instanceof BarrierOption).collect(Collectors.toList());
        List<Asset> assetList = getAssets().stream().filter(x -> x instanceof Commodity).collect(Collectors.toList());
        sqNavigator.getCdf(assetList.get(0), OptionType.CALL, new ANum(23), new ANum(1), 1d, 1d);
        //
    }
}
