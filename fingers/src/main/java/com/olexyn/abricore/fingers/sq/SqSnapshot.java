package com.olexyn.abricore.fingers.sq;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This represents a Sq fullquote table.
 */
public class SqSnapshot {

    protected LocalDate date;
    protected LocalTime time;
    protected Double last;
    protected Integer volBid;
    protected Integer volAsk;
    protected Double bid;
    protected Double ask;

    protected Boolean isOpen;

}
