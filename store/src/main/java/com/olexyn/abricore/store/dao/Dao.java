package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.AbstractEntity;
import com.olexyn.abricore.model.runtime.AssetHolder;
import com.olexyn.abricore.model.runtime.Dto;
import com.olexyn.abricore.model.runtime.assets.DummyAssetDto;
import com.olexyn.abricore.store.Mapper;
import com.olexyn.abricore.store.runtime.AssetService;

import java.util.List;


/**
 * <b>Contract:</b><br>
 * - Dao returns/accepts only Dto. <br>
 * - Dto leave the Dao fully initialized. <br>
 */
public abstract class Dao<E extends AbstractEntity, D extends Dto<D>> extends SlimDao<E, D> {

    private final AssetService assetService;

    protected Dao(
        Mapper mapper,
        AssetService assetService
    ) {
        super(mapper);
        this.assetService = assetService;
    }

    public List<D> findDtos() {
        return findAll().stream()
            .map(this::toDto)
            .map(this::postProcess)
            .toList();
    }

    /**
     * The Dtos managed by the Service might need some post-processing.
     * E.g. Dto containing references to AssetDto which were first initialized with dummy Objects, <br>
     * and now can be fetched from AssetService.
     */
    protected D postProcess(D value) {
        if (
            value instanceof AssetHolder assetHolder
                && assetHolder.getAsset() instanceof DummyAssetDto
        ) {
            var existingAsset = assetService.ofName(assetHolder.getAsset().getName());
            assetHolder.setAsset(existingAsset);
        }
        return value;
    }

}
