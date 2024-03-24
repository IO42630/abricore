package com.olexyn.abricore.navi.sq;

import com.olexyn.abricore.navi.Session;
import com.olexyn.abricore.navi.TabPurpose;
import com.olexyn.abricore.navi.mwatch.MWatch;
import com.olexyn.abricore.navi.mwatch.MWatchable;
import com.olexyn.abricore.util.log.LogU;
import com.olexyn.propconf.PropConf;
import com.olexyn.tabdriver.TabDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
            td.newTab(TabPurpose.SQ_SESSION.name());
            td.get("https://trade.swissquote.ch");
            TabDriver.sleep(1000);
            td.findByCss("dbutiv[class='SmartL3']").ifPresent(WebElement::click);


            td.findElement(By.name("username")).sendKeys(CREDENTIALS.get("user"));
            TabDriver.sleep(1000);
            td.findElement(By.name("password")).sendKeys(CREDENTIALS.get("pwd"));
            TabDriver.sleep(1000);
            td.findElement(By.id("loginText")).click();
            TabDriver.sleep(1000);
            try {
                var mobileConfirm = td.findByCss("div[class='SmartL3']");
                if (Objects.nonNull(mobileConfirm)) {
                    td.findByCss("div[class='SmartL3__l3CodeAuthContainer']").ifPresent(WebElement::click);
                }
            } catch (Exception e) {
                // NOP
            }
            try {
                var confirmPwdPage = td.findByCss("div[class='Confirm-password']");
                if (Objects.nonNull(confirmPwdPage)) {
                    td.findElement(By.id("password")).sendKeys(CREDENTIALS.get("pwd"));
                    td.findElement(By.className("Button--primary")).click();
                }
            } catch (Exception e) {
                // NOP
            }

            String key = td.findByCss("[class*='L3Code__l3Element']").orElseThrow().getText();
            TabDriver.sleep(1000);
            td.findByCss("[class*='-L3Code__l3Input']").orElseThrow().sendKeys(CREDENTIALS.get(key));
            TabDriver.sleep(1000);
            td.findByCss("[class*='-L3Code__l3Button']").orElseThrow().click();
            TabDriver.sleep(1000);
            MWatch.setAlive(SqSession.class);
        }
    }

    @Override
    protected Map<String, String> fetchCredentials() {
        var extractedMap = extractCredentialMap(PropConf.get("cred.sq"));
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
