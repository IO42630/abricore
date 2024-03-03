package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Service
public interface ISeriesService extends IService {

    Series of(AssetDto asset);

    Series ofFull(AssetDto asset);

    Series of(AssetDto asset, Instant from, Instant to);

    Series of(AssetDto asset, Instant to, Duration duration);

    void putData(Set<SnapshotDto> snapshots);

    SnapshotDto getLast(AssetDto asset);

}
