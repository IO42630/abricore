package com.olexyn.abricore.flow.jobs;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public final class JobStarter {

    public static Job prepareJob(Job job, String... nameSuffix) {
        job.setThread(new Thread(job));
        job.getThread().setName(job.getType().name() + StringUtils.join(nameSuffix));
        return job;
    }

    public static Job startJob(Job job, String... nameSuffix) {
        return prepareJob(job, nameSuffix).start();
    }

}
