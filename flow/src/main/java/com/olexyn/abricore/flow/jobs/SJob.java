package com.olexyn.abricore.flow.jobs;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.model.runtime.LockAware;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.util.Lock;
import com.olexyn.abricore.util.LockKeeper;
import lombok.Getter;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * SJob is a Job that is associated with a Strategy.
 * UUID is used to identify the Strategy.
 */
public class SJob extends Job implements LockAware {

    @Getter
    private final StrategyDto strategy;

    @Getter
    private final AssetDto underlying;


    private Set<JobType> jobDependencyTypes = new HashSet<>();

    protected SJob(
        ConfigurableApplicationContext ctx,
        StrategyDto strategy
    ) {
        super(ctx);
        this.strategy = strategy;
        this.underlying = strategy.getUnderlying();
        setUuid(strategy.getUuid());
    }

    @Override
    public UUID getUuid() {
        return getStrategy().getUuid();
    }

    public Set<JobType> getJobDependencyTypes() {
        return jobDependencyTypes;
    }

    public void setJobDependencyTypes(Set<JobType> jobDependencyTypes) {
        this.jobDependencyTypes = jobDependencyTypes;
    }

    public Stream<SJob> getDependencies() {
        return JobKeeper.streamJobsByUUIDAndType(getUuid(), jobDependencyTypes);
    }

    public Stream<SJob> getDeadDependencies() {
        return JobKeeper.streamDeadJobsByUUIDAndType(getUuid(), jobDependencyTypes);
    }

    public boolean hasDeadDependencies() {
        return getDeadDependencies().findFirst().isPresent();
    }

    @Override
    public final Lock getLock() {
        return LockKeeper.getLock(getUuid());
    }

}
