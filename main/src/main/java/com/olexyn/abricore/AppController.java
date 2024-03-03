package com.olexyn.abricore;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.TaskType;
import com.olexyn.abricore.navi.sq.SqNavigator;
import com.olexyn.abricore.navi.tw.TwNavigator;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.util.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.olexyn.abricore.MainApp.ctx;

@Controller
public class AppController {

    private static final String INDEX= "index";
    private static final String ROOT= "redirect:/";

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("jobTypes", JobType.validValues());
        model.addAttribute("taskTypes", TaskType.validValues());
        return INDEX;
    }

    @PostMapping("/start-job")
    public String startJob(@RequestParam String jobType) throws InterruptedException {
        MainApp.start(JobType.of(jobType), List.of()); // Example call, adjust as necessary
        return ROOT;
    }

    @PostMapping("/end-job")
    public String endJob(@RequestParam String jobName, Model model) {
        // Logic to end a job based on jobName
        // Adapt this to your application's job management
        MainApp.cancelJob(jobName); // Example call, adjust as necessary
        return ROOT;
    }

    @PostMapping("/run-task")
    public String runTask(@RequestParam String taskStr) {
        MainApp.runTask(TaskType.of(taskStr), List.of()); // Example call, adjust as necessary
        return ROOT;
    }

    @PostMapping("/init-cache")
    public String postInitCache() {
        ctx.getBean(SeriesService.class).initCache();
        return ROOT;
    }

    @PostMapping("/patch")
    public String postPatch() {
        ctx.getBean(SeriesService.class).patch();
        return ROOT;
    }

    @GetMapping("/query-property")
    @ResponseBody
    public String queryProperty(@RequestParam String propertyName) throws IOException {
        return MainApp.describe(propertyName);
    }


    @PostMapping("/property")
    public String postProperty(
        @RequestParam String propName,
        @RequestParam String propValue
    ) {
        // Logic to end a job based on jobName
        // Adapt this to your application's job management
        Property.getEvents().setProperty(propName, propValue);
        return ROOT;
    }

    @PostMapping("/logout")
    public String logout() {
        MainUtil.logout(ctx);
        return ROOT;
    }

    @PostMapping("/exit")
    public String exit() throws InterruptedException {
        MainApp.exit();
        return INDEX;
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