package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.FrameEntity;
import com.olexyn.abricore.model.runtime.snapshots.FrameDto;
import com.olexyn.abricore.store.Mapper;
import com.olexyn.abricore.store.repo.FrameRepo;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.util.enums.FrameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FrameDao extends Dao<FrameEntity, FrameDto> {

    private final FrameRepo frameRepo;

    @Autowired
    public FrameDao(
        FrameRepo frameRepo,
        Mapper mapper,
        AssetService assetService
    ) {
        super(mapper, assetService);
        this.frameRepo = frameRepo;
    }

    @Override
    protected JpaRepository<FrameEntity, Long> getRepo() {
        return frameRepo;
    }



    @Override
    public List<FrameDto> findDtos() {
        return frameRepo.findAll().stream()
            .map(mapper::toFrameDto)
            .map(this::postProcess)
            .toList();
    }

    public List<FrameDto> findAllByAssetAndFrameType(String asset, FrameType frameType) {
        return frameRepo.findAllByAssetAndFrameType(asset, frameType).stream()
            .map(mapper::toFrameDto)
            .map(this::postProcess)
            .toList();
    }

    @Override
    public void saveDtos(Set<FrameDto> dtos) {
        var entities = dtos.stream()
            .map(mapper::toFrameEntity)
            .toList();
        saveAll(entities);
    }

    @Override
    protected FrameDto toDto(FrameEntity entity) {
        return mapper.toFrameDto(entity);
    }

    @Override
    protected FrameEntity toEntity(FrameDto dto) {
        return mapper.toFrameEntity(dto);
    }

}
