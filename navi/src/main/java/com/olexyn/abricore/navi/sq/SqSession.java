package com.olexyn.abricore.navi.sq;

import com.olexyn.abricore.navi.Session;
import com.olexyn.abricore.navi.TabDriver;
import com.olexyn.abricore.navi.TabPurpose;
import com.olexyn.abricore.navi.mwatch.MWatch;
import com.olexyn.abricore.navi.mwatch.MWatchable;
import com.olexyn.abricore.util.Property;
import com.olexyn.abricore.util.log.LogU;
import org.openqa.selenium.By;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class SqSession extends Session implements MWatchable {

    private static final String PREFIX = "swiss_iol_";
    private static final String USER = "user";
    private static final String PWD = "pwd";
    protected static final String ACCOUNT_NR = "account_nr";

    protected final Map<String, String> CREDENTIALS;


    public SqSession(TabDriver td) {
        super(td);
        CREDENTIALS = fetchCredentials();
    }

    @Override
    public void doLogin() {
        synchronized(td) {
            if (MWatch.isAlive(SqSession.class)) { return; }
            LogU.infoStart("new Sq Session.");
            td.newTab(TabPurpose.SQ_SESSION);
            td.get("https://www.swissquote.ch/url/login_bank?l=de");
            td.findElement(By.name("username")).sendKeys(CREDENTIALS.get("user"));
            TabDriver.sleep(1000);
            td.findElement(By.name("password")).sendKeys(CREDENTIALS.get("pwd"));
            TabDriver.sleep(1000);
            td.findElement(By.id("loginText")).click();
            TabDriver.sleep(1000);
            try {
                var confirmPwdPage = td.getWhereClassName("Confirm-password");
                if (Objects.nonNull(confirmPwdPage)) {
                    td.findElement(By.id("password")).sendKeys(CREDENTIALS.get("pwd"));
                    td.findElement(By.className("Button--primary")).click();
                }
            } catch (Exception e) {
                // NOP
            }
            var keyHolder = td.getWhereClassName("L3Code__l3Element");
            String key = null;
            if (Objects.nonNull(keyHolder)) {
                key = keyHolder.getText();
            }
            TabDriver.sleep(1000);
            td.findElement(By.className("L3Code__l3Input")).sendKeys(CREDENTIALS.get(key));
            TabDriver.sleep(1000);
            td.findElement(By.className("L3Code__l3Button")).click();
            TabDriver.sleep(1000);
            MWatch.setAlive(SqSession.class);
        }
    }

    @Override
    protected Map<String, String> fetchCredentials() {
        var extractedMap = extractCredentialMap(Property.get("cred.sq"));
        Map<String, String> credentialMap = new HashMap<>();
        credentialMap.put(USER, extractedMap.get(PREFIX + USER));
        credentialMap.put(PWD, extractedMap.get(PREFIX + PWD));
        credentialMap.put(ACCOUNT_NR, extractedMap.get(PREFIX + ACCOUNT_NR));
        String valuePrefix = PREFIX + "l3_";
        for (String key : extractedMap.keySet()) {
            if (key.startsWith(valuePrefix)) {
                credentialMap.put(key.replace(valuePrefix, ""), extractedMap.get(key));
            }
        }
        return credentialMap;
    }

}
