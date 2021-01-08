package com.olexyn.abricore.fingers;

public class StoreData {

    private static StoreData instance = null;

    private StoreData() {}

    public static synchronized StoreData getInstance() {
        if (instance == null) {
            instance = new StoreData();
        }
        return instance;
    }


    public boolean store() {

        return false;
    }

    void save(){
        String name = "name";
        String start = "start";
        String end = "end";
        String increment = "increment";
        String fileName = name + "_" + start + "_" + end + "_" + increment + ".csv";
        String path = System.getProperty("user.home") + "/docs/swissquote/quotes/" + fileName;
    }
}
