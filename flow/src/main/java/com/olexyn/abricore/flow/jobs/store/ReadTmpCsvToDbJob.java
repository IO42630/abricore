package com.olexyn.abricore.flow.jobs.store;

import com.olexyn.abricore.flow.JobType;
import com.olexyn.abricore.flow.jobs.Job;
import com.olexyn.abricore.store.csv.FileNameUtil;
import com.olexyn.abricore.store.csv.TmpCsvStore;
import com.olexyn.abricore.store.dao.SnapshotDao;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.util.enums.FlowHint;
import com.olexyn.abricore.util.log.LogU;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

import static com.olexyn.abricore.util.enums.FlowHint.OK;

public class ReadTmpCsvToDbJob extends Job {

    public ReadTmpCsvToDbJob(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    @Override
    public void nestedRun() {
        try {
            new TmpCsvStore(
                bean(SnapshotDao.class),
                bean(AssetService.class),
                bean(FileNameUtil.class)
            ).readTmpCsvToDb();
        } catch (IOException e) {
            LogU.warnPlain(e.getMessage());
        }
    }

    @Override
    public FlowHint fetchData() {
        // No Data needs to be fetched for this Job.
        return OK;
    }

    @Override
    public JobType getType() { return JobType.READ_TMP_CSV_TO_DB; }

}
