package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.SymbolEntity;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.UnderlyingAssetDto;
import com.olexyn.abricore.store.Mapper;
import com.olexyn.abricore.store.repo.SymbolRepo;
import com.olexyn.min.log.LogU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SymbolDao extends SlimDao<SymbolEntity, AssetDto> {

    private final SymbolRepo symbolRepo;

    @Autowired
    public SymbolDao(
        SymbolRepo positionRepo,
        Mapper mapper
    ) {
        super(mapper);
        this.symbolRepo = positionRepo;
    }

    @Override
    protected JpaRepository<SymbolEntity, Long> getRepo() {
        return this.symbolRepo;
    }

    @Transactional(readOnly = true)
    public Set<UnderlyingAssetDto> findAllUnderlying() {
        var result = symbolRepo.findAll().stream()
            .map(mapper::toAssetDto)
            .filter(UnderlyingAssetDto.class::isInstance)
            .map(UnderlyingAssetDto.class::cast)
            .collect(Collectors.toSet());
        LogU.load("%-6d (%s)", result.size(), classPrint(result));
        return result;
    }

    @Transactional(readOnly = true)
    public Set<OptionDto> findAllOptions() {
        var result = symbolRepo.findAll().stream()
            .map(mapper::toAssetDto)
            .filter(OptionDto.class::isInstance)
            .map(OptionDto.class::cast)
            .collect(Collectors.toSet());
        LogU.load("%-6d (%s)", result.size(), classPrint(result));
        return result;
    }


    public List<AssetDto> findDtos() {
        return symbolRepo.findAll().stream()
            .map(mapper::toAssetDto)
            .toList();
    }

    @Override
    protected AssetDto toDto(SymbolEntity entity) {
        return mapper.toAssetDto(entity);
    }

    @Override
    protected SymbolEntity toEntity(AssetDto dto) {
        return mapper.toSymbolEntity(dto);
    }

}
