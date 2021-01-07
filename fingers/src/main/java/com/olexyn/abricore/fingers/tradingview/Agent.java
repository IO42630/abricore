package com.olexyn.abricore.fingers.tradingview;

public class Agent {


    // Thread start.


    void save(){
        String name = "name";
        String start = "start";
        String end = "end";
        String increment = "increment";
        String fileName = name + "_" + start + "_" + end + "_" + increment + ".csv";
        String path = System.getProperty("user.home") + "/docs/swissquote/quotes/" + fileName;
    }
}
