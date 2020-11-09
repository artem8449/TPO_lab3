package ru.ifmo.lab3;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests {
    private static WebDriver driver;

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.gecko.driver", "C:/SeleniumDrivers/geckodriver.exe");
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
    }

    @Test
    void loginLogoutTest() {
        login();
        assertTrue(driver.getCurrentUrl().startsWith("https://www.ucoz.ru/createsite"));

        driver.findElement(By.xpath("//div[@class='nav-button']/a")).click();
        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//nav[contains(@class, 'main-nav') and contains(@class, 'pushy-open')]")
        ));

        driver.findElement(By.xpath("//a[text()='Выход']")).click();
        assertTrue(driver.getCurrentUrl().startsWith("https://www.ucoz.ru"));
    }

    @Test
    void createWebsiteTest() throws AWTException {
        login();

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='create-form']")
        ));

        String websiteName = "test" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        driver.findElement(By.xpath("//div[@class='create-form']/form//input[@name='addr']"))
                .sendKeys(websiteName);

        driver.findElement(By.xpath("//div[@class='SumoSelect']")).click();
        new WebDriverWait(driver, 1).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//ul[@class='options']")
        ));
        List<WebElement> elements = driver.findElements(By.xpath("//ul[@class='options']/li"));
        int index = new Random().nextInt(elements.size());

        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_DOWN);

        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOf(
                elements.get(index)
        ));

        robot.keyRelease(KeyEvent.VK_DOWN);

        elements.get(index).click();

        driver.findElement(By.xpath("//div[@class='check-agree']/label/span")).click();
        driver.findElement(By.xpath("//div[@class='create-form']/form/button")).click();

        new WebDriverWait(driver, 5).until(
                ExpectedConditions.urlContains(websiteName)
        );
        
    }

    void login() {
        driver.get("https://www.ucoz.ru/");
        driver.findElement(By.xpath("//div[@class='login-logout']/a")).click();

        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//section[contains(@class, 'user-side') and contains(@class, 'pushy-open')]")
        ));

        driver.findElement(By.xpath("//div[@class='uid-login']/a[@class='uid-btn']")).click();

        String oldHandle = driver.getWindowHandle();
        Object[] handles = driver.getWindowHandles().toArray();
        String newHandle = oldHandle.equals(handles[0]) ? (String)handles[1] : (String)handles[0];

        driver.switchTo().window(newHandle);

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='uid-form-field']/input[@name='email']")
        ));

        driver.findElement(By.xpath("//div[@class='uid-form-field']/input[@name='email']"))
                .sendKeys("artem844499@yandex.ru");
        driver.findElement(By.xpath("//div[@class='uid-form-field']/input[@type='password']"))
                .sendKeys("Fhntv8449");
        driver.findElement(By.xpath("//form[@class='uid-form']//input[@class='uid-form-submit']"))
                .click();

        driver.switchTo().window(oldHandle);

        new WebDriverWait(driver, 5).until(
                ExpectedConditions.urlContains("https://www.ucoz.ru/createsite")
        );

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[text()='Создание нового сайта']"))
        );
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
