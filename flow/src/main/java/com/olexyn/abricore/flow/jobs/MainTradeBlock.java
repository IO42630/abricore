package com.olexyn.abricore.flow.jobs;

import com.olexyn.abricore.model.runtime.TradeDto;

public interface MainTradeBlock {


    void run();

    void tryToPlaceOrders();

    void tryToPlaceBuyOrders();

    void tryToPlaceSellOrders();

    void placeBuyOrder(TradeDto trade)  ;

    void placeSellOrder(TradeDto trade)  ;

}
