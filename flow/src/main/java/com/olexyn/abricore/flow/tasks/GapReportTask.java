package com.olexyn.abricore.flow.tasks;

import com.olexyn.abricore.model.runtime.snapshots.SnapshotDistanceDto;
import com.olexyn.abricore.store.dao.SnapshotDistanceDao;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.enums.SnapshotDistanceType;
import com.olexyn.min.log.LogU;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;


/**
 * GapReportTask reads the table t_snapshot and creates a report of gaps.
 * If it finds a gap, it will create a Segment of  type GAP.
 * Finally it will user
 */
@Component
public class GapReportTask extends CtxAware implements Task {




    public GapReportTask(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    @Override
    public void run() {

        LogU.infoStart("GapReportTask");
        bean(AssetService.class).getUnderlyings().forEach(asset -> {
            var fullSeries = bean(SeriesService.class).ofFull(asset);

            assert fullSeries != null;
            var first = fullSeries.getFirstKey();
            assert first != null;
            var snd = fullSeries.getSnapshotDistance(first);


            Set<SnapshotDistanceDto> gaps = new HashSet<>();
            while (snd != null && snd.getEnd() != null) {
                if (snd.isLargerThan(Duration.ofMinutes(5))) {
                    snd.setSnapshotDistanceType(SnapshotDistanceType.GAP);
                    gaps.add(snd);
                }
                snd = snd.next();
            }

            bean(SnapshotDistanceDao.class).saveDtos(gaps);


        });
        LogU.infoEnd("GapReportTask");
    }



}
