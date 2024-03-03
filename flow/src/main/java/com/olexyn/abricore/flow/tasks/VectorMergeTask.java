package com.olexyn.abricore.flow.tasks;

import com.olexyn.abricore.model.runtime.strategy.vector.BoundParam;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.store.dao.VectorDao;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.Property;
import com.olexyn.abricore.util.log.LogU;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.Num.TWO;
import static com.olexyn.abricore.util.num.NumCalc.abs;
import static com.olexyn.abricore.util.num.NumCalc.div;



/**
 * GapReportTask reads the table t_snapshot and creates a report of gaps.
 * If it finds a gap, it will create a Segment of  type GAP.
 * Finally it will user
 */
@Component
public class VectorMergeTask extends CtxAware implements Task {


    private static final long VECTOR_MERGE_FACTOR =
        NumSerialize.fromStr(PropConf.get("vector.merge.factor"));


    public VectorMergeTask(ConfigurableApplicationContext ctx) {
        super(ctx);
    }




    @Override
    public void run() {

        var vectors = new ArrayList<>(bean(VectorDao.class).findDtos());
        merge(vectors);

    }




    List<VectorDto> deletedmerge(List<VectorDto> vectors) {
        for (var a : vectors) {
            for (var b : vectors) {
                if (shouldMerge(a, b)) {
                    mergeToA(a, b);
                    vectors.remove(b);
                    return deletedmerge(vectors);
                }
            }
        }
        return vectors;
    }



    void merge(List<VectorDto> vectors) {
        bean(VectorDao.class).delete(vectors);
        LogU.infoPlain(" " + vectors.size());
        var trimmed = deletedmerge(vectors);
        LogU.infoPlain(" -> " + trimmed.size());
        bean(VectorDao.class).saveDtos(new HashSet<>(vectors));
    }


    boolean shouldMerge(VectorDto a, VectorDto b) {
        if (a.equals(b)) { return false; }
        for (var key : a.getParamMap().keySet()) {

            var aBound = a.getParamMap().get(key);
            var bBound = b.getParamMap().get(key);
            boolean aZero = aBound.getValue() == 0;
            boolean bZero = bBound.getValue() == 0;

            if (aZero && bZero) { continue; }

            if (aZero || bZero) { return false; }

            long ratio = div(aBound.getValue(), bBound.getValue());
            boolean valueMatches = abs(ratio - ONE) < VECTOR_MERGE_FACTOR;

            if (!valueMatches) { return false; }
        }


        return true;
    }


    private void mergeToA(VectorDto a, VectorDto b) {

        var sampleSum = a.getSampleCount() + b.getSampleCount();

        // (10 x 10) && (100 x 1)
        // (10 * 10) + (100 * 1) / (10 + 1)
        // 200 / 11= 18.18
        var aRatingSum = a.getRating() * a.getSampleCount();
        var bRatingSum = b.getRating() * b.getSampleCount();
        a.setRating((aRatingSum + bRatingSum) / sampleSum);

        var aDurationSum = a.getAvgDuration() * a.getSampleCount();
        var bDurationSum = b.getAvgDuration() * b.getSampleCount();
        a.setAvgDuration((aDurationSum + bDurationSum) / sampleSum);

        var aSamples = Optional.ofNullable(a.getSampleCount()).orElse(1L);
        var bSamples = Optional.ofNullable(b.getSampleCount()).orElse(1L);
        a.setSampleCount(aSamples + bSamples);
        for (var key : a.getParamMap().keySet()) {
            var aBound = a.getParamMap().get(key);
            var bBound = b.getParamMap().get(key);
            var mergedBound = merge(aBound, bBound);
            a.getParamMap().put(key, mergedBound);
        }
    }

    BoundParam merge(BoundParam a, BoundParam b) {
        var merged = new BoundParam(a.getLowerBound(), a.getUpperBound(), a.getPrecision());
        long sum = a.getValue() + b.getValue();
        merged.setValue(div(sum, TWO));
        return merged;
    }



}
