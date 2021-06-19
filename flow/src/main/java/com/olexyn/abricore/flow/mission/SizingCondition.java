package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.util.ANum;

@FunctionalInterface
public interface SizingCondition  {


    ANum sizeAmount(ANum totalAmount);
}
