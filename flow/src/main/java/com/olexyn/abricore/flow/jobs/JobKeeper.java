package com.olexyn.abricore.flow.jobs;

import com.olexyn.abricore.flow.JobType;
import lombok.Synchronized;
import lombok.experimental.UtilityClass;

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
@UtilityClass
public final class JobKeeper {

    private static final Map<UUID, List<Job>> JOBS = new HashMap<>();

    /**
     * Keep this package private.
     */
    @Synchronized
    void addJob(Job job) {
        JOBS.computeIfAbsent(job.getUuid(), k -> new ArrayList<>());
        JOBS.get(job.getUuid()).add(job);
    }

    public List<Job> getJobs() {
        List<Job> jobs = new ArrayList<>();
        JOBS.values().forEach(jobs::addAll);
        return jobs;
    }

    // STREAM ==================================================================

    @Synchronized
    private Stream<Job> streamJobs(UUID uuid) {
        return new HashSet<>(JOBS.get(uuid)).stream();
    }

    public Stream<SJob> streamStrategyAwareJobs(UUID uuid) {
        return streamJobs(uuid)
            .filter(SJob.class::isInstance)
            .map(e -> (SJob) e);
    }

    private Stream<SJob> streamJobsByUUID(UUID uuid) {
        return streamStrategyAwareJobs(uuid)
            .filter(e -> e.getUuid().equals(uuid));
    }

    public Stream<SJob> streamJobsByUUIDAndType(UUID uuid, Set<JobType> jobTypes) {
        return streamJobsByUUID(uuid)
            .filter(e -> jobTypes.contains(e.getType()));
    }

    public Stream<SJob> streamDeadJobsByUUIDAndType(UUID uuid, Set<JobType> jobTypes) {
        return streamJobsByUUIDAndType(uuid, jobTypes)
            .filter(e -> !e.getThread().isAlive());
    }

}
