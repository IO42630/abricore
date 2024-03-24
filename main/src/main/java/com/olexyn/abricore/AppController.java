package com.olexyn.abricore;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.TaskType;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDistanceDto;
import com.olexyn.abricore.navi.sq.SqNavigator;
import com.olexyn.abricore.navi.tw.TwNavigator;
import com.olexyn.abricore.store.dao.EventDao;
import com.olexyn.abricore.store.dao.SnapshotDistanceDao;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.util.DataUtil;
import com.olexyn.abricore.util.enums.SnapshotDistanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.olexyn.abricore.MainApp.ctx;

@Controller
public class AppController {


    private final EventDao eventDao;
    private final SnapshotDistanceDao frameDao;

    private final List<SnapshotDistanceDto> gaps = new ArrayList<>();

    @Autowired
    public AppController(
        EventDao eventDao,
        SnapshotDistanceDao frameDao
    ) {
        this.eventDao = eventDao;
        this.frameDao = frameDao;
    }

    private static final String INDEX = "index";
    private static final String ROOT = "redirect:/";

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("jobTypes", JobType.validValues());
        model.addAttribute("taskTypes", TaskType.validValues());
        model.addAttribute("gaps", gaps);
        ;
        return INDEX;
    }


    @PostMapping("/set-evol-date")
    public String setEvolDate(@RequestParam String fromEvol, @RequestParam String toEvol, Model model) {
        var fromI = DataUtil.getInstantSoDay(DataUtil.parseDate(fromEvol));
        var toI = DataUtil.getInstantEoDay(DataUtil.parseDate(toEvol));
        gaps.clear();
        frameDao.findAllByAssetAndSnapshotDistanceType("XAGUSD", SnapshotDistanceType.GAP)
            .stream()
            .filter(snd -> snd.getEnd() != null)
            .filter(snd -> snd.getStart().isAfter(fromI))
            .filter(snd -> snd.getEnd().isBefore(toI))
            .filter(snd -> snd.isLargerThan(Duration.ofHours(1)))
            .forEach(gaps::add);

        //
        return ROOT;
    }

    @PostMapping("/start-job")
    public String startJob(@RequestParam String jobType) throws InterruptedException {
        MainApp.start(JobType.of(jobType), List.of());
        return ROOT;
    }

    @PostMapping("/stop-job")
    public String stopJob(@RequestParam String jobType) {
        MainApp.cancelJob(jobType);
        return ROOT;
    }

    @PostMapping("/run-task")
    public String runTask(@RequestParam String taskType) {
        MainApp.runTask(TaskType.of(taskType), List.of());
        return ROOT;
    }

    @PostMapping("/patch")
    public String postPatch() {
        ctx.getBean(SeriesService.class).patch();
        return ROOT;
    }




    @PostMapping("/property")
    public String postProperty(
        @RequestParam String propName,
        @RequestParam String propValue
    ) {
        eventDao.set(propName, propValue);
        return ROOT;
    }

    @PostMapping("/logout")
    public String logout() {
        MainUtil.logout(ctx);
        return ROOT;
    }

    @PostMapping("/exit")
    public String exit() throws InterruptedException {
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Delay to allow the client to receive the redirect response
                MainApp.exit();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }).start();
        return ROOT;
    }

    @PostMapping("/smoke-tw-nav")
    public String smokeTwNav() {
        ctx.getBean(TwNavigator.class).doLogin();
        return ROOT;
    }

    @PostMapping("/smoke-sq-nav")
    public String smokeSqNav() {
        ctx.getBean(SqNavigator.class).doLogin();
        return ROOT;
    }



}