package com.olexyn.abricore.navi;

import com.olexyn.abricore.navi.mwatch.MWatch;
import com.olexyn.abricore.navi.mwatch.MWatchable;
import com.olexyn.abricore.util.Constants;
import com.olexyn.abricore.util.Property;
import com.olexyn.abricore.util.log.LogU;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.olexyn.abricore.navi.TabPurpose.SQ_SESSION;
import static com.olexyn.abricore.navi.TabPurpose.TW_SESSION;
import static com.olexyn.abricore.util.Constants.WORKING_DIR;

@Lazy
@Service
public class TabDriver extends ChromeDriver implements MWatchable, JavascriptExecutor {

    private static final String ABOUT_BLANK = "about:blank";
    private static final String CHROME_DRIVER = "chromedriver_119";
    private final Map<String, Tab> tabs = new HashMap<>();

    private static final ChromeDriverService SERVICE;
    private static final DesiredCapabilities CAP;

    static {
        var path = Path.of(Property.get(WORKING_DIR), "/navi/src/main/resources/", CHROME_DRIVER);
        SERVICE = new ChromeDriverService.Builder()
            .usingDriverExecutable(path.toFile())
            .usingAnyFreePort()
            .build();

        CAP = DesiredCapabilities.chrome();
        CAP.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        if (Property.is("headless")) {
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--headless");
        }
        // see also https://chromium.googlesource.com/chromium/src/+/master/chrome/common/pref_names.cc
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", Property.get("quotes.dir.tmp"));
        chromePrefs.put("download.prompt_for_download", false);
        options.setExperimentalOption("prefs", chromePrefs);
        CAP.setCapability(ChromeOptions.CAPABILITY, options);
    }

    public TabDriver() {
        super(SERVICE, CAP);
        MWatch.setAlive(TabDriver.class);
        manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    public synchronized void registerCurrentTab(TabPurpose purpose) {
        Tab tab = new Tab(getWindowHandle());
        tab.setName(getTitle());
        tab.setUrl(getCurrentUrl());
        tab.setPurpose(purpose);
        tabs.put(tab.getHandle(), tab);
    }

    public synchronized Tab getCurrentTab() {
        return tabs.get(getWindowHandle());
    }

    public synchronized List<Tab> getTabByPurpose(TabPurpose purpose) {
        return tabs.values().stream()
            .filter(x -> x.getPurpose() == purpose)
            .toList();
    }

    public synchronized @Nullable String registerBlankTab(TabPurpose purpose) {
        Set<String> openTabHandles = getWindowHandles();
        for (String openTabHandle : openTabHandles) {
            if (!tabs.containsKey(openTabHandle)) {
                Tab blankTab = new Tab(openTabHandle);
                blankTab.setName(ABOUT_BLANK);
                blankTab.setUrl(ABOUT_BLANK);
                blankTab.setPurpose(purpose);
                tabs.put(openTabHandle, blankTab);
                return openTabHandle;
            }
        }
        LogU.warnPlain("Not unregistered tab found.");
        return null;
    }

    public synchronized String registerExistingTab(TabPurpose purpose) {
        String handle = getWindowHandle();
        Tab tab = new Tab(handle);
        tab.setName(getTitle());
        tab.setUrl(getCurrentUrl());
        tab.setPurpose(purpose);
        tabs.put(handle, tab);
        return handle;
    }

    public synchronized void switchToTab(String handle) {
        for (Map.Entry<String, Tab> entry : tabs.entrySet()) {
            String tabHandle = entry.getKey();
            if (tabHandle.equals(handle)) {
                switchTo().window(tabHandle);
            }
        }
    }

    public synchronized void switchToTab(Tab tab) {
        switchTo().window(tab.getHandle());
    }

    /**
     * Opens a new tab, and "moves" the WebDriver to the new tab.
     * If the current tab is empty, it is registered - this happens usually only with the initial tab of the session.
     */
    public synchronized void newTab(TabPurpose purpose) {
        String currentUrl = getCurrentUrl();
        if (currentUrl.isEmpty()
            || currentUrl.equals("data:,")
            || currentUrl.equals(ABOUT_BLANK)) {
            registerExistingTab(purpose);
        } else {
            executeScript("window.open(arguments[0])");
            Optional.ofNullable(registerBlankTab(purpose))
                .ifPresent(this::switchToTab);
        }
    }

    public synchronized void switchToTab(TabPurpose purpose) {
        List<Tab> existingTabs = getTabByPurpose(purpose);
        if (!existingTabs.isEmpty()) {
            switchToTab(existingTabs.get(0));
        } else {
            Tab currentTab = getCurrentTab();
            if (currentTab.getPurpose() == TW_SESSION || currentTab.getPurpose() == SQ_SESSION) {
                currentTab.setPurpose(purpose);
            } else {
                newTab(purpose);
            }
        }
    }

    public synchronized void refresh() {
        navigate().refresh();
    }

    @Override
    public synchronized void get(String url) {
        super.get(url);
    }

    @Override
    public synchronized WebElement findElement(By by) {
        return super.findElement(by);
    }

    public synchronized void executeScript(String script) {
        ((JavascriptExecutor) this).executeScript(script);
    }

    public synchronized void sendDeleteKeys(WebElement element, int n) {
        for (int i = 0; i < n; i++) {
            element.sendKeys(Keys.BACK_SPACE);
        }
    }

    public synchronized @Nullable WebElement filterElementListBy(List<WebElement> list, CRITERIA criteria, String text) {
        for (WebElement element : list) {
            String toEvaluate = switch (criteria) {
                case CLASS -> element.getClass().getName();
                case TEXT -> element.getText();
                case TAG -> element.getTagName();
                case HREF -> element.getAttribute("href");
                case ID -> element.getAttribute("id");
                case TITLE -> element.getAttribute("title");
                case NONE -> text;
            };
            if (toEvaluate != null && toEvaluate.contains(text)) { return element; }
        }
        return null;
    }

    private static final String FRAME_ID_DEFAULT_CONTENT = "defaultContent";
    private static final String FRAME_ID_NONE_FOUND = "noneFound";

    /**
     * Collects all frames accessible to WebDriver.
     */
    public synchronized Map<String, String> collectAllFrames() throws NoSuchFrameException {
        Map<String, String> mapOfCollectedSources = new HashMap<>();
        switchTo().defaultContent();
        mapOfCollectedSources.put(FRAME_ID_DEFAULT_CONTENT, getPageSource());
        for (int i = 0; i < 10; i++) {
            try {
                switchTo().defaultContent();
                switchTo().frame(i);
                mapOfCollectedSources.put(String.valueOf(i), getPageSource());
            } catch (NoSuchFrameException e) {
                return mapOfCollectedSources;
            }
        }
        return mapOfCollectedSources;
    }

    public synchronized String findFrameContainingCharSeq(Map<String, String> mapOfCollectedSources, String string) {
        for (Entry<String, String> entry : mapOfCollectedSources.entrySet()) {
            if (entry.getValue().contains(string)) {
                return entry.getKey();
            }
        }
        return FRAME_ID_NONE_FOUND;
    }

    public enum CRITERIA {
        CLASS,
        TEXT,
        TAG,
        HREF,
        NONE,
        ID,
        TITLE
    }

    public synchronized void switchToFrameContainingCharSeq(String charSeq) {
        switchTo().defaultContent();
        sleep(500);
        final String frameId = findFrameContainingCharSeq(collectAllFrames(), charSeq);
        sleep(400);
        switch (frameId) {
            case FRAME_ID_DEFAULT_CONTENT:
                switchTo().defaultContent();
                break;
            case FRAME_ID_NONE_FOUND:
                break;
            default:
                switchTo().frame(Integer.parseInt(frameId));
        }
    }

    public static void sleep(long milli) {
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            LogU.warnPlain("SLEEP was INTERRUPED.");
        }
    }

