package com.olexyn.abricore.fingers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App
{


    private WebDriver driver;

    @BeforeClass
    public void init(){

        try {
            String path = App.class.getClassLoader().getResource("chromedriver_83").getPath();
            System.setProperty("webdriver.chrome.driver", path);
        } catch (NullPointerException ignored){}

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test(testName = "downloadCharts")
    public void tstHelloWorldGoogle() throws InterruptedException {
        // Step 1 TwLogin

        driver.get("https://www.tradingview.com/symbols/AMEX-UNG/");
        WebElement fullChartButton = driver.findElement(By.linkText("Full-featured chart"));
        fullChartButton.click();
        WebElement topLeftButton = driver.findElement(By.className("topLeftButton"));
        topLeftButton.click();
        Thread.sleep(50000);
        // while(true) {
        //     WebElement price = driver.findElement(By.className("tv-symbol-price-quote__value"));
        //     System.out.println(price.getText());
        //     //Deliberately adding pause
        //     Thread.sleep(5000);
        // }

    }

    @AfterClass
    public void cleanup(){
        if(driver !=null)
            driver.quit();
    }
}
