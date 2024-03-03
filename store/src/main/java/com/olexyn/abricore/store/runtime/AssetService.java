package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.UnderlyingAssetDto;
import com.olexyn.abricore.store.dao.SymbolDao;
import com.olexyn.abricore.util.log.LogU;
import lombok.Synchronized;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AssetService implements IService {

    private static final Set<AssetDto> ASSETS = new HashSet<>();
    private final SymbolDao symbolDao;

    @Autowired
    public AssetService(SymbolDao symbolDao) {
        this.symbolDao = symbolDao;

        ASSETS.addAll(symbolDao.findAllUnderlying());

        var ulByName = getAssets().stream()
            .collect(Collectors.toMap(AssetDto::getName, (dto) -> dto));

        var options = symbolDao.findAllOptions().stream()
            .filter(option -> !option.getUnderlying().isComplete())
            .peek(option -> option.setUnderlying(ulByName.get(option.getUnderlying().getName())))
            .collect(Collectors.toSet());

        ASSETS.addAll(options);
    }

    @Synchronized
    public Set<String> getNames() {
        return getAssets().stream().map(AssetDto::getName)
            .collect(Collectors.toSet());
    }

    @Override
    @Synchronized
    public void save() {
        symbolDao.saveDtos(getAssets());
    }

    public Set<AssetDto> getAssets() {
        return ASSETS;
    }

    @Synchronized
    public @Nullable AssetDto ofName(String name) {
        return getAssets().stream()
            .filter(x -> Objects.nonNull(x.getName()))
            .filter(x -> x.getName().equals(name)).findAny().orElse(null);
    }

    @Synchronized
    public @Nullable AssetDto ofIsin(String isin) {
        return getAssets().stream()
            .filter(x -> Objects.nonNull(x.getSqIsin()))
            .filter(x -> x.getSqIsin().equals(isin))
            .findAny().orElse(null);
    }

    @Synchronized
    public AssetDto ofTwSymbol(String twSymbol) {
        return getAssets().stream()
            .filter(x -> Objects.nonNull(x.getTwSymbol()))
            .filter(x -> x.getTwSymbol().equals(twSymbol)).findAny().orElseThrow();
    }

    @Synchronized
    public Set<AssetDto> getUnderlyings() {
        return getAssets().stream()
            .filter(UnderlyingAssetDto.class::isInstance)
            .map(UnderlyingAssetDto.class::cast)
            .collect(Collectors.toSet());
    }

    @Synchronized
    public Set<OptionDto> getOptions(AssetDto underlyingAsset) {
        return getAssets().stream()
            .filter(OptionDto.class::isInstance)
            .map(OptionDto.class::cast)
            .filter(option -> option.getUnderlying().equals(underlyingAsset))
            .collect(Collectors.toSet());
    }

    @Synchronized
    public void addAsset(AssetDto asset) {
        if (asset.isComplete()) {
            getAssets().add(asset);
        } else {
            LogU.warnPlain("Asset %s has not been added because it is not complete.", asset.getName());
        }
    }

}
