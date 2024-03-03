package com.olexyn.abricore.flow.strategy.templates;

import com.olexyn.abricore.model.runtime.strategy.vector.BinaryParam;
import com.olexyn.abricore.model.runtime.strategy.vector.BoundParam;
import com.olexyn.abricore.model.runtime.strategy.vector.IntegerParam;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BOL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.BUY;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.DEPTH;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.LOSS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.MA;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.RADIUS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.RSI;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.SELL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.SIDE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.SIZE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.STOP;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.TAIL;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKeyWord.TIMES;
import static com.olexyn.abricore.util.num.Num.EN1;
import static com.olexyn.abricore.util.num.Num.FIVE;
import static com.olexyn.abricore.util.num.Num.HALF;
import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.Num.P10;
import static com.olexyn.abricore.util.num.Num.TEN;
import static com.olexyn.abricore.util.num.Num.TWO;
import static com.olexyn.abricore.util.num.Num.ZERO;
import static com.olexyn.abricore.util.num.NumUtil.fromInt;

@Component
public class VectorTemplates {

    /**
     * INIT the Boundaries and Precision for each Parameter of the Vector.
     * * Start with only FAVORABLE SIDE enabled.
     * * Assume that evolution will selectively enable the other conditions
     * <p>
     * a BUY SIDE ENABLED
     * b BUY TREND ENABLED
     * c BUY BOL TAIL SIZE ENABLED
     */
    public VectorDto fullVectorDef() {
        VectorDto vectorDef = new VectorDto();
        // FAVORABLE SIDE
        vectorDef.cloneParam(List.of(SIDE, BARS), new BoundParam(ZERO, fromInt(1000), fromInt(10)));

        for (var TYPE : List.of(BUY, SELL)) {
            // BOL TAIL SIZE
            vectorDef.cloneParam(List.of(TYPE, BOL, TAIL, SIZE, BARS), new BoundParam(ZERO, fromInt(200), fromInt(10)));
            vectorDef.cloneParam(List.of(TYPE, BOL, TAIL, SIZE, SIZE), new IntegerParam(ZERO, fromInt(200)));
            vectorDef.cloneParam(List.of(TYPE, BOL, TAIL, SIZE, BOL, TIMES), new BoundParam(ONE + P10, TWO + HALF, EN1));
            // TAIL DEPTH
            vectorDef.cloneParam(List.of(TYPE, TAIL, DEPTH, BARS), new BoundParam(fromInt(20), fromInt(200), fromInt(10)));
            vectorDef.cloneParam(List.of(TYPE, TAIL, DEPTH, SIZE), new IntegerParam(ONE, TEN));
            vectorDef.cloneParam(List.of(TYPE, TAIL, DEPTH, DEPTH), new BoundParam(ONE + P10, TWO + HALF, EN1));
            vectorDef.cloneParam(List.of(TYPE, TAIL, DEPTH, BOL, TIMES), new BoundParam(ONE + P10, TWO + HALF, EN1));
            // RSI RADIUS
            vectorDef.cloneParam(List.of(TYPE, RSI, SIDE), new BinaryParam().setValue(ZERO));
            vectorDef.cloneParam(List.of(TYPE, RSI, BARS), new IntegerParam(FIVE, fromInt(20)));
            vectorDef.cloneParam(List.of(TYPE, RSI, RADIUS), new BoundParam(ZERO, fromInt(40), FIVE));
        }
        // STOP LOSS AT BOL
        vectorDef.cloneParam(List.of(STOP, LOSS, BOL, BARS), new BoundParam(ZERO, fromInt(10000), fromInt(10)));
        vectorDef.cloneParam(List.of(STOP, LOSS, BOL, TIMES), new BoundParam(ONE + HALF, TWO + HALF, EN1));
        // STOP LOSS AT MA
        vectorDef.cloneParam(List.of(STOP, LOSS, MA, BARS), new BoundParam(ZERO, fromInt(10000), fromInt(10)));

        return vectorDef;
    }


}