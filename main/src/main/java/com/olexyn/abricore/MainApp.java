package com.olexyn.abricore;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.TaskType;
import com.olexyn.abricore.flow.jobs.JobKeeper;
import com.olexyn.abricore.flow.jobs.sq.ObservePositionsSqJob;
import com.olexyn.abricore.flow.jobs.sq.SyncOptionsSqJob;
import com.olexyn.abricore.flow.jobs.sq.TradeSqJob;
import com.olexyn.abricore.flow.jobs.store.FixNullValuesDbJob;
import com.olexyn.abricore.flow.jobs.store.ReadTmpCsvToDbJob;
import com.olexyn.abricore.flow.jobs.tw.DownloadTwJob;
import com.olexyn.abricore.flow.jobs.tw.ObserveTwJob;
import com.olexyn.abricore.flow.strategy.Evolution;
import com.olexyn.abricore.flow.strategy.templates.StrategyTemplates;
import com.olexyn.abricore.flow.tasks.GapReportTask;
import com.olexyn.abricore.flow.tasks.VectorMergeTask;
import com.olexyn.abricore.flow.tasks.VectorSaveTask;
import com.olexyn.abricore.flow.tools.PaperOptionTools;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.AssetType;
import com.olexyn.abricore.model.runtime.assets.UnderlyingAssetDto;
import com.olexyn.abricore.navi.TabDriverHolder;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.store.runtime.PositionService;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.store.runtime.TradeService;
import com.olexyn.abricore.store.runtime.VectorService;
import com.olexyn.abricore.util.MemState;
import com.olexyn.abricore.util.enums.CmdOptions;
import com.olexyn.min.log.LogU;
import com.olexyn.propconf.PropConf;
import com.olexyn.tabdriver.TabDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static com.olexyn.abricore.flow.JobType.EVOLVE;
import static com.olexyn.abricore.flow.jobs.JobStarter.startJob;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Main class of the application.
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MainApp {

    static {
        PropConf.load("config.properties");
    }

    public static ConfigurableApplicationContext ctx = SpringApplication.run(MainApp.class);

    private static AssetService assetService = ctx.getBean(AssetService.class);
    private static final Scanner INPUT = new Scanner(System.in, UTF_8);

    public static void main(String... args) {

        PaperOptionTools.init(ctx.getBean(AssetService.class).ofName("XAGUSD"));

        var x = new MemState();
        int br = 0;
    }


    public static void exit() throws InterruptedException {
        LogU.infoStart("((SHUTDOWN))");
        for (var job : ctx.getBean(JobKeeper.class).getJobs()) {
            cancelJob(job.getThread().getName());
        }
        while (!allJobsTerminated()) {
            Thread.sleep(1000L);
        }
        INPUT.close();


        ctx.getBean(TabDriverHolder.class)
            .getTdOpt().ifPresent(TabDriver::quit);


        assetService.save();
        ctx.getBean(SeriesService.class).save();
        ctx.getBean(TradeService.class).save();
        ctx.getBean(PositionService.class).save();
        ctx.getBean(VectorService.class).save();

        ctx.close();
        LogU.infoEnd("((SHUTDOWN))");
        System.exit(0);
    }

    static void start(JobType type, List<CmdOptions> cmdOptions) throws InterruptedException {
        var strategy = ctx.getBean(StrategyTemplates.class).dummy();
        switch (type) {
            case UNKNOWN -> LogU.infoPlain("Invalid JOB was ignored.");
            case DL_TW -> {
                var assetsToDownload = assetService.getAssets().stream()
                    .filter(assetDto -> assetDto.getAssetType() != AssetType.CASH)
                    .filter(UnderlyingAssetDto.class::isInstance)
                    .toList();
                startJob(new DownloadTwJob(ctx, assetsToDownload, cmdOptions));
            }
            case READ_TMP_CSV_TO_DB -> startJob(new ReadTmpCsvToDbJob(ctx));
            case OBS_POS_SQ -> {
                startJob(new SyncOptionsSqJob(ctx, strategy));
                startJob(new ObservePositionsSqJob(ctx));
            }
            case OBS_TW -> {
                var assetsToObserve = new ArrayList<AssetDto>();
                Optional.ofNullable(assetService.ofName("BTCUSD")).ifPresent(assetsToObserve::add);
                Optional.ofNullable(assetService.ofName("XAGUSD")).ifPresent(assetsToObserve::add);
                Optional.ofNullable(assetService.ofName("XAUUSD")).ifPresent(assetsToObserve::add);
                Optional.ofNullable(assetService.ofName("UBSG")).ifPresent(assetsToObserve::add);
                Optional.ofNullable(assetService.ofName("AMD")).ifPresent(assetsToObserve::add);
                Optional.ofNullable(assetService.ofName("PLTR")).ifPresent(assetsToObserve::add);
                Optional.ofNullable(assetService.ofName("TSLA")).ifPresent(assetsToObserve::add);
                var dummy = ctx.getBean(StrategyTemplates.class).dummy();
                startJob(new ObserveTwJob(ctx, dummy, assetsToObserve));
            }
            case SYNC_OPTIONS_SQ -> startJob(new SyncOptionsSqJob(ctx, strategy));
            case TRADE_SQ -> {
                strategy = ctx.getBean(StrategyTemplates.class).tradeSqTest();
                if (PropConf.isNot("trade.is.test")) {
                    strategy = ctx.getBean(StrategyTemplates.class).tradeSq();
                }
                startJob(new SyncOptionsSqJob(ctx, strategy));
                startJob(new ObservePositionsSqJob(ctx));
                startJob(new TradeSqJob(ctx, strategy));
                Thread.sleep(1000L);
                var assetsToObserve = List.of(strategy.getUnderlying());
                startJob(new ObserveTwJob(ctx, strategy, assetsToObserve));
            }
            case EVOLVE -> {
                Thread thread = new Thread(new Evolution(ctx));
                thread.setName(EVOLVE.name());
                thread.start();
                thread.join();
            }
            case FIX_NULL_VALUES_DB -> startJob(new FixNullValuesDbJob(ctx));
            default -> LogU.infoPlain("Invalid JOB was ignored.");
        }
    }

    static void runTask(TaskType type, List<CmdOptions> cmdOptions) {
        switch (type) {
            case GAP_REPORT -> ctx.getBean(GapReportTask.class).run();
            case VECTOR_MERGE -> ctx.getBean(VectorMergeTask.class).run();
            case VECTOR_SAVE -> ctx.getBean(VectorSaveTask.class).run();
            default -> LogU.infoPlain("Invalid JOB was ignored.");
        }
    }


    static void cancelJob(String target) {
        for (var job : ctx.getBean(JobKeeper.class).getJobs()) {
            String name = job.getThread().getName();
            if (name.equals(target)) {
                LogU.infoPlain("SENDING cancel to %s.", name);
                job.setCancelled(true);
            }
        }
    }

    private static boolean allJobsTerminated() {
        boolean jobsTerminated = true;
        for (var job : ctx.getBean(JobKeeper.class).getJobs()) {
            if (job.getThread().isAlive()) {
                jobsTerminated = false;
                break;
            }
        }
        return jobsTerminated;
    }

}
