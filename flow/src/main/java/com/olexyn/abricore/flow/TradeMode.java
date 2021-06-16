package com.olexyn.abricore.flow;

import com.olexyn.abricore.fingers.sq.SqNavigator;
import com.olexyn.abricore.fingers.sq.SqLogin;
import com.olexyn.abricore.fingers.sq.enums.Exchange;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.util.enums.Currency;

import java.util.ArrayList;
import java.util.List;

public class TradeMode extends AbstractMode {

    ActionMode actionMode = ActionMode.LIVE;

    @Override
    void start() throws InterruptedException {

        retrieveStoredData();

        while (true) {
            fetchLiveData();
            consultRules();
            initializeAction();
            Thread.sleep(100);
        }
    }

    @Override
    void retrieveStoredData() {

    }

    void fetchLiveData() {
        SqNavigator navigator = new SqNavigator(new SqLogin().doLogin());
        //navigator.search("XAG");
        navigator.tradeWindow("CH1111950643", Currency.CHF, Exchange.SDOTS);

        List<AssetSnapshot> snapshotList = new ArrayList<>();
        while(true) {
            snapshotList.add(navigator.resolveQuote(null, null));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            navigator.refresh();
        }


    }

    @Override
    void consultRules() {

    }



}
