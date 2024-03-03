package com.olexyn.abricore.model;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.UnderlyingAssetDto;
import org.junit.Before;

import java.time.Duration;
import java.time.Instant;

public class BoundParamTest {



    AssetDto asset = new UnderlyingAssetDto("TEST");
    Instant instant1 = Instant.now().minus(Duration.ofMillis(1000));

    @Before
    public void init() {

    }



}
