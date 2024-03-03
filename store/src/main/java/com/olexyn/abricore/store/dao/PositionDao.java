package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.PositionEntity;
import com.olexyn.abricore.model.runtime.PositionDto;
import com.olexyn.abricore.store.Mapper;
import com.olexyn.abricore.store.repo.PositionRepo;
import com.olexyn.abricore.store.runtime.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionDao extends Dao<PositionEntity, PositionDto> {

    private final PositionRepo positionRepo;

    @Autowired
    public PositionDao(
        PositionRepo positionRepo,
        Mapper mapper,
        AssetService assetService
    ) {
        super(mapper, assetService);
        this.positionRepo = positionRepo;
    }

    @Override
    protected JpaRepository<PositionEntity, Long> getRepo() {
        return this.positionRepo;
    }

    @Override
    public List<PositionDto> findDtos() {
        return findAll().stream()
            .map(mapper::toPositionDto)
            .map(this::postProcess)
            .toList();
    }

    @Override
    protected PositionDto toDto(PositionEntity entity) {
        return mapper.toPositionDto(entity);
    }

    @Override
    protected PositionEntity toEntity(PositionDto dto) {
        return mapper.toPositionEntity(dto);
    }

}
