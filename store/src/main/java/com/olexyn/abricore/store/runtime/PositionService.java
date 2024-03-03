package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.model.runtime.PositionDto;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.store.dao.PositionDao;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PositionService implements DtoService<PositionDto> {

    private static final Map<String, PositionDto> POSITION_MAP = new HashMap<>();

    private final PositionDao positionDao;

    @Autowired
    public PositionService(
        PositionDao positionDao
    ) {
        this.positionDao = positionDao;
        this.positionDao.findDtos()
            .forEach(dto -> POSITION_MAP.put(dto.getAsset().getName(), dto));
    }

    @Synchronized
    public PositionDto of(AssetDto asset) {
        return POSITION_MAP.get(asset.getName());
    }

    @Override
    @Synchronized
    public PositionService update(Set<PositionDto> dtos) {
        Set<PositionDto> toAdd = new HashSet<>();
        dtos.forEach(pos -> {
            var existingPos = of(pos.getAsset());
            if (existingPos == null) {
                toAdd.add(pos);
            } else {
                existingPos.mergeFrom(pos);
            }
        });
        toAdd.forEach(dto -> POSITION_MAP.put(dto.getAsset().getName(), dto));
        return this;
    }

    @Override
    @Synchronized
    public void save() {
        positionDao.saveDtos(Set.copyOf(POSITION_MAP.values()));
    }

    @Synchronized
    public Set<PositionDto> getByUnderlying(AssetDto asset) {
        return POSITION_MAP.values().stream().filter(dto -> {
            AssetDto tradedAsset = dto.getAsset();
            if (tradedAsset instanceof OptionDto tradedOption) {
                return tradedOption.getUnderlying().equals(asset);
            }
            return false;
        }).collect(Collectors.toSet());
    }

}
