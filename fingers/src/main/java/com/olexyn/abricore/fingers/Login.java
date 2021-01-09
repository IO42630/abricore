package com.olexyn.abricore.fingers;

import com.olexyn.abricore.fingers.sq.SleepFactory;
import com.olexyn.abricore.fingers.sq.Tools;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public abstract class Login {

    protected WebDriver driver;

    protected Login() {
        this.driver = init();
    }

    protected WebDriver init() {
        try {
            String path = App.class.getClassLoader().getResource("chromedriver_83").getPath();
            System.setProperty("webdriver.chrome.driver", path);
        } catch (NullPointerException ignored){}

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    protected boolean cleanup(WebDriver driver){
        if(driver !=null)
            driver.quit();
        return true;
    }

    protected Map<String,String> extractCredentialMap(String cred_path) {
        Map<String,String> extractedMap = new HashMap<>();

        String path = System.getProperty("user.home") + cred_path;
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
        return extractedMap;
    }

    protected  abstract Map<String,String> fetchCredentials();

    public abstract WebDriver doLogin();
    public abstract  boolean doLogout(WebDriver webDriver);




    private static final String FRAME_ID_DEFAULT_CONTENT = "defaultContent";
    private static final String FRAME_ID_NONE_FOUND = "noneFound";


    protected Map<String,String> collectAllFrames(WebDriver driver) throws NoSuchFrameException {
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

    protected String findFrameContainingCharSeq(Map<String,String> mapOfCollectedSources , String string) {
        for (Entry<String, String> entry : mapOfCollectedSources.entrySet()) {
            if (entry.getValue().contains(string)) {
                return entry.getKey();
            }
        }
        return FRAME_ID_NONE_FOUND;
    }

    protected enum CRITERIA{
        CLASS,
        TEXT,
        TAG,
        HREF,
        NONE
    }

    protected WebElement filterElementListBy(List<WebElement> list, CRITERIA criteria, String text) {
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

    protected void switchToFrameContainingCharSeq(String charSeq) {
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
    protected WebElement getWhere(String className, String labelText) {
        switchToFrameContainingCharSeq(labelText);
        List<WebElement> elements = driver.findElements(By.className(className));
        return filterElementListBy(elements, CRITERIA.TEXT, labelText);
    }

    protected WebElement getWhere(String className) {
        switchToFrameContainingCharSeq(className);
        List<WebElement> elements = driver.findElements(By.className(className));
        return filterElementListBy(elements, CRITERIA.NONE, "");
    }

    protected void followContainedLink(WebDriver driver, WebElement element) {
        String link = element.getAttribute("href");
        if (link != null) driver.navigate().to(link);
    }
}
