package com.olexyn.abricore.model.runtime.assets;

import java.io.Serial;

/**
 * Exists for the sole purpose of being a placeholder, until the AssetHolder can resolve the real Asset during post-processing.
 */
public class DummyAssetDto extends AssetDto {

    @Serial
    private static final long serialVersionUID = 1907939325101686287L;

    public DummyAssetDto(String name) {
        super(name);
    }
}
