package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.VectorEntity;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.store.Mapper;
import com.olexyn.abricore.store.repo.VectorRepo;
import com.olexyn.abricore.store.runtime.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorDao extends Dao<VectorEntity, VectorDto> {

    private final VectorRepo vectorRepo;

    @Autowired
    public VectorDao(
        VectorRepo vectorRepo,
        Mapper mapper,
        AssetService assetService
    ) {
        super(mapper, assetService);
        this.vectorRepo = vectorRepo;
    }

    @Override
    protected JpaRepository<VectorEntity, Long> getRepo() {
        return this.vectorRepo;
    }

    public List<VectorDto> findTop640ByRatingDesc() {
        return vectorRepo.findTop640ByOrderByRatingDesc().stream()
            .map(mapper::toVectorDto)
            .map(this::postProcess)
            .toList();
    }

    @Override
    protected VectorDto toDto(VectorEntity entity) {
        return mapper.toVectorDto(entity);
    }

    @Override
    protected VectorEntity toEntity(VectorDto dto) {
        return mapper.toVectorEntity(dto);
    }

}
