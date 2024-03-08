package com.olexyn.abricore.model;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import com.olexyn.abricore.util.exception.CalcException;
import com.olexyn.propconf.PropConf;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static com.olexyn.abricore.util.Constants.MS100;
import static com.olexyn.abricore.util.Constants.S0;
import static com.olexyn.abricore.util.Constants.S2;
import static com.olexyn.abricore.util.Constants.S3;
import static com.olexyn.abricore.util.Constants.S4;
import static com.olexyn.abricore.util.Constants.S5;
import static com.olexyn.abricore.util.num.Num.FOUR;
import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.Num.THREE;
import static com.olexyn.abricore.util.num.Num.TWO;
import static com.olexyn.abricore.util.num.NumSerialize.fromStr;
import static org.junit.Assert.assertEquals;


public class SeriesUtilTest {

    private AssetDto asset = null;
    private Series series = null;

    private static final Instant POINT_ZERO = Instant.MIN.plus(Duration.ofDays(9000));


    @Before
    public void init() {
        PropConf.load("config.properties");
        asset = new AssetDto("TEST") { };
        series = new Series(asset, 50);




        SnapshotDto snap = new SnapshotDto(asset);
        snap.setInstant(POINT_ZERO.plus(Duration.ofSeconds(1)));
        snap.setTradePrice(TWO);
        series.put(snap);

        snap = new SnapshotDto(asset);
        snap.setInstant(POINT_ZERO.plus(Duration.ofSeconds(2)));
        snap.setTradePrice(THREE);
        series.put(snap);

        snap = new SnapshotDto(asset);
        snap.setInstant(POINT_ZERO.plus(Duration.ofSeconds(3)));
        snap.setTradePrice(TWO);
        series.put(snap);

        snap = new SnapshotDto(asset);
        snap.setInstant(POINT_ZERO.plus(Duration.ofSeconds(4)));
        snap.setTradePrice(ONE);
        series.put(snap);

        snap = new SnapshotDto(asset);
        snap.setInstant(POINT_ZERO.plus(Duration.ofSeconds(5)));
        snap.setTradePrice(0);
        series.put(snap);

        snap = new SnapshotDto(asset);
        snap.setInstant(POINT_ZERO.plus(Duration.ofSeconds(6)));
        snap.setTradePrice(ONE);
        series.put(snap);

        snap = new SnapshotDto(asset);
        snap.setInstant(POINT_ZERO.plus(Duration.ofSeconds(7)));
        snap.setTradePrice(TWO);
        series.put(snap);

        snap = new SnapshotDto(asset);
        snap.setInstant(POINT_ZERO.plus(Duration.ofSeconds(8)));
        snap.setTradePrice(THREE);
        series.put(snap);
        snap = new SnapshotDto(asset);
        snap.setInstant(POINT_ZERO.plus(Duration.ofSeconds(9)));
        snap.setTradePrice(FOUR);
        series.put(snap);
        snap = new SnapshotDto(asset);
        snap.setInstant(POINT_ZERO.plus(Duration.ofSeconds(10)));
        snap.setTradePrice(TWO);
        series.put(snap);

        // 4         -
        // 3  -     -
        // 2 - -   -  -
        // 1    - -
        // 0     -
        // avg of all 10 -> 2.0
        // avg of last 5 -> 2.4
    }


    @Test
    public void sectionTest() {
        // 4         -
        // 3  -     -
        // 2 - -   -  x
        // 1    - -
        // 0     -
        assertEquals(series.getLastKey(), series.getSection(S0, S0).firstKey());
        // 4         -
        // 3  -     x
        // 2 - -   x  -
        // 1    - x
        // 0     -
        assertEquals(
            POINT_ZERO.plus(Duration.ofSeconds(6)),
            series.getSection(S2, S3.minus(MS100)).firstKey()
        );
        assertEquals(
            POINT_ZERO.plus(Duration.ofSeconds(8)),
            series.getSection(S2, S3.minus(MS100)).lastKey()
        );
        // 4         -
        // 3  -     x
        // 2 - -   x  -
        // 1    - x
        // 0     x
        assertEquals(
            POINT_ZERO.plus(Duration.ofSeconds(5)),
            series.getSection(S2, S4.minus(MS100)).firstKey()
        );
        assertEquals(
            POINT_ZERO.plus(Duration.ofSeconds(8)),
            series.getSection(S2, S4.minus(MS100)).lastKey()
        );
    }

    @Test
    public void maTest() throws CalcException {
        // 4         x
        // 3  -     x
        // 2 - -   x  x
        // 1    - -
        // 0     -
        assertEquals(fromStr("2.75"), series.ma(S0, S4.minus(MS100)));
        // 4         x
        // 3  -     x
        // 2 - -   x  x
        // 1    - x
        // 0     -
        assertEquals(fromStr("2.4"), series.ma(S0, S5.minus(MS100)));
    }


    @Test
    public void rsiTest() throws CalcException {
        // 4         x
        // 3  -     x
        // 2 - -   x  x
        // 1    - -
        // 0     -
        assertEquals(fromStr("2.75"), series.ma(S0, S4.minus(MS100)));
        // 4         x
        // 3  -     x
        // 2 - -   x  x
        // 1    - x
        // 0     -
        assertEquals(fromStr("2.4"), series.ma(S0, S5.minus(MS100)));
    }




}
