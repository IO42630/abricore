package com.olexyn.abricore.fingers;


import com.olexyn.abricore.fingers.sq.SleepFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class.
 */
public class DriverTools {

    public static WebElement filterElementListBy(List<WebElement> list, CRITERIA criteria, String text) {
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
                case ID:
                    toEvaluate = element.getAttribute("id");
                    break;
                case TITLE:
                    toEvaluate = element.getAttribute("title");
                    break;
                case NONE:
                    toEvaluate = text;
            }
            if (toEvaluate != null && toEvaluate.contains(text)) { return element; }
        }
        return  null;
    }

    private static final String FRAME_ID_DEFAULT_CONTENT = "defaultContent";
    private static final String FRAME_ID_NONE_FOUND = "noneFound";

    /**
     * Collects all frames accessible to WebDriver.
     */
    public static Map<String,String> collectAllFrames(WebDriver driver) throws NoSuchFrameException {
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

    public static String findFrameContainingCharSeq(Map<String,String> mapOfCollectedSources , String string) {
        for (Entry<String, String> entry : mapOfCollectedSources.entrySet()) {
            if (entry.getValue().contains(string)) {
                return entry.getKey();
            }
        }
        return FRAME_ID_NONE_FOUND;
    }

    public enum CRITERIA{
        CLASS,
        TEXT,
        TAG,
        HREF,
        NONE,
        ID,
        TITLE
    }



    public static void switchToFrameContainingCharSeq(WebDriver driver, String charSeq) {
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
     */
    public static WebElement getWhere(WebDriver driver, String className, CRITERIA criteria, String text) {
        switchToFrameContainingCharSeq(driver, text);
        List<WebElement> elements = driver.findElements(By.className(className));
        return filterElementListBy(elements, criteria, text);
    }

    public static WebElement getWhere(WebDriver driver, String className) {
        switchToFrameContainingCharSeq(driver, className);
        List<WebElement> elements = driver.findElements(By.className(className));
        return filterElementListBy(elements, CRITERIA.NONE, "");
    }

    public static void followContainedLink(WebDriver driver, WebElement element) {
        String link = element.getAttribute("href");
        if (link != null) driver.navigate().to(link);
    }
}
