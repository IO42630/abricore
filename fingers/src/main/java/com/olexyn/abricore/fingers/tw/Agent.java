package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.StoreData;

public class Agent {

    final StoreData storeData = StoreData.getInstance();

    void start() throws InterruptedException {

        Login login = new Login();
        login.login();

        Fetch fetch = new Fetch();
        while (true) {
            fetch.fetch();
            fetch.transform();
            storeData.store();
            Thread.sleep(100);
        }
    }
}
