package com.olexyn.abricore.flow.tasks;

import com.olexyn.abricore.model.runtime.snapshots.FrameDto;
import com.olexyn.abricore.store.dao.FrameDao;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.enums.FrameType;
import com.olexyn.abricore.util.log.LogU;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
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
            var frame = fullSeries.getFrame(first);


            Set<FrameDto> gaps = new HashSet<>();
            while (frame != null && frame.getEnd() != null) {
                if (frame.getDuration().minus(Duration.ofSeconds(300)).isPositive()) {
                    frame.setFrameType(FrameType.GAP);
                    gaps.add(frame);
                }
                frame = frame.next();
            }

            bean(FrameDao.class).saveDtos(gaps);


        });
        LogU.infoEnd("GapReportTask");
    }



}