    /**
     * Return the first occurrence of specified class that has specified label.
     */
    public synchronized @Nullable WebElement getWhereClassName(String className, CRITERIA criteria, String text) {
        switchToFrameContainingCharSeq(text);
        List<WebElement> elements = findElements(By.className(className));
        return filterElementListBy(elements, criteria, text);
    }

    public synchronized @Nullable WebElement getWhereClassName(String className) {
        switchToFrameContainingCharSeq(className);
        List<WebElement> elements = findElements(By.className(className));
        return filterElementListBy(elements, CRITERIA.NONE, Constants.EMPTY);
    }

    public synchronized void followContainedLink(WebElement element) {
        String link = element.getAttribute("href");
        if (link != null) { navigate().to(link); }
    }

    public synchronized void setRadio(By by, boolean checked) {
        ((JavascriptExecutor) this).executeScript("arguments[0].checked = " + checked + ';', findElement(by));
    }

    public synchronized void setComboByDataValue(By comboBy, String dataValue) {
        WebElement combo = findElement(comboBy);
        combo.click();
        combo.findElement(By.cssSelector("li[data-value='" + dataValue + "']")).click();
    }

    public synchronized WebElement findByCss(String css) {
        return findElement(By.cssSelector(css));
    }

    /**
     * Any-Match.
     */
    public synchronized WebElement getByFieldValue(String type, String field, String value) {
        return findElement(By.cssSelector(type + '[' + field + "*='" + value + "']"));
    }

    public synchronized void clickByFieldValue(String type, String field, String value) {
        var we = getByFieldValue(type, field, value);
        click(we);
    }

    /**
     * @param field can contain wildcards like "class*" or "class^"
     * @param value can contain partial matches like "last-"
     */
    public synchronized WebElement getByFieldValue(SearchContext searchContext, String type, String field, String value) {
        return searchContext.findElement(By.cssSelector(type + '[' + field + "='" + value + "']"));
    }

    public synchronized WebElement getByText(String text) {
        return findElement(By.xpath("//*[contains(text(),'" + text + "')]"));
    }

    /**
     * use this method to click on elements that are not clickable/interactable.
     * if clicking does not work, also try form.submit()
     */
    public synchronized void click(WebElement we) {
        executeScript("arguments[0].click();", we);
    }

}
