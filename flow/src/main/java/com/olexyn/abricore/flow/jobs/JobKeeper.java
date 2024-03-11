package com.olexyn.abricore.flow.jobs;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.util.CtxAware;
import lombok.Synchronized;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Keep track of all Jobs.
 */
@Service
public final class JobKeeper extends CtxAware {


    @Autowired
    public JobKeeper(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    private static final Map<UUID, Set<Job>> JOBS = new HashMap<>();

    /**
     * Keep this package private.
     */
    @Synchronized
    public void addJob(Job job) {
        JOBS.computeIfAbsent(job.getUuid(), k -> new HashSet<>());
        JOBS.get(job.getUuid()).add(job);
    }

    public List<Job> getJobs() {
        List<Job> jobs = new ArrayList<>();
        JOBS.values().forEach(jobs::addAll);
        return jobs;
    }

    @Synchronized
    public void removeJob(Job job) {
        JOBS.get(job.getUuid()).remove(job);
    }


    // STREAM ==================================================================

    @Synchronized
    private Stream<Job> streamJobs(UUID uuid) {
        return new HashSet<>(JOBS.get(uuid)).parallelStream();
    }

    public Stream<SJob> streamJobsByUUIDAndType(UUID uuid, Set<JobType> jobTypes) {
        return streamJobs(uuid)
            .filter(SJob.class::isInstance)
            .filter(e -> e.getUuid().equals(uuid))
            .filter(e -> jobTypes.contains(e.getType()))
            .map(SJob.class::cast);
    }


    public Stream<SJob> streamAliveJobsByUUIDAndType(UUID uuid, Set<JobType> jobTypes) {
        return streamJobs(uuid)
            .filter(SJob.class::isInstance)
            .filter(e -> e.getUuid().equals(uuid))
            .filter(e -> jobTypes.contains(e.getType()))
            .filter(e -> e.getThread().isAlive())
            .map(SJob.class::cast);
    }

}
