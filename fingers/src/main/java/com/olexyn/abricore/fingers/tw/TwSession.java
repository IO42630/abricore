package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.TabPurpose;
import com.olexyn.abricore.fingers.SleepFactory;
import org.openqa.selenium.By;

import java.util.HashMap;
import java.util.Map;

public class TwSession extends Session {

    private static boolean active = false;

    private static final String PREFIX = "tw_iol_";
    private static final String USER = "user";
    private static final String PWD = "pwd";
    private static final String CRED_PATH = "/docs/abricore/tw-credentials.json";


    public static void doLogin() {
        if (active) {
            return;
        }

        Map<String,String> credentials = fetchCredentials();
        synchronized (Session.class) {
            newTab(TabPurpose.TW_SESSION);
            DRIVER.get("https://www.tradingview.com/#signin");

            DRIVER.findElement(By.className("tv-signin-dialog__toggle-email")).click();

            DRIVER.findElement(By.name("username")).sendKeys(credentials.get("user"));
            SleepFactory.sleep(1);
            DRIVER.findElement(By.name("password")).sendKeys(credentials.get("pwd"));
            SleepFactory.sleep(1);
            Session.getWhere("tv-button", CRITERIA.ID, "email-signin__submit-button").click();
        }
        active = true;
    }




    protected static Map<String, String> fetchCredentials() {
        Map<String,String> extractedMap = extractCredentialMap(CRED_PATH);
        Map<String,String> credentialMap = new HashMap<>();

        credentialMap.put(USER, extractedMap.get(PREFIX + USER));
        credentialMap.put(PWD, extractedMap.get(PREFIX + PWD));
        return credentialMap;
    }

}
