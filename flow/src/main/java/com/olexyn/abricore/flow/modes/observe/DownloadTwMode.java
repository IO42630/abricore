package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.datastore.StoreCsvService;
import com.olexyn.abricore.datastore.TmpCsvService;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.tw.TwFetch;
import com.olexyn.abricore.fingers.tw.TwSession;
import com.olexyn.abricore.flow.MainApp;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.util.Constants;
import com.olexyn.abricore.util.LogUtil;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadTwMode extends Mode {

    private static final Logger LOGGER = LogUtil.get(DownloadTwMode.class);

    private static final long INTERVAL_BETWEEN_DOWNLOADS = Long.parseLong(MainApp.config.getProperty("tw.download.interval.minutes"));
    private static final long TIMEFRAME_OF_DOWNLOAD = Long.parseLong(MainApp.config.getProperty("tw.download.timeframe.minutes"));

    private TwSession twSession;
    private TwFetch twFetch;

    private final List<Asset> assets = new ArrayList<>();

    public DownloadTwMode(List<Asset> assets) {
        this.assets.addAll(assets);
    }

    public void run() {
        start();
        timer.start();
        while (!timer.hasPassed(Duration.ofSeconds(Long.parseLong(MainApp.config.getProperty("run.time.seconds"))))) {
            try {
                Instant lastTwDownload = Instant.parse(MainApp.events.getProperty("tw.last.download"));
                if (lastTwDownload.plus(Duration.ofMinutes(INTERVAL_BETWEEN_DOWNLOADS)).isBefore(Instant.now())) {
                    fetchData();
                    MainApp.events.setProperty("tw.last.download", Instant.now().toString());
                    MainApp.saveProperties(MainApp.events, "events.properties");
                }
                Thread.sleep(Long.parseLong(MainApp.config.getProperty("tw.download.check.interval.seconds")) * Constants.SECONDS);
            } catch (InterruptedException | IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        stop();
    }

    @Override
    public void start() {
        twSession = new TwSession();
        twSession.doLogin();
        twFetch = new TwFetch();
    }

    @Override
    public void stop() {
        twSession.doLogout();
    }

    @Override
    public void fetchData() throws InterruptedException, IOException {
        synchronized (Session.class) {
            List<Interval> intervals = new ArrayList<>();
            intervals.add(Interval.S_1);
            LOGGER.info("STARTED downloading historical data.");
            twFetch.fetchHistoricalData(assets, intervals, TIMEFRAME_OF_DOWNLOAD);
            LOGGER.info("FINISHED downloading historical data.");
            Thread.sleep(5000L);
            TmpCsvService.parseTmpCsv();
            for (Asset asset : assets) {
                // TODO make sure it merges with runtime data.
                StoreCsvService.readFromStoreCsv(asset);
            }
        }
    }

}
