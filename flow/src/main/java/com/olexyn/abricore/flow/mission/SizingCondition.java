package com.olexyn.abricore.flow.mission;

@FunctionalInterface
public interface SizingCondition  {


    Long sizeAmount(Long totalAmount);
}
