package com.olexyn.abricore.model.runtime.strategy.vector;

import static com.olexyn.abricore.util.Constants.RNG;
import static com.olexyn.abricore.util.num.Num.EN1;
import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.Num.P50;
import static com.olexyn.abricore.util.num.Num.TWO;
import static com.olexyn.abricore.util.num.NumCalc.abs;
import static com.olexyn.abricore.util.num.NumCalc.round;
import static com.olexyn.abricore.util.num.NumCalc.times;


public class BinaryParam extends IntegerParam {

    public BinaryParam() {
        super(0, ONE);
    }

    @Override
    public BoundParam mutate(long impulse) {
        var mutant = new BinaryParam();
        long coin = round(RNG.nextLong(0, ONE), EN1);
        boolean doFlip = times(coin, TWO) < impulse;
        if (doFlip) {
            mutant.setValue(-getValue());
        } else {
            mutant.setValue(getValue());
        }
        // coiN    coin2    impulseN    doFlip
        // 0.1     0.2      0.2         true
        // 0.2     0.4      0.2         false
        // 0.4     0.8      0.2         false
        // 0.6     1.2      0.2         false
        // =================================
        // 0.1     0.2      0.5         true
        // 0.2     0.4      0.5         true
        // 0.4     0.8      0.5         false
        // 0.6     1.2      0.5         false
        // =================================
        // 0.1     0.2      1.0         true
        // 0.2     0.4      1.0         true
        // 0.4     0.8      1.0         true
        // 0.6     1.2      1.0         false
        return mutant;
    }

    @Override
    public BoundParam setValue(long value) {
        if (abs(value) > P50) {
            setForceValue(ONE);
        } else {
            setForceValue(0);
        }
        return this;
    }
}
