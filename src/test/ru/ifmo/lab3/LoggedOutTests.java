package ru.ifmo.lab3;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoggedOutTests {
    private static WebDriver driver;

    @BeforeAll
    static void setUp() {
        System.setProperty("webdriver.gecko.driver", "C:/SeleniumDrivers/geckodriver.exe");
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
    }

    @Test
    void leftSidebarTest() {
        driver.get("https://www.ucoz.ru/");

        WebElement menuOpenCloseButton = driver.findElement(By.xpath("//a[@class='dark']"));
        menuOpenCloseButton.click();
        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//nav[contains(@class, 'main-nav') and contains(@class, 'pushy-open')]")
        ));

        menuOpenCloseButton.click();
        new WebDriverWait(driver, 2).until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//nav[contains(@class, 'main-nav') and contains(@class, 'pushy-open')]")
        ));
    }

    @Test
    void languageSwitchTest() {
        driver.get("https://www.ucoz.ru/");

        WebElement languageButton =
                driver.findElement(By.xpath("//div[contains(@class, 'language-menu')]/button"));
        assertEquals("Русский",
                languageButton.findElement(By.xpath("./span[contains(@class, 'filter-option ')]")).getText());

        languageButton.click();
        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'dropdown-menu')]/ul")
        ));

        driver.findElement(By.xpath("//div[contains(@class, 'dropdown-menu')]/ul/li[@class='en']")).click();

        assertTrue(driver.getCurrentUrl().startsWith("https://www.ucoz.com"));
        assertEquals("Choose a website builder that suits your needs:",
                driver.findElement(By.xpath("//h2[@class='slogan']")).getText());
    }

    @Test
    void knowledgeBaseSearchTest() {
        driver.get("https://www.ucoz.ru/help/");
        driver.findElement(By.xpath("//form[@class='search-form']/input[@type='text']")).sendKeys("домен");
        driver.findElement(By.xpath("//form[@class='search-form']/input[@type='submit']")).click();

        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[@class='body-title' and contains(text(), 'Поиск  по запросу')]")
        ));
        assertTrue(driver.findElements(By.xpath("//a[@class='search-link' and contains(text(), 'домен')]")).size() > 0);
    }

    @Test
    void exampleSliderTest() throws AWTException {
        driver.get("https://www.ucoz.ru/success/");

        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_DOWN);

        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(), 'Создали сайт, наполнили его первым')]")
        ));

        robot.keyRelease(KeyEvent.VK_DOWN);

        List<WebElement> contentElements =
                driver.findElements(By.xpath("//section[@id='section1']/div[@class='slidesControl']/div"));

        assertTrue(isOnlyIdDisplayed(contentElements, 0));

        WebElement nextElementButton =
            driver.findElement(By.xpath("//section[@id='section1']//a[contains(@class, 'slidesNext')]"));

        nextElementButton.click();
        assertTrue(isOnlyIdDisplayed(contentElements, 1));

        nextElementButton.click();
        assertTrue(isOnlyIdDisplayed(contentElements, 2));

        nextElementButton.click();
        assertTrue(isOnlyIdDisplayed(contentElements, 3));

        nextElementButton.click();
        assertTrue(isOnlyIdDisplayed(contentElements, 0));
    }

    boolean isOnlyIdDisplayed(List<WebElement> elements, int id) {
        for (int i = 0; i < elements.size(); i++) {
            if (i == id && !elements.get(i).isDisplayed())
                return false;
            if (i != id && elements.get(i).isDisplayed())
                return false;
        }
        return true;
    }

    @Test
    void scrollUpButtonTest() throws AWTException {
        driver.get("https://www.ucoz.ru");

        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_DOWN);

        new WebDriverWait(driver, 2).until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//h2[@class='slogan']")
        ));

        robot.keyRelease(KeyEvent.VK_DOWN);

        driver.findElement(By.xpath("//a[contains(@class, 'anchorLink')]")).click();

        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[@class='slogan']")
        ));
    }

    @Test
    void rightNavTest() {
        driver.get("https://www.ucoz.ru");
        List<WebElement> links =
                driver.findElements(By.xpath("//div[@id='fp-nav']/ul/li/a"));

        links.get(0).click();
        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[@class='slogan']")
        ));

        links.get(2).click();
        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(), 'Интуитивно понятный конструктор')]")
        ));

        links.get(5).click();
        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(), 'Калькуляторы и формы')]")
        ));

        links.get(8).click();
        new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[text()='Профессиональные шаблоны']")
        ));
    }

    @Test
    void landingButtonsTest() {
        driver.get("https://www.ucoz.ru");
        List<WebElement> toVisit =
                driver.findElements(By.xpath("//div[@class='content-section']/div/a"));

        String oldUrl = driver.getCurrentUrl();

        for (WebElement webElement : toVisit) {
            webElement.click();

            Object[] handles = driver.getWindowHandles().toArray();

            driver.switchTo().window((String)handles[1]);
            assertNotEquals(oldUrl, driver.getCurrentUrl());
            driver.close();
            driver.switchTo().window((String)handles[0]);
        }
    }

    //TODO: delete this later or fix
//    @Test
//    void pricingTableTest() {
//        driver.get("https://www.ucoz.ru/pricing/");
//
//        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(
//            By.xpath("//table[@class='u-tariffs-table']")
//        ));
//
//        List<WebElement> rows =
//                driver.findElements(By.xpath("//table[@class='u-tariffs-table']/tbody/tr"));
//
//        for (int i = 0; i < rows.size() - 1; i++) {
//            if (!rows.get(i).isEnabled() || !rows.get(i).isDisplayed())
//                continue;
//            if (!rows.get(i).getAttribute("class").contains("tariffs-info"))
//                continue;
//
//            System.out.println(i);
//            rows.get(i).click();
//            new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOf(
//                    rows.get(i+1)
//            ));
//
//            rows.get(i).click();
//            new WebDriverWait(driver, 2).until(ExpectedConditions.invisibilityOf(
//                    rows.get(i+1)
//            ));
//        }
//    }


    @AfterAll
    static void tearDown() {
        driver.quit();
    }
}