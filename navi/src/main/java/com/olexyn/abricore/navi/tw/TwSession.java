package com.olexyn.abricore.navi.tw;

import com.olexyn.abricore.navi.Session;
import com.olexyn.abricore.navi.TabPurpose;
import com.olexyn.min.log.LogU;
import com.olexyn.propconf.PropConf;
import com.olexyn.tabdriver.TabDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

import static com.olexyn.abricore.navi.tw.TwUrls.TW_SIGNIN;

public abstract class TwSession extends Session {


    private static final String PREFIX = "tw_iol_";
    private static final String USER = "user";
    private static final String PWD = "pwd";

    TwSession(TabDriver td) {
        super(td);
    }

    @Override
    public void doLogin() {
        if (isLoggedIn()) { return; }

        synchronized(td) {
            LogU.infoStart("new Tw Session.");
            Map<String, String> credentials = fetchCredentials();
            td.newTab(TabPurpose.TW_SESSION.name());
            td.get(TW_SIGNIN);
            td.findByCss("button[class*='email']").orElseThrow().click();
            TabDriver.sleep(500);
            td.findByCss("input[name*='username']").orElseThrow().sendKeys(credentials.get("user"));
            td.findByCss("input[name*='password']").orElseThrow().sendKeys(credentials.get("pwd"));
            td.findByCss("form[action*='sign']").orElseThrow().submit();
            TabDriver.sleep(1000);
            td.findByCss("button.tv-button#email-signin").ifPresent(WebElement::click);
            setLoggedIn(true);
        }
    }

    @Override
    protected Map<String, String> fetchCredentials() {
        var extractedMap = extractCredentialMap(PropConf.get("cred.tw"));
        Map<String, String> credentialMap = new HashMap<>();
        credentialMap.put(USER, extractedMap.get(PREFIX + USER));
        credentialMap.put(PWD, extractedMap.get(PREFIX + PWD));
        return credentialMap;
    }

}
