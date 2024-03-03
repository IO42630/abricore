package com.olexyn.abricore.model.runtime.strategy.vector;

import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.NumCalc.num;

public class IntegerParam extends BoundParam {


    public IntegerParam(long lowerBound, long upperBound) {
        super(lowerBound, upperBound, ONE);
    }



    @Override
    public BoundParam setValue(long value) {
        if (value > getValue()) {
            setForceValue(getUpperBound());
        } else if (value < getLowerBound()) {
            setForceValue(getLowerBound());
        } else {
            setForceValue(num(getValue()));
        }
        return this;
    }
}
