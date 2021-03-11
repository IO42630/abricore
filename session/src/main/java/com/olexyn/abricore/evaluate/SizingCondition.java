package com.olexyn.abricore.evaluate;

@FunctionalInterface
public interface SizingCondition  {


    Long sizeAmount(Long totalAmount);
}
