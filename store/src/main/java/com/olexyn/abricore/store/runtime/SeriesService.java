package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.store.dao.SnapshotDao;
import org.springframework.stereotype.Service;

/**
 * ONLY so we don't have to mess with @Qualifier.
 */
@Service
public class SeriesService extends ProtoSeriesService {

    public SeriesService(
        SnapshotDao snapshotDao,
        AssetService assetService,
        EventService eventService
    ) {
        super(snapshotDao, assetService, eventService);
    }

}
