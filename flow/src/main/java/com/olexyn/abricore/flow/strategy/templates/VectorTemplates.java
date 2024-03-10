package com.olexyn.abricore.flow.strategy.templates;

import com.olexyn.abricore.model.runtime.strategy.vector.BinaryParam;
import com.olexyn.abricore.model.runtime.strategy.vector.BoundParam;
import com.olexyn.abricore.model.runtime.strategy.vector.IntegerParam;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorKey;
import org.springframework.stereotype.Component;

import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_BOL_TAIL_SIZE_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_BOL_TAIL_SIZE_BOL_TIMES;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_BOL_TAIL_SIZE_SIZE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_RSI_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_RSI_RADIUS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_RSI_SIDE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_TAIL_DEPTH_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_TAIL_DEPTH_BOL_TIMES;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.BUY_TAIL_DEPTH_SIZE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_BOL_TAIL_SIZE_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_BOL_TAIL_SIZE_BOL_TIMES;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_BOL_TAIL_SIZE_SIZE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_RSI_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_RSI_RADIUS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_RSI_SIDE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_TAIL_DEPTH_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_TAIL_DEPTH_BOL_TIMES;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.SELL_TAIL_DEPTH_SIZE;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.STOP_LOSS_BOL_BARS;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.STOP_LOSS_BOL_TIMES;
import static com.olexyn.abricore.model.runtime.strategy.vector.VectorKey.STOP_LOSS_MA_BARS;
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
        vectorDef.cloneParam(VectorKey.SIDE_BARS, new BoundParam(ZERO, fromInt(1000), fromInt(10)));


        // BUY BOL TAIL SIZE
        vectorDef.cloneParam(BUY_BOL_TAIL_SIZE_BARS, new BoundParam(ZERO, fromInt(200), fromInt(10)));
        vectorDef.cloneParam(BUY_BOL_TAIL_SIZE_SIZE, new IntegerParam(ZERO, fromInt(200)));
        vectorDef.cloneParam(BUY_BOL_TAIL_SIZE_BOL_TIMES, new BoundParam(ONE + P10, TWO + HALF, EN1));
        // BUY TAIL DEPTH
        vectorDef.cloneParam(BUY_TAIL_DEPTH_BARS, new BoundParam(fromInt(20), fromInt(200), fromInt(10)));
        vectorDef.cloneParam(BUY_TAIL_DEPTH_SIZE, new IntegerParam(ONE, TEN));
        vectorDef.cloneParam(BUY_TAIL_DEPTH_BOL_TIMES, new BoundParam(ONE + P10, TWO + HALF, EN1));
        // BUY RSI RADIUS
        vectorDef.cloneParam(BUY_RSI_SIDE, new BinaryParam().setValue(ZERO));
        vectorDef.cloneParam(BUY_RSI_BARS, new IntegerParam(FIVE, fromInt(20)));
        vectorDef.cloneParam(BUY_RSI_RADIUS, new BoundParam(ZERO, fromInt(40), FIVE));

        // SELL BOL TAIL SIZE
        vectorDef.cloneParam(SELL_BOL_TAIL_SIZE_BARS, new BoundParam(ZERO, fromInt(200), fromInt(10)));
        vectorDef.cloneParam(SELL_BOL_TAIL_SIZE_SIZE, new IntegerParam(ZERO, fromInt(200)));
        vectorDef.cloneParam(SELL_BOL_TAIL_SIZE_BOL_TIMES, new BoundParam(ONE + P10, TWO + HALF, EN1));
        // SELL TAIL DEPTH
        vectorDef.cloneParam(SELL_TAIL_DEPTH_BARS, new BoundParam(fromInt(20), fromInt(200), fromInt(10)));
        vectorDef.cloneParam(SELL_TAIL_DEPTH_SIZE, new IntegerParam(ONE, TEN));
        vectorDef.cloneParam(SELL_TAIL_DEPTH_BOL_TIMES, new BoundParam(ONE + P10, TWO + HALF, EN1));
        // SELL RSI RADIUS
        vectorDef.cloneParam(SELL_RSI_SIDE, new BinaryParam().setValue(ZERO));
        vectorDef.cloneParam(SELL_RSI_BARS, new IntegerParam(FIVE, fromInt(20)));
        vectorDef.cloneParam(SELL_RSI_RADIUS, new BoundParam(ZERO, fromInt(40), FIVE));

        // STOP LOSS AT BOL
        vectorDef.cloneParam(STOP_LOSS_BOL_BARS, new BoundParam(ZERO, fromInt(10000), fromInt(10)));
        vectorDef.cloneParam(STOP_LOSS_BOL_TIMES, new BoundParam(ONE + HALF, TWO + HALF, EN1));
        // STOP LOSS AT MA
        vectorDef.cloneParam(STOP_LOSS_MA_BARS, new BoundParam(ZERO, fromInt(10000), fromInt(10)));

        return vectorDef;
    }


}