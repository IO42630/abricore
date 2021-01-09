package com.olexyn.abricore.fingers.sq;

import com.olexyn.abricore.fingers.App;
import com.olexyn.abricore.fingers.Login;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;




public class SqLogin implements Login {

    private WebDriver driver;

    public void init(){

        try {
            String path = App.class.getClassLoader().getResource("chromedriver_83").getPath();
            System.setProperty("webdriver.chrome.driver", path);
        } catch (NullPointerException ignored){}

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    public void cleanup(){
        if(driver !=null)
            driver.quit();
    }

    private static final String PREFIX = "swiss_iol_";
    private static final String USER = "user";
    private static final String PWD = "pwd";
    private static final String L3_NR = "current_l3_card_nr";

    private static final String FRAME_ID_DEFAULT_CONTENT = "defaultContent";
    private static final String FRAME_ID_NONE_FOUND = "noneFound";


    Map<String,String> collectAllFrames(WebDriver driver) throws  NoSuchFrameException{
        Map<String, String> mapOfCollectedSources = new HashMap<>();
        driver.switchTo().defaultContent();
        mapOfCollectedSources.put(FRAME_ID_DEFAULT_CONTENT, driver.getPageSource());
        for (int i = 0; i< 10 ; i++) {
            try {
                driver.switchTo().defaultContent();
                driver.switchTo().frame(i);
                mapOfCollectedSources.put(String.valueOf(i), driver.getPageSource());
            } catch (NoSuchFrameException e) {
                return  mapOfCollectedSources;
            }
        }
        return  mapOfCollectedSources;
    }

    String findFrameContainingCharSeq(Map<String,String> mapOfCollectedSources , String string) {
        for (Entry<String, String> entry : mapOfCollectedSources.entrySet()) {
            if (entry.getValue().contains(string)) {
                return entry.getKey();
            }
        }
        return FRAME_ID_NONE_FOUND;
    }

    enum CRITERIA{
        CLASS,
        TEXT,
        TAG,
        HREF,
        NONE
    }

    WebElement filterElementListBy(List<WebElement> list, CRITERIA criteria, String text) {
        for (WebElement element : list) {
            String toEvaluate = null;
            switch (criteria) {
                case CLASS:
                    toEvaluate = element.getClass().getName();
                    break;
                case TEXT:
                    toEvaluate = element.getText();
                    break;
                case TAG:
                    toEvaluate = element.getTagName();
                    break;
                case HREF:
                    toEvaluate = element.getAttribute("href");
                    break;
                case NONE:
                    toEvaluate = text;
            }
            if (toEvaluate != null && toEvaluate.contains(text)) return element;
        }
        return  null;
    }

    void switchToFrameContainingCharSeq(String charSeq) {
        driver.switchTo().defaultContent();
        SleepFactory.sleep(2);
        final String frameId= findFrameContainingCharSeq(collectAllFrames(driver), charSeq);
        SleepFactory.sleep(2);
        switch (frameId) {
            case FRAME_ID_DEFAULT_CONTENT:
                driver.switchTo().defaultContent();
                break;
            case FRAME_ID_NONE_FOUND:
                break;
            default:
                driver.switchTo().frame(Integer.parseInt(frameId));
        }
    }

    /**
     * Return the first occurrence of specified class that has specified label.
     * @param className
     * @param labelText
     */
    WebElement getWhere(String className, String labelText) {
        switchToFrameContainingCharSeq(labelText);
        List<WebElement> elements = driver.findElements(By.className(className));
        return filterElementListBy(elements, CRITERIA.TEXT, labelText);
    }

    WebElement getWhere(String className) {
        switchToFrameContainingCharSeq(className);
        List<WebElement> elements = driver.findElements(By.className(className));
        return filterElementListBy(elements, CRITERIA.NONE, "");
    }

    void followContainedLink(WebDriver driver, WebElement element) {
        String link = element.getAttribute("href");
        if (link != null) driver.navigate().to(link);
    }


    public static void main(String... args) throws InterruptedException {
        SqLogin login = new SqLogin();
        login.init();
        login.doLogin();
        login.cleanup();
    }

    public void  doLogin() throws InterruptedException {
        Map<String,String> credentials = fetchCredentials();
        driver.get("https://www.swissquote.ch/");

        getWhere("mn-Dropdown__trigger", "LOGIN").click();
        followContainedLink(driver, getWhere("mn-Dropdown__text", "Login Bank"));

        driver.findElement(By.name("username")).sendKeys(credentials.get("user"));
        SleepFactory.sleep(1);
        driver.findElement(By.name("password")).sendKeys(credentials.get("pwd"));
        SleepFactory.sleep(1);
        driver.findElement(By.id("loginText")).click();
        SleepFactory.sleep(2);

        WebElement keyHolder = getWhere("L3CodeDialog__challengeCode");
        String key = keyHolder.getText();
        driver.findElement(By.className("js-l3Code L3CodeDialog__l3Code Input Field Field__input")).sendKeys(credentials.get(key));
        driver.findElement(By.className("js-authenticate Button Button--primary")).click();
     }




    Map<String,String> fetchCredentials() {
        Map<String,String> credentialMap = new HashMap<>();
        Map<String,String> extractedMap = new HashMap<>();

        String path = System.getProperty("user.home") + "/docs/swissquote/credentials.json";
        String contents = new Tools().fileToString(new File(path));
        JSONObject obj ;
        try {
            obj = new JSONObject(contents);
            for (String key : obj.keySet()) {
                extractedMap.put(key, obj.getString(key));
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        credentialMap.put(USER, extractedMap.get(PREFIX+USER));
        credentialMap.put(PWD, extractedMap.get(PREFIX + PWD));
        credentialMap.put(L3_NR, extractedMap.get(PREFIX + L3_NR));
        String value_prefix = PREFIX+ "l3_" + credentialMap.get(L3_NR) + "_";

        for (String key : extractedMap.keySet()) {
            if (key.startsWith(value_prefix)) {
                credentialMap.put( key.replace(value_prefix, ""), extractedMap.get(key));
            }
        }
        return credentialMap;
    }

}
