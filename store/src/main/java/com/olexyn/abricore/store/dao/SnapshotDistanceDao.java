package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.SnapshotDistanceEntity;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDistanceDto;
import com.olexyn.abricore.store.Mapper;
import com.olexyn.abricore.store.repo.SnapshotDistanceRepo;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.util.enums.SnapshotDistanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SnapshotDistanceDao extends Dao<SnapshotDistanceEntity, SnapshotDistanceDto> {

    private final SnapshotDistanceRepo frameRepo;

    @Autowired
    public SnapshotDistanceDao(
        SnapshotDistanceRepo frameRepo,
        Mapper mapper,
        AssetService assetService
    ) {
        super(mapper, assetService);
        this.frameRepo = frameRepo;
    }

    @Override
    protected JpaRepository<SnapshotDistanceEntity, Long> getRepo() {
        return frameRepo;
    }



    @Override
    public List<SnapshotDistanceDto> findDtos() {
        return frameRepo.findAll().stream()
            .map(mapper::toSnapshotDistanceDto)
            .map(this::postProcess)
            .toList();
    }

    public List<SnapshotDistanceDto> findAllByAssetAndSnapshotDistanceType(String asset, SnapshotDistanceType frameType) {
        return frameRepo.findAllByAssetAndSnapshotDistanceType(asset, frameType).stream()
            .map(mapper::toSnapshotDistanceDto)
            .map(this::postProcess)
            .toList();
    }

    @Override
    public void saveDtos(Set<SnapshotDistanceDto> dtos) {
        var entities = dtos.stream()
            .map(mapper::toSnapshotDistanceEntity)
            .toList();
        saveAll(entities);
    }

    @Override
    protected SnapshotDistanceDto toDto(SnapshotDistanceEntity entity) {
        return mapper.toSnapshotDistanceDto(entity);
    }

    @Override
    protected SnapshotDistanceEntity toEntity(SnapshotDistanceDto dto) {
        return mapper.toSnapshotDistanceEntity(dto);
    }

}
