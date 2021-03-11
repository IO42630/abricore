package com.olexyn.abricore.session;

@FunctionalInterface
public interface SizingCondition  {


    Long sizeAmount(Long totalAmount);
}
