package com.olexyn.abricore.model.runtime.strategy.vector;

import com.olexyn.abricore.util.exception.SoftCalcException;
import com.olexyn.min.log.LogU;
import lombok.Getter;

import java.util.Objects;

import static com.olexyn.abricore.util.Constants.RNG;
import static com.olexyn.abricore.util.num.Num.TWO;
import static com.olexyn.abricore.util.num.NumCalc.div;
import static com.olexyn.abricore.util.num.NumCalc.rng;
import static com.olexyn.abricore.util.num.NumCalc.round;
import static com.olexyn.abricore.util.num.NumCalc.times;
import static com.olexyn.abricore.util.num.NumSerialize.toStr;
import static com.olexyn.abricore.util.num.NumUtil.fromDouble;

/***
 * BoundParam is a parameter with a lower and upper bound.
 * It can be mutated and copied.
 * Bounds are included!
 *
 */
@Getter
public class BoundParam {

    private long value;
    private final long lowerBound;
    private final long upperBound;
    private final long precision;

    /**
     *
     */
    public BoundParam(long lowerBound, long upperBound, long precision) {
        if (lowerBound > upperBound) {
            LogU.warnPlain("lowerBound %s > upperBound %s", lowerBound, upperBound);
            throw new SoftCalcException();
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.precision = precision;
    }

    protected void setForceValue(long value) {
        this.value = value;
    }

    public BoundParam setValue(long value) {
        if (value > upperBound) {
            this.value = upperBound;
        } else if (value < lowerBound) {
            this.value = lowerBound;
        } else {
            this.value = round(value, precision);
        }
        return this;
    }

    public BoundParam copy() {
        BoundParam copy = new BoundParam(lowerBound, upperBound, precision);
        copy.setForceValue(getValue());
        return copy;
    }

    public BoundParam generateRng() {
        var rng = new BoundParam(lowerBound, upperBound, precision);
        long rngValue = rng(lowerBound, upperBound);
        rng.value = round(rngValue, precision);
        return rng;
    }

    public void normalizeToLowerBound() {
        setValue(lowerBound);
    }

    public long range() {
        return upperBound - lowerBound;
    }

    public BoundParam mutate(long impulse) {
        BoundParam mutant = new BoundParam(lowerBound, upperBound, precision);
        double gauss = RNG.nextGaussian();
        long gaussN = fromDouble(gauss); // -1 to +1
        long rangeGaussN = times(div(range(), TWO), impulse, gaussN);



        mutant.setValue(getValue() + rangeGaussN);
        // param         range/2    impulse    *impulse    gauss      *gaussN    +value
        // 500 [0,1000]  500        0          0           -0.2       0          500
        // 500 [0,1000]  500        0          0           +0.2       0          500
        // 500 [0,1000]  500        0.5        250         -0.2       -50        450
        // 500 [0,1000]  500        0.5        250         +0.2       +50        550
        // 500 [0,1000]  500        1.0        500         -0.2       -100       400
        // 500 [0,1000]  500        1.0        500         +0.2       +100       600
        // NOTE: if a value is close to abound, it might still be bumped towards said bump.
        // Thus, ~26% will be accumulated at lower/upper bound.
        return mutant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || !Objects.equals(getClass(), o.getClass())) { return false; }
        BoundParam that = (BoundParam) o;
        return value == that.value &&
            upperBound == that.upperBound &&
            lowerBound == that.lowerBound &&
            precision == that.precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, upperBound, lowerBound, precision);
    }

    @Override
    public String toString() {
        return toStr(value) +
            "  [ " + toStr(lowerBound) +
            " , " + toStr(upperBound) + " ]";
    }



}
