package ru.ifmo.lab3;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests {
    private static WebDriver driver;

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.gecko.driver", "C:/SeleniumDrivers/geckodriver.exe");
        System.setProperty("webdriver.chrome.driver", "C:/SeleniumDrivers/chromedriver.exe");
        driver = new FirefoxDriver();
        // driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    void loginLogoutTest() {
        AccountController.login(driver);
        assertTrue(driver.getCurrentUrl().startsWith("https://www.ucoz.ru/createsite"));

        driver.findElement(By.xpath("//div[@class='nav-button']/a")).click();
        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//nav[contains(@class, 'main-nav') and contains(@class, 'pushy-open')]")
        ));

        driver.findElement(By.xpath("//a[text()='Выход']")).click();
        assertTrue(driver.getCurrentUrl().startsWith("https://www.ucoz.ru"));
    }

    @Test
    void loginIncorrectEmailTest() {
        tryLogin("lalala", "nanana");

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='form-error']")
        ));
    }

    @Test
    void loginIncorrectPasswordTest() {
        tryLogin(Constants.TEST_ACCOUNT_EMAIL, "notmypassword");

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='form-error']")
        ));
    }

    @Test
    void createWebsiteTest() throws AWTException {
        AccountController.createWebsite(driver);
        //if WebDriverWait on createWebsite throw exception then test fail
    }

    void tryLogin(String email, String password) {
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
                .sendKeys(email);
        driver.findElement(By.xpath("//div[@class='uid-form-field']/input[@type='password']"))
                .sendKeys(password);
        driver.findElement(By.xpath("//form[@class='uid-form']//input[@class='uid-form-submit']"))
                .click();
    }

    @Test
    void adminPanelLoginIncorrectTest() {
        driver.get("http://test834bda6dfa.do.am/admin");

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='password']")
        ));
        driver.findElement(By.xpath("//input[@type='password']"))
                .sendKeys("fakepassword");
        driver.findElement(By.xpath("//button[text()='Вход']"))
                .click();

        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[@class='myWinError']")
        ));

        String errorText = driver.findElement(By.xpath("//span[@class='myWinError']")).getText();
        assertTrue(
                errorText.equals("Неправильный логин или пароль") ||
                        errorText.equals("Неправильный код безопасности"));
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
