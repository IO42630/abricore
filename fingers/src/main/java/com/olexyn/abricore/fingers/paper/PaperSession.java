package com.olexyn.abricore.fingers.paper;

import com.olexyn.abricore.fingers.Session;
import org.openqa.selenium.WebDriver;

import java.util.Map;

public class PaperSession extends Session {

    @Override
    protected Map<String, String> fetchCredentials() {
        return null;
    }

    @Override
    public WebDriver doLogin() {
        return null;
    }




}
