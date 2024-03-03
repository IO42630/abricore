package com.olexyn.abricore.navi.tw;

import com.olexyn.abricore.navi.Session;
import com.olexyn.abricore.navi.TabDriver;
import com.olexyn.abricore.navi.TabPurpose;
import com.olexyn.abricore.navi.mwatch.MWatch;
import com.olexyn.abricore.navi.mwatch.MWatchable;
import com.olexyn.abricore.util.Property;
import com.olexyn.abricore.util.log.LogU;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.olexyn.abricore.navi.tw.TwUrls.TW_SIGNIN;

public abstract class TwSession extends Session implements MWatchable {


    private static final String PREFIX = "tw_iol_";
    private static final String USER = "user";
    private static final String PWD = "pwd";

    TwSession(TabDriver td) {
        super(td);
    }

    @Override
    public void doLogin() {
        synchronized(td) {
            if (MWatch.isAlive(TwSession.class)) { return; }
            LogU.infoStart("new Tw Session.");
            Map<String, String> credentials = fetchCredentials();
            td.newTab(TabPurpose.TW_SESSION);
            td.get(TW_SIGNIN);
            td.findByCss("button[class*='email']").click();
            TabDriver.sleep(500);
            td.findByCss("input[name*='username']").sendKeys(credentials.get("user"));
            td.findByCss("input[name*='password']").sendKeys(credentials.get("pwd"));
            td.findByCss("form[action*='sign']").submit();
            TabDriver.sleep(1000);
            Optional.ofNullable(td.getWhereClassName("tv-button", TabDriver.CRITERIA.ID, "email-signin__submit-button"))
                .ifPresent(WebElement::click);
            MWatch.setAlive(TwSession.class);
        }
    }

    @Override
    protected Map<String, String> fetchCredentials() {
        var extractedMap = extractCredentialMap(Property.get("cred.tw"));
        Map<String, String> credentialMap = new HashMap<>();
        credentialMap.put(USER, extractedMap.get(PREFIX + USER));
        credentialMap.put(PWD, extractedMap.get(PREFIX + PWD));
        return credentialMap;
    }

}
