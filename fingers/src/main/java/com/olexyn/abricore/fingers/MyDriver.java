package com.olexyn.abricore.fingers;

import org.openqa.selenium.chrome.ChromeDriver;

public class MyDriver extends ChromeDriver {

    private static MyDriver instance = null;

    private MyDriver(){}

    public static synchronized MyDriver getInstance(){
        if (instance == null) instance = new MyDriver();
        return instance;
    }



}
