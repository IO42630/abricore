package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.SnapshotEntity;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import com.olexyn.abricore.store.Mapper;
import com.olexyn.abricore.store.repo.SnapshotRepo;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.min.log.LogU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.olexyn.min.log.LogPrint.PLAIN;
import static java.util.logging.Level.INFO;

@Service
public class SnapshotDao extends Dao<SnapshotEntity, SnapshotDto> {

    private final SnapshotRepo snapshotRepo;

    @Autowired
    public SnapshotDao(
        SnapshotRepo snapshotRepo,
        Mapper mapper,
        AssetService assetService
    ) {
        super(mapper, assetService);
        this.snapshotRepo = snapshotRepo;
    }

    @Override
    protected JpaRepository<SnapshotEntity, Long> getRepo() {
        return snapshotRepo;
    }

    public SnapshotDto find(String assetName, Instant instant) {
        var entity = snapshotRepo.findByAssetAndInstant(assetName, instant);
        var mapped = mapper.toSnapShotDto(entity);
        return postProcess(mapped);
    }

    /**
     * Find all E withing the same range as PARAM. Including a padding of 1 min.
     */
    public Set<SnapshotDto> findAll(List<SnapshotEntity> entities) {
        if (entities.isEmpty()) { return new HashSet<>(); }
        var assetName = entities.get(0).getAsset();
        entities.sort(Comparator.comparingLong(a -> a.getInstant().toEpochMilli()));
        var found = snapshotRepo.findAllByAssetAndInstantAfterAndInstantBefore(
            assetName,
            entities.get(0).getInstant().minus(Duration.ofMinutes(1)),
            entities.get(entities.size() - 1).getInstant().plus(Duration.ofMinutes(1))
        );
        return found.stream()
            .map(mapper::toSnapShotDto)
            .map(this::postProcess)
            .collect(Collectors.toSet());
    }

    public Stream<SnapshotDto> findAllByAsset(AssetDto asset) {
        return snapshotRepo.findAllByAsset(asset.getName()).stream()
            .map(mapper::toSnapShotDto)
            .peek(x -> x.setAsset(asset));
    }

    public Stream<SnapshotDto> getSegment(AssetDto asset, Instant from, Instant to) {
        var fromStr = SQL_TIME_FORMATTER.format(from);
        var toStr = SQL_TIME_FORMATTER.format(to);
        return snapshotRepo.findSegmentByAsset(asset.getName(), fromStr, toStr)
            .stream()
            .map(mapper::toSnapShotDto)
            .peek(x -> x.setAsset(asset));
    }

    public void save(Series series) {
        var snaps = series.getNavSet().stream()
            .map(series::getSnapshot)
            .filter(SnapshotDto::isTouched)
            .map(mapper::toSnapShotEntity)
            .collect(Collectors.toSet());
        if (series.isEmpty() || snaps.isEmpty()) { return; }
        LogU.log(INFO, PLAIN,
            "TOUCHED:  %-10d SERIES: %-10d %-10s",
            snaps.size(), series.size(), series.getAsset()
        );
        saveAll(new ArrayList<>(snaps));
    }

    @Override
    protected void saveAll(List<SnapshotEntity> entities) {
        var existingByInstant = findAll(entities).stream()
            .collect(Collectors.toMap(SnapshotDto::getInstant, (entity) -> entity));
        List<SnapshotEntity> saves = new ArrayList<>();
        long duplicateE = 0;
        for (var entity : entities) {
            if (existingByInstant.containsKey(entity.getInstant())) {
                duplicateE++;
            } else {
                saves.add(entity);
            }
        }
        LogU.log(INFO, PLAIN,
            "UNIQUE:   %-10d SKIP:   %-10d",
            saves.size(), duplicateE
        );
        super.saveAll(saves);
    }

    @Override
    public List<SnapshotDto> findDtos() {
        return snapshotRepo.findAll().stream()
            .map(mapper::toSnapShotDto)
            .map(this::postProcess)
            .toList();
    }

    @Override
    public void saveDtos(Set<SnapshotDto> dtos) {
        var entities = dtos.stream()
            .map(mapper::toSnapShotEntity)
            .toList();
        saveAll(entities);
    }

    @Override
    protected SnapshotDto toDto(SnapshotEntity entity) {
        return mapper.toSnapShotDto(entity);
    }

    @Override
    protected SnapshotEntity toEntity(SnapshotDto dto) {
        return mapper.toSnapShotEntity(dto);
    }

}
