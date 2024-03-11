package com.olexyn.abricore.model;

import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import static com.olexyn.abricore.util.num.NumCalc.div;
import static com.olexyn.abricore.util.num.NumCalc.sqrt;
import static com.olexyn.abricore.util.num.NumCalc.square;
import static com.olexyn.abricore.util.num.NumUtil.fromInt;

public class OverRatioTest {



    @Test
    public void std() {


        int someSum10 = someSum(10);
        int someSum20 = someSum(20);
        int someSum30 = someSum(30);
        int someSum100 = someSum(100);
        int someSum200 = someSum(200);
        int someSum300 = someSum(300);
        int someSum400 = someSum(400);

        int br = 0;

    }


    public int someSum(int sampleSize) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add("");
        }

        var section = list.toArray(new String[0]);


        int overRatio = section.length / sampleSize;
        int safeOverRatio = overRatio == 0 ? 1 : overRatio;


        int sumSize = 0;
        for (int i = 0; i < section.length; i++) {
            if (i % safeOverRatio == 0) {
                sumSize++;
            }
        }
        return sumSize;

    }
}
