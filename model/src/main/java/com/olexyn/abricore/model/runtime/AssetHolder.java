package com.olexyn.abricore.model.runtime;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface AssetHolder {

    AssetDto getAsset();

    void setAsset(@Nullable AssetDto asset);

}
