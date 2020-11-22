package ru.ifmo.lab3;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class AccountController {
    public static void login(WebDriver driver) {
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
                .sendKeys(Constants.TEST_ACCOUNT_EMAIL);
        driver.findElement(By.xpath("//div[@class='uid-form-field']/input[@type='password']"))
                .sendKeys(Constants.TEST_ACCOUNT_PASSWORD);
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

    public static boolean checkIsLoggedIn(WebDriver driver) {
        driver.get("https://www.ucoz.ru/createsite");

        try {
            new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[text()='Создание нового сайта']")
            ));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static void createWebsite(WebDriver driver) throws AWTException {
        if (!checkIsLoggedIn(driver))
            login(driver);
        if (!driver.getCurrentUrl().startsWith("https://www.ucoz.ru/createsite"))
            driver.get("https://www.ucoz.ru/createsite");

        String websiteName = "test" + RandomString.get();
        fillCreateForm(driver, websiteName);
        clickThroughFirstLogin(driver);
    }

    private static void fillCreateForm(WebDriver driver, String websiteName) throws AWTException {
        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='create-form']")
        ));

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

    private static void clickThroughFirstLogin(WebDriver driver) {
        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(), 'Это ваш первый вход в систему')]")
        ));
        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Продолжить']")
        ));
        driver.findElement(By.xpath("//button[text()='Продолжить']"))
                .click();


        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[contains(text(), 'Выберите необходимые вашему сайту ')]")
        ));
        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[text()='Продолжить']")
        ));
        driver.findElement(By.xpath("//button[text()='Продолжить']"))
                .click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.urlMatches(".*/panel.*"));
    }

    public static String getAnyCreatedWebsite(WebDriver driver) {
        int websiteId = 3;

        if (!driver.getCurrentUrl().matches("https://www.ucoz.ru/createsite"))
            driver.get("https://www.ucoz.ru/createsite");

        List<WebElement> links = driver.findElements(By.xpath("//div[@id='mysites-list']/a"));
        if (links.size() == 0)
            return null;
        if (links.size() <= websiteId)
            websiteId = 0;

        return links.get(websiteId).getAttribute("href");
    }

    public static void websiteAdminLogin(WebDriver driver, String url) {
        driver.get(url);
        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//td/b[text()='Вход в панель управления']")
        ));

        driver.findElement(By.xpath("//input[@type='password']"))
                .sendKeys(Constants.TEST_WEBSITE_PASSWORD);
        driver.findElement(By.xpath("//button[text()='Вход']"))
                .click();

        try {
            new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Это ваш первый вход в систему')]")
            ));
            clickThroughFirstLogin(driver);
        }
        catch (Exception ignored) {}
    }

}
