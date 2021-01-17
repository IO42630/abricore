package com.olexyn.abricore.evaluate;

import com.olexyn.abricore.model.Asset;

public class Evaluator {


    public Decision evaluate(Asset asset) {
        return new Decision(Action.HOLD, 1.0);
    }




}
