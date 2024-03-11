package com.olexyn.abricore.flow.tasks;

import com.olexyn.Pair;
import com.olexyn.abricore.model.runtime.strategy.vector.BoundParam;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.store.runtime.VectorService;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.log.LogU;
import com.olexyn.abricore.util.num.NumSerialize;
import com.olexyn.propconf.PropConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.Num.TWO;
import static com.olexyn.abricore.util.num.NumCalc.abs;
import static com.olexyn.abricore.util.num.NumCalc.div;



/**
 */
@Component
public class VectorMergeTask extends CtxAware implements Task {


    private static final long VECTOR_MERGE_FACTOR =
        NumSerialize.fromStr(PropConf.get("vector.merge.factor"));

    private final VectorService vectorService;

    @Autowired
    public VectorMergeTask(
        ConfigurableApplicationContext ctx,
        VectorService vectorService
    ) {
        super(ctx);
        this.vectorService = vectorService;
    }




    @Override
    public void run() {

        List<VectorDto> vectors = new ArrayList<>(vectorService.getVectors());
        var mergePair = merge(vectors);
        var merged = mergePair.getA();
        var deleted = mergePair.getB();
        LogU.infoPlain("MERGED:   %-6d ->  %-6d  (-%d)", vectors.size(), merged.size(), deleted.size());
        vectorService.save(merged);
        vectorService.delete(deleted);

        var surplus = limit(merged, 9000);
        LogU.infoPlain("LIMITED: to 9000 deleting  %-6d", surplus.size());
        vectorService.delete(surplus);

    }

    List<VectorDto> limit(List<VectorDto> input, int limit) {
        input.sort((v1, v2) -> Long.compare(v2.getRating(), v1.getRating()));
        return new ArrayList<>(input.subList(Math.min(limit, input.size()), input.size()));
    }

    Pair<List<VectorDto>> merge(List<VectorDto> originalVectors) {
        List<VectorDto> mergedL = new ArrayList<>(originalVectors);
        List<VectorDto> deletedL = new ArrayList<>();
        int lastRemoved = 0;
        boolean wasMerged = true;
        while (wasMerged) {
            wasMerged = false;
            for (int i = lastRemoved; i < mergedL.size(); i++) {
                for (int j = i + 1; j < mergedL.size(); j++) {
                    VectorDto a = mergedL.get(i);
                    VectorDto b = mergedL.get(j);

                    if (shouldMerge(a, b)) {
                        if (a.getId() != null) {
                            mergeToA(a, b);
                            mergedL.remove(j);
                            deletedL.add(b);
                        } else {
                            mergeToA(b, a);
                            mergedL.remove(i);
                            deletedL.add(a);
                        }
                        wasMerged = true;
                        lastRemoved = Math.min(i, mergedL.size() - 1);
                        break;
                    }
                }
                if (wasMerged) {
                    break;
                }
            }
        }
        return new Pair<>(mergedL, deletedL);
    }



    boolean shouldMerge(VectorDto a, VectorDto b) {
        if (a.equals(b)) { return false; }
        for (var key : a.getParamMap().keySet()) {
            var aBound = a.getParamMap().get(key);
            var bBound = b.getParamMap().get(key);
            if (!valueMatches(aBound, bBound)) { return false; }
        }
        return true;
    }

    boolean valueMatches(BoundParam aBound, BoundParam bBound) {
        boolean aZero = aBound.getValue() == 0;
        boolean bZero = bBound.getValue() == 0;
        if (aZero && bZero) { return true; }
        if (aZero || bZero) { return false; }
        long abRatio = div(aBound.getValue(), bBound.getValue());
        long baRatio = div(bBound.getValue(), aBound.getValue());
        long ratio = Math.max(abRatio, baRatio);
        return abs(ratio - ONE) < VECTOR_MERGE_FACTOR;
    }


    private void mergeToA(VectorDto aV, VectorDto bV) {

        var sampleSum = aV.getSampleCount() + bV.getSampleCount();

        // (10 x 10) && (100 x 1)
        // (10 * 10) + (100 * 1) / (10 + 1)
        // 200 / 11= 18.18
        var aRatingSum = aV.getRating() * aV.getSampleCount();
        var bRatingSum = bV.getRating() * bV.getSampleCount();
        aV.setRating((aRatingSum + bRatingSum) / sampleSum);

        var aDurationSum = aV.getAvgDuration() * aV.getSampleCount();
        var bDurationSum = bV.getAvgDuration() * bV.getSampleCount();
        aV.setAvgDuration((aDurationSum + bDurationSum) / sampleSum);

        var aSamples = Optional.ofNullable(aV.getSampleCount()).orElse(1L);
        var bSamples = Optional.ofNullable(bV.getSampleCount()).orElse(1L);
        aV.setSampleCount(aSamples + bSamples);
        for (var key : aV.getParamMap().keySet()) {
            var aBound = aV.getParamMap().get(key);
            var bBound = bV.getParamMap().get(key);
            var mergedBound = merge(aBound, bBound);
            aV.getParamMap().put(key, mergedBound);
        }
    }

    BoundParam merge(BoundParam a, BoundParam b) {
        var merged = new BoundParam(a.getLowerBound(), a.getUpperBound(), a.getPrecision());
        long sum = a.getValue() + b.getValue();
        merged.setValue(div(sum, TWO));
        return merged;
    }



}
