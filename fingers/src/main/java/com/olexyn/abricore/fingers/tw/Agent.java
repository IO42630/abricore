package com.olexyn.abricore.fingers.tw;

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
