package com.olexyn.abricore.model;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.UnderlyingAssetDto;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static com.olexyn.abricore.util.num.Num.FOUR;
import static com.olexyn.abricore.util.num.Num.THREE;
import static org.junit.Assert.assertEquals;

public class SnapshotDtoTest {

    private final AssetDto asset = new UnderlyingAssetDto("TEST");
    private final Instant instant1 = Instant.now().minus(Duration.ofMillis(1000));

    @Before
    public void init() {

    }


    @Test
    public void priceTest() {
        SnapshotDto oldSnap = new SnapshotDto(asset);
        oldSnap.setTradePrice(THREE);
        oldSnap.setInstant(instant1);
        SnapshotDto newSnap = new SnapshotDto(asset);
        newSnap.setTradePrice(FOUR);
        newSnap.setInstant(instant1);
        newSnap.setAskPrice(FOUR);

        oldSnap = oldSnap.mergeFrom(newSnap);

        assertEquals(THREE, oldSnap.getTradePrice());
        assertEquals(FOUR, oldSnap.getAskPrice());

    }
}
