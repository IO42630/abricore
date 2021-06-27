package com.olexyn.abricore.flow.modes.observe;

import com.olexyn.abricore.datastore.StoreCsvService;
import com.olexyn.abricore.datastore.TmpCsvService;
import com.olexyn.abricore.fingers.tw.TwFetch;
import com.olexyn.abricore.fingers.tw.TwSession;
import com.olexyn.abricore.flow.Main;
import com.olexyn.abricore.flow.modes.Mode;
import com.olexyn.abricore.model.Asset;
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

    private static final long INTERVAL_BETWEEN_DOWNLOADS = Long.parseLong(Main.config.getProperty("tw.download.interval.minutes"));

    private TwSession twSession;
    private TwFetch twFetch;

    private final List<Asset> assets = new ArrayList<>();

    public DownloadTwMode(List<Asset> assets) {
        this.assets.addAll(assets);
    }

    public void run() {
        start();
        timer.start();
        while (!timer.hasPassed(Duration.ofSeconds(Long.parseLong(Main.config.getProperty("run.time"))))) {
            try {

                Instant lastTwDownload = Instant.parse(Main.events.getProperty("tw.last.download"));
                if (lastTwDownload.plus(Duration.ofMinutes(INTERVAL_BETWEEN_DOWNLOADS)).isBefore(Instant.now())) {
                    fetchData();
                    Main.events.setProperty("tw.last.download", Instant.now().toString());
                    Main.saveProperties(Main.events, "events.properties");
                }
                Thread.sleep(Long.parseLong(Main.config.getProperty("tw.download.check.interval")));
            } catch (InterruptedException | IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        stop();
    }

    @Override
    public void start() {
        twSession = new TwSession();
        twFetch = new TwFetch(twSession.doLogin());
    }

    @Override
    public void stop() {
        twSession.doLogout();
    }

    @Override
    public void fetchData() throws InterruptedException, IOException {
        twFetch.fetchHistoricalData(assets, INTERVAL_BETWEEN_DOWNLOADS);
        TmpCsvService.parseTmpCsv();
        for (Asset asset : assets) {
            // TODO make sure it merges with runtime data.
            StoreCsvService.readFromStoreCsv(asset);
        }
    }

}
